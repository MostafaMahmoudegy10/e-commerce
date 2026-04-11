package org.stylehub.backend.e_commerce;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.common.mail.EmailService;

@RestController
@RequestMapping("api/v1/public")
@RequiredArgsConstructor
public class TestController {

    private final EmailService emailService;

    @GetMapping("/test-email")
    public String testEmail(@RequestParam String to,
                            @RequestParam(required = false) String from) {
        emailService.sendEmail(to, "Test Email", "Hello from 3mk teto");
        return "Email sent";
    }
}
