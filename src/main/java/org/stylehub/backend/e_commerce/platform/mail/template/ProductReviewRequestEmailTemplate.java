package org.stylehub.backend.e_commerce.platform.mail.template;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.stylehub.backend.e_commerce.platform.mail.dto.ProductReviewEmailItem;
import org.stylehub.backend.e_commerce.platform.mail.events.ProductReviewRequestedEmailEvent;
import org.stylehub.backend.e_commerce.platform.mail.service.EmailReviewTokenService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ProductReviewRequestEmailTemplate {

    private static final String HTML_TEMPLATE = "templates/customer/product-review-request.html";
    private static final String CSS_TEMPLATE = "templates/customer/product-review-request.css";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a").withZone(ZoneId.of("Africa/Cairo"));

    @Value("${app.storefront.base-url:http://localhost:3000}")
    private String storefrontBaseUrl;

    @Value("${app.api.base-url:http://localhost:8080}")
    private String apiBaseUrl;

    private final EmailReviewTokenService emailReviewTokenService;

    public String buildTemplate(ProductReviewRequestedEmailEvent event) {
        try {
            String html = readResource(HTML_TEMPLATE);
            String css = readResource(CSS_TEMPLATE);

            return html
                    .replace("{{EMAIL_CSS}}", css)
                    .replace("{{CUSTOMER_NAME}}", safe(event.customerName()))
                    .replace("{{ORDER_NUMBER}}", safe(event.orderNumber()))
                    .replace("{{DELIVERED_AT}}", event.deliveredAt() == null ? "-" : DATE_TIME_FORMATTER.format(event.deliveredAt()))
                    .replace("{{PRODUCT_REVIEW_BLOCKS}}", buildProductReviewBlocks(event))
                    .replace("{{REVIEW_URL}}", buildReviewUrl(event, null));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build product review email template", e);
        }
    }

    private String buildProductReviewBlocks(ProductReviewRequestedEmailEvent event) {
        if (event.products() == null || event.products().isEmpty()) {
            return """
                    <div class="product-rating-block">
                        <p class="product-name">Your delivered StyleHub items</p>
                    </div>
                    """;
        }

        StringBuilder html = new StringBuilder();
        for (ProductReviewEmailItem product : event.products()) {
            html.append("<div class=\"product-rating-block\">")
                    .append("<p class=\"product-name\">")
                    .append(escapeHtml(safe(product.productName())))
                    .append("</p>")
                    .append("<div class=\"rating-grid\">")
                    .append(buildStarButtons(event, product))
                    .append("</div>")
                    .append("</div>");
        }
        return html.toString();
    }

    private String buildStarButtons(ProductReviewRequestedEmailEvent event, ProductReviewEmailItem product) {
        StringBuilder html = new StringBuilder();
        for (int stars = 1; stars <= 5; stars++) {
            html.append("<a class=\"rating-button\" href=\"")
                    .append(buildReviewUrl(event, product, stars))
                    .append("\" target=\"_blank\" rel=\"noopener noreferrer\">")
                    .append(buildStarsLabel(stars))
                    .append("</a>");
        }
        return html.toString();
    }

    private String buildReviewUrl(ProductReviewRequestedEmailEvent event, Integer stars) {
        String baseUrl = this.storefrontBaseUrl == null || this.storefrontBaseUrl.isBlank()
                ? "http://localhost:3000"
                : this.storefrontBaseUrl.trim();

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        String url = baseUrl + "/orders/" + event.orderId();
        if (stars == null) {
            return url;
        }
        return url + "?stars=" + stars + "&source=email";
    }

    private String buildReviewUrl(ProductReviewRequestedEmailEvent event, ProductReviewEmailItem product, Integer stars) {
        String baseUrl = this.storefrontBaseUrl == null || this.storefrontBaseUrl.isBlank()
                ? "http://localhost:3000"
                : this.storefrontBaseUrl.trim();

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        String token = this.emailReviewTokenService.createProductReviewToken(
                event.orderId(),
                product.productId(),
                event.customerExternalId()
        );
        return normalizeBaseUrl(this.apiBaseUrl) + "/api/v1/public/reviews/email/product?stars=" + stars + "&token=" + token;
    }

    private String buildStarsLabel(int stars) {
        StringBuilder label = new StringBuilder();
        for (int i = 0; i < stars; i++) {
            label.append("&#9733;");
        }
        label.append(" ").append(stars).append(stars == 1 ? " Star" : " Stars");
        return label.toString();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "http://localhost:8080";
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

    private String readResource(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}
