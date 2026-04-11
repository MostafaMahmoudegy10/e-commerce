package org.stylehub.backend.e_commerce.brand.dashboard.otp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.dto.GenerateOtpRequest;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.dto.GenerateOtpResponse;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.dto.VerifyOtpRequest;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.dto.VerifyOtpResponse;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.entity.Otp;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.repoistory.OtpRepository;
import org.stylehub.backend.e_commerce.common.mail.EmailService;
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

    @Transactional
    public GenerateOtpResponse  generateOtp(GenerateOtpRequest generateOtpRequest){
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
        emailService.sendEmail(generateOtpRequest.recipient(),"Login With Otp",
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
           return new VerifyOtpResponse(false, "OTP has expired", 0);       }
       if(otp.isConsumed()){
           return new VerifyOtpResponse(false, "OTP already used", 0);       }
       if(!otp.canAttemptVerification()){
           return new VerifyOtpResponse(false, "Maximum verification attempts reached", 0);
       }
       String inComingHash=hashOtp(verifyOtpRequest.otpCode());

       if(inComingHash.equals(otp.getOtpHash())){
           otp.consume();
           otpRepository.save(otp);
           return new VerifyOtpResponse(true, "OTP verified",
                   otp.getMaxAttempts()-otp.getAttemptCount());
       }
       otp.recordFailedAttempt();
       otpRepository.save(otp);
       int remainingAttempts = Math.max(otp.getMaxAttempts()-otp.getAttemptCount(),0);

        return new VerifyOtpResponse(false, "Invalid OTP code", remainingAttempts);
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
