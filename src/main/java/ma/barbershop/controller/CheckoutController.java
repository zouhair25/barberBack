package ma.barbershop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.Invoice;
import ma.barbershop.dto.request.invoice.CheckoutRequest;
import ma.barbershop.exception.ResourceNotFoundException;
import ma.barbershop.repository.InvoiceRepository;
import ma.barbershop.security.UserPrincipal;
import ma.barbershop.service.CheckoutService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/barber")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final InvoiceRepository invoiceRepository;

    @Value("${app.storage.base-path}")
    private String basePath;

    @PostMapping("/checkout")
    public ResponseEntity<Invoice> checkout(
            @Valid @RequestBody CheckoutRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(checkoutService.checkout(req, principal));
    }

    @GetMapping("/invoices")
    public ResponseEntity<Page<Invoice>> listInvoices(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        LocalDateTime f = from != null ? from : LocalDateTime.now().minusMonths(1);
        LocalDateTime t = to != null ? to : LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(invoiceRepository
                .findByUserCentreSoinIdAndIssuedAtBetweenOrderByIssuedAtDesc(principal.getUserCentreSoins().getFirst().getId(), f, t, pageable));
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getInvoice(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(invoiceRepository.findByIdAndUserCentreSoinId(id, principal.getUserCentreSoins().getFirst().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id)));
    }

    @GetMapping("/invoices/{id}/pdf")
    public ResponseEntity<PathResource> downloadPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) throws IOException {

        Invoice invoice = invoiceRepository.findByIdAndUserCentreSoinId(id, principal.getUserCentreSoins().getFirst().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));

        if (invoice.getPdfUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        Path file = Paths.get(basePath).resolve(invoice.getPdfUrl().replaceFirst("^/", ""));
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(new PathResource(file));
    }
}
