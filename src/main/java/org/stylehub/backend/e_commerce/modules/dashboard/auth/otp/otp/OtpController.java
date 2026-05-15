package org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.dto.*;
import org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.service.OtpService;

@RestController
@RequestMapping("api/v1/public/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenerateOtpResponse generateOtpJson(@RequestBody GenerateOtpRequest generateOtpRequest){
        return otpService.generateOtp(generateOtpRequest);
    }

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public GenerateOtpResponse generateOtp(@ModelAttribute GenerateOtpRequest generateOtpRequest){
        return otpService.generateOtp(generateOtpRequest);
    }

    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public VerifyOtpResponse verifyOtpJson(@RequestBody VerifyOtpRequest verifyOtpRequest){
        return otpService.verifyOtp(verifyOtpRequest);
    }

    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public VerifyOtpResponse verifyOtp(@ModelAttribute VerifyOtpRequest verifyOtpRequest){
        return otpService.verifyOtp(verifyOtpRequest);
        }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        return otpService.refreshToken(request);
    }
}
