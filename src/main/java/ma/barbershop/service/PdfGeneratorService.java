package ma.barbershop.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.barbershop.domain.entity.Invoice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;

    @Value("${app.storage.base-path}")
    private String basePath;

    public String generateInvoicePdf(Invoice invoice) {
        try {
            Context ctx = new Context();
            ctx.setVariable("invoice", invoice);
            ctx.setVariable("barber", invoice.getBarber());
            ctx.setVariable("client", invoice.getClient());
            ctx.setVariable("lines", invoice.getLines());
            ctx.setVariable("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String html = templateEngine.process("invoice", ctx);

            Path dir = Paths.get(basePath, "invoices");
            Files.createDirectories(dir);

            String filename = "invoice-" + invoice.getInvoiceNumber() + ".pdf";
            Path filePath = dir.resolve(filename);

            try (OutputStream os = Files.newOutputStream(filePath)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(html, null);
                builder.toStream(os);
                builder.run();
            }

            return "/invoices/" + filename;
        } catch (Exception e) {
            log.error("Failed to generate PDF for invoice {}", invoice.getInvoiceNumber(), e);
            return null;
        }
    }
}
