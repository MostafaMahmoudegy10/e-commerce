package org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class OtpTemplateService {

    private static final String HTML_TEMPLATE = "templates/otp/otp-email.html";
    private static final String CSS_TEMPLATE = "templates/otp/otp-email.css";

    public String buildOtpEmailTemplate(String recipient, String otpCode, int expiryMinutes) {
        try {
            String html = readResource(HTML_TEMPLATE);
            String css = readResource(CSS_TEMPLATE);

            return html
                    .replace("{{EMAIL_CSS}}", css)
                    .replace("{{RECIPIENT}}", recipient)
                    .replace("{{OTP_CODE}}", otpCode)
                    .replace("{{EXPIRY_MINUTES}}", String.valueOf(expiryMinutes));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build OTP email template", e);
        }
    }

    private String readResource(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}
