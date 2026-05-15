package org.stylehub.backend.e_commerce.platform.mail.template;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.stylehub.backend.e_commerce.platform.mail.events.ModelReviewRequestedEmailEvent;
import org.stylehub.backend.e_commerce.platform.mail.service.EmailReviewTokenService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ModelReviewRequestEmailTemplate {

    private static final String HTML_TEMPLATE = "templates/brand/model-review-request.html";
    private static final String CSS_TEMPLATE = "templates/brand/model-review-request.css";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a").withZone(ZoneId.of("Africa/Cairo"));

    @Value("${app.dashboard.base-url:http://localhost:3000}")
    private String dashboardBaseUrl;

    @Value("${app.api.base-url:http://localhost:8080}")
    private String apiBaseUrl;

    private final EmailReviewTokenService emailReviewTokenService;

    public String buildTemplate(ModelReviewRequestedEmailEvent event) {
        try {
            String html = readResource(HTML_TEMPLATE);
            String css = readResource(CSS_TEMPLATE);

            return html
                    .replace("{{EMAIL_CSS}}", css)
                    .replace("{{BRAND_NAME}}", safe(event.brandName()))
                    .replace("{{MODEL_NAME}}", safe(event.modelName()))
                    .replace("{{AGREEMENT_NUMBER}}", safe(event.agreementNumber()))
                    .replace("{{COMPLETED_AT}}", event.completedAt() == null ? "-" : DATE_TIME_FORMATTER.format(event.completedAt()))
                    .replace("{{STAR_BUTTONS}}", buildStarButtons(event))
                    .replace("{{REVIEW_URL}}", buildReviewUrl(event, null));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build model review email template", e);
        }
    }

    private String buildStarButtons(ModelReviewRequestedEmailEvent event) {
        StringBuilder html = new StringBuilder();
        for (int stars = 1; stars <= 5; stars++) {
            html.append("<a class=\"rating-button\" href=\"")
                    .append(buildReviewUrl(event, stars))
                    .append("\" target=\"_blank\" rel=\"noopener noreferrer\">")
                    .append(buildStarsLabel(stars))
                    .append("</a>");
        }
        return html.toString();
    }

    private String buildReviewUrl(ModelReviewRequestedEmailEvent event, Integer stars) {
        String baseUrl = this.dashboardBaseUrl == null || this.dashboardBaseUrl.isBlank()
                ? "http://localhost:3000"
                : this.dashboardBaseUrl.trim();

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        String url = baseUrl + "/brand/model-agreements/" + event.agreementId();
        if (stars == null) {
            return url;
        }
        String token = this.emailReviewTokenService.createModelReviewToken(event.agreementId(), event.brandExternalId());
        return normalizeBaseUrl(this.apiBaseUrl) + "/api/v1/public/reviews/email/model?stars=" + stars + "&token=" + token;
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

    private String readResource(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}
