package org.stylehub.backend.e_commerce.platform.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.customer.dto.rating.ProductRatingCreationResponse;
import org.stylehub.backend.e_commerce.customer.rating.product_rating.service.ProductRatingService;
import org.stylehub.backend.e_commerce.model.review.dto.ModelAgreementReviewResponse;
import org.stylehub.backend.e_commerce.model.review.service.ModelReviewService;
import org.stylehub.backend.e_commerce.platform.mail.service.EmailReviewTokenService;

@RestController
@RequestMapping("api/v1/public/reviews/email")
@RequiredArgsConstructor
public class EmailReviewController {

    private final EmailReviewTokenService emailReviewTokenService;
    private final ModelReviewService modelReviewService;
    private final ProductRatingService productRatingService;

    @Value("${app.dashboard.base-url:http://localhost:3000}")
    private String dashboardBaseUrl;

    @Value("${app.storefront.base-url:http://localhost:3000}")
    private String storefrontBaseUrl;

    @GetMapping(value = "/model", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> submitModelReview(
            @RequestParam String token,
            @RequestParam Integer stars
    ) {
        try {
            EmailReviewTokenService.ModelReviewClaim claim = this.emailReviewTokenService.verifyModelReviewToken(token);
            ModelAgreementReviewResponse response = this.modelReviewService.upsertBrandAgreementReviewFromEmail(
                    claim.agreementId(),
                    claim.brandExternalId(),
                    stars
            );
            return htmlOk(buildSuccessPage(
                    "Model review submitted",
                    "Your " + response.stars() + "-star review was saved successfully.",
                    normalizeBaseUrl(this.dashboardBaseUrl) + "/brand/model-agreements/" + response.agreementId()
            ));
        } catch (RuntimeException exception) {
            return htmlBadRequest(buildFailurePage("Model review could not be submitted", exception.getMessage()));
        }
    }

    @GetMapping(value = "/product", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> submitProductReview(
            @RequestParam String token,
            @RequestParam Integer stars
    ) {
        try {
            EmailReviewTokenService.ProductReviewClaim claim = this.emailReviewTokenService.verifyProductReviewToken(token);
            ProductRatingCreationResponse response = this.productRatingService.upsertNewRateFromEmail(
                    claim.customerExternalId(),
                    claim.orderId(),
                    claim.productId(),
                    stars
            );
            return htmlOk(buildSuccessPage(
                    "Product review submitted",
                    "Your " + response.stars() + "-star review for " + response.productNameEn() + " was saved successfully.",
                    normalizeBaseUrl(this.storefrontBaseUrl) + "/orders/" + claim.orderId()
            ));
        } catch (RuntimeException exception) {
            return htmlBadRequest(buildFailurePage("Product review could not be submitted", exception.getMessage()));
        }
    }

    private ResponseEntity<String> htmlOk(String body) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
                .body(body);
    }

    private ResponseEntity<String> htmlBadRequest(String body) {
        return ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
                .body(body);
    }

    private String buildSuccessPage(String title, String message, String linkUrl) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        body { margin:0; padding:32px 16px; background:#f7f2ec; font-family:"Segoe UI", Arial, sans-serif; color:#1f2937; }
                        .card { max-width:560px; margin:0 auto; background:#fff; border:1px solid #eadfd4; border-radius:20px; padding:32px; box-shadow:0 12px 40px rgba(31,41,55,.08); }
                        h1 { margin:0 0 12px; font-size:28px; color:#111827; }
                        p { margin:0 0 18px; font-size:15px; line-height:1.7; color:#475467; }
                        a { display:inline-block; padding:12px 16px; border-radius:12px; background:#111827; color:#fff; text-decoration:none; font-weight:600; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>%s</h1>
                        <p>%s</p>
                        <a href="%s" target="_blank" rel="noopener noreferrer">Open details</a>
                    </div>
                </body>
                </html>
                """.formatted(
                escapeHtml(title),
                escapeHtml(title),
                escapeHtml(message),
                linkUrl
        );
    }

    private String buildFailurePage(String title, String message) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        body { margin:0; padding:32px 16px; background:#f7f2ec; font-family:"Segoe UI", Arial, sans-serif; color:#1f2937; }
                        .card { max-width:560px; margin:0 auto; background:#fff; border:1px solid #eadfd4; border-radius:20px; padding:32px; box-shadow:0 12px 40px rgba(31,41,55,.08); }
                        h1 { margin:0 0 12px; font-size:28px; color:#111827; }
                        p { margin:0 0 18px; font-size:15px; line-height:1.7; color:#475467; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>%s</h1>
                        <p>%s</p>
                    </div>
                </body>
                </html>
                """.formatted(
                escapeHtml(title),
                escapeHtml(title),
                escapeHtml(message == null || message.isBlank() ? "Please try again from the latest email." : message)
        );
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "http://localhost:3000";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
