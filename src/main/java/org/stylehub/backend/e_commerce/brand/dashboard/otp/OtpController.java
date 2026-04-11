package org.stylehub.backend.e_commerce.brand.dashboard.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.dto.GenerateOtpRequest;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.dto.GenerateOtpResponse;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.dto.VerifyOtpRequest;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.dto.VerifyOtpResponse;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.service.OtpService;

@RestController
@RequestMapping("api/v1/public/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    public GenerateOtpResponse generateOtp(@ModelAttribute GenerateOtpRequest generateOtpRequest){
        return otpService.generateOtp(generateOtpRequest);
    }
    @PostMapping("/verify")
    private VerifyOtpResponse verifyOtp(@ModelAttribute VerifyOtpRequest verifyOtpRequest){
        return otpService.verifyOtp(verifyOtpRequest);
    }
}
