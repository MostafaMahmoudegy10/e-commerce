package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.auth.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.auth.otp.dto.*;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.auth.otp.service.OtpService;

@RestController
@RequestMapping("api/v1/public/otp")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BRAND_OWNER')")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    public GenerateOtpResponse generateOtp(@ModelAttribute GenerateOtpRequest generateOtpRequest){
        return otpService.generateOtp(generateOtpRequest);
    }

    @PostMapping("/verify")
    public VerifyOtpResponse verifyOtp(@ModelAttribute VerifyOtpRequest verifyOtpRequest){
        return otpService.verifyOtp(verifyOtpRequest);
        }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        return otpService.refreshToken(request);
    }
}
