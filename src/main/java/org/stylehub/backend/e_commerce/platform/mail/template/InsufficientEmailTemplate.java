package org.stylehub.backend.e_commerce.platform.mail.template;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.stylehub.backend.e_commerce.platform.mail.events.InsufficientStockRequestedEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class InsufficientEmailTemplate {

    private final static String HTML_TEMPLATE="templates/brand/stock-insufficient-alert.html";
    private final static String CSS_TEMPLATE="templates/brand/stock-insufficient-alert.css";

    public static String buildInsufficientEmailTemplate(InsufficientStockRequestedEvent event, String recipient)  {
        String html = null;
        try {
            html = readResources(HTML_TEMPLATE);

            String css=readResources(CSS_TEMPLATE);

            return html.
                     replace("{{EMAIL_CSS}}", css)
                    .replace("{{RECIPIENT}}", recipient)
                    .replace("{{PRODUCT_NAME}}", event.productName())
                    .replace("{{SKU}}", event.sku())
                    .replace(
                            "{{REQUESTED_QUANTITY}}",
                            String.valueOf(event.requestedQuantity())
                    )
                    .replace(
                            "{{AVAILABLE_STOCK}}",
                            String.valueOf(event.availableStock())
                    );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static String readResources(String path) throws IOException {
        ClassPathResource  resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

    }

}
