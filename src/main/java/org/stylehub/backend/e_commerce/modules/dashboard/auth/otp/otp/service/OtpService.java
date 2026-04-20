package org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.dto.*;
import org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.entity.Otp;
import org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.repository.OtpRepository;
import org.stylehub.backend.e_commerce.platform.mail.EmailService;
import org.stylehub.backend.e_commerce.platform.security.jwt.DashboardTokenService;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int DEFAULT_EXPIRY_MINUTES = 10;
    private static final int MAX_EXPIRY_MINUTES = 30;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final OtpTemplateService otpTemplateService;
    private final EmailService emailService;
    private final DashboardTokenService dashboardTokenService;

    @Transactional
    public GenerateOtpResponse generateOtp(GenerateOtpRequest generateOtpRequest){
        // first check of the generated request dto "fields"
        validateGenrateRequest(generateOtpRequest);

        // second bring the user
        User user =this.userRepository.findByEmail(generateOtpRequest.email())
                .orElseThrow(()-> new IllegalArgumentException("User not found"));

        // because he ask for another otp
        // find the top used otp for the recipient and purpose then filter if he is valid not
        // consumed and not expired then  make it consumed and save it
        otpRepository.findTopByRecipientAndPurposeOrderByCreatedAtDesc(generateOtpRequest.recipient(),
                generateOtpRequest.purpose())
                .filter(existingOtp->!existingOtp.isExpired()&&!existingOtp.isConsumed())
                .ifPresent(otp->{
                    otp.consume();
                    otpRepository.save(otp);
                });
        String otp = generateNumericOtp(6);
        int expiryMinutes =normalizeExpiryMinutes(generateOtpRequest.expiryMinutes());

        Otp newOtp = new Otp();
        newOtp.setOtpHash(hashOtp(otp));
        newOtp.setRecipient(generateOtpRequest.recipient());
        newOtp.setPurpose(generateOtpRequest.purpose());
        newOtp.setUser(user);
        newOtp.setChannel(generateOtpRequest.channel());
        newOtp.setExpiresAt(Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES));
        newOtp.setAttemptCount(0);

        Otp savedOtp= otpRepository.save(newOtp);

        String emailHtml=this.otpTemplateService.buildOtpEmailTemplate(
                generateOtpRequest.recipient(),otp,expiryMinutes
        );
        emailService.sendHtmlEmail(generateOtpRequest.recipient(),"Your StyleHub verification code",
                emailHtml);

        return new GenerateOtpResponse(
                savedOtp.getId(),
                savedOtp.getRecipient(),
                savedOtp.getPurpose().name(),
                savedOtp.getExpiresAt(),
                otp
        );
    }
    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest verifyOtpRequest){
        validateVerifiedOtpRequest(verifyOtpRequest);

       Otp otp = this.otpRepository.findTopByRecipientAndPurposeOrderByCreatedAtDesc(
                verifyOtpRequest.recipient(),verifyOtpRequest.purpose()
        ).orElseThrow(()-> new IllegalArgumentException("Otp not found"));

       if(otp.isExpired()){
           return  VerifyOtpResponse.failed("OTP has expired", 0);       }
       if(otp.isConsumed()){
           return  VerifyOtpResponse.failed("OTP already used", 0);       }
       if(!otp.canAttemptVerification()){
           return  VerifyOtpResponse.failed( "Maximum verification attempts reached", 0);
       }
       String inComingHash=hashOtp(verifyOtpRequest.otpCode());

       if(inComingHash.equals(otp.getOtpHash())){
           otp.consume();
          otpRepository.save(otp);
          DashboardTokenService.TokenPair tokenPair = dashboardTokenService.generateTokenPair(otp.getUser());
           return new VerifyOtpResponse(true,
                   "OTP verified",
                   otp.getMaxAttempts()-otp.getAttemptCount(),
                   tokenPair.accessToken(),
                   tokenPair.refreshToken());
       }
       otp.recordFailedAttempt();
       otpRepository.save(otp);
       int remainingAttempts = Math.max(otp.getMaxAttempts()-otp.getAttemptCount(),0);

        return VerifyOtpResponse.failed("Invalid OTP code", remainingAttempts);
    }
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        DashboardTokenService.TokenPair tokenPair = dashboardTokenService.refresh(request.refreshToken());
        return new RefreshTokenResponse(tokenPair.accessToken(), tokenPair.refreshToken());
    }

    private void validateVerifiedOtpRequest(VerifyOtpRequest verifyOtpRequest) {
        if(verifyOtpRequest.otpCode()==null){
            throw new IllegalArgumentException("OTP code is required");
        }
        if(verifyOtpRequest.purpose()==null){
            throw new IllegalArgumentException("Purpose code is required");
        }
        if(verifyOtpRequest.recipient()==null){
            throw new IllegalArgumentException("Recipient code is required");
        }
    }

    private String hashOtp(String otp) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashed=messageDigest.digest(otp.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        }catch (NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 not available", e);
        }

    }

    private int normalizeExpiryMinutes(Integer expiryMinutes) {
        if(expiryMinutes==null||expiryMinutes<0){
            return DEFAULT_EXPIRY_MINUTES;
        }
        return Math.min(MAX_EXPIRY_MINUTES,expiryMinutes);
    }

    private String generateNumericOtp(int digits) {
        int min = (int) Math.pow(10,digits-1);
        int max = (int) (Math.pow(10,digits)-1);
        int value = SECURE_RANDOM.nextInt(max-min+1)+min;
        return String.valueOf(value);
    }

    private void validateGenrateRequest(GenerateOtpRequest generateOtpRequest) {
        if(generateOtpRequest.email()==null){
            throw new IllegalArgumentException("email is required");
        }
        if(generateOtpRequest.channel()==null){
            throw new IllegalArgumentException("channel is required");
        }
        if(generateOtpRequest.recipient()==null){
            throw new IllegalArgumentException("recipient is required");
        }
        if(generateOtpRequest.purpose()==null){
            throw new IllegalArgumentException("purpose is required");
        }

    }
}
