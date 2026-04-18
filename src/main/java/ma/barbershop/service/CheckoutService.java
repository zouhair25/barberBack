package ma.barbershop.service;

import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.domain.enums.*;
import ma.barbershop.dto.request.invoice.CheckoutRequest;
import ma.barbershop.exception.*;
import ma.barbershop.repository.*;
import ma.barbershop.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final PdfGeneratorService pdfGeneratorService;

    @Transactional
    public Invoice checkout(CheckoutRequest req, UserPrincipal principal) {
        Appointment apt = appointmentRepository.findByIdAndBarberId(req.appointmentId(), principal.getBarberId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", req.appointmentId()));

        if (apt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Appointment already completed");
        }
        if (apt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("Cannot checkout a cancelled appointment");
        }

        if (invoiceRepository.findByAppointmentId(apt.getId()).isPresent()) {
            throw new BusinessException("Invoice already exists for this appointment");
        }

        List<InvoiceLine> lines = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        // Service line
        BarberService svc = apt.getService();
        InvoiceLine serviceLine = InvoiceLine.builder()
                .lineType(InvoiceLineType.SERVICE)
                .service(svc)
                .label(svc.getName())
                .unitPrice(svc.getPrice())
                .quantity(1)
                .totalPrice(svc.getPrice())
                .build();
        lines.add(serviceLine);
        subtotal = subtotal.add(svc.getPrice());

        // Product lines
        if (req.products() != null) {
            for (CheckoutRequest.ProductLineItem item : req.products()) {
                Product product = productRepository.findByIdAndBarberId(item.productId(), principal.getBarberId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product", item.productId()));

                if (product.getStockQuantity() < item.quantity()) {
                    throw new BusinessException("Insufficient stock for product: " + product.getName());
                }

                BigDecimal lineTotal = product.getPriceSell().multiply(BigDecimal.valueOf(item.quantity()));

                InvoiceLine productLine = InvoiceLine.builder()
                        .lineType(InvoiceLineType.PRODUCT)
                        .product(product)
                        .label(product.getName())
                        .unitPrice(product.getPriceSell())
                        .quantity(item.quantity())
                        .totalPrice(lineTotal)
                        .build();
                lines.add(productLine);
                subtotal = subtotal.add(lineTotal);

                // Deduct stock
                product.setStockQuantity(product.getStockQuantity() - item.quantity());
                productRepository.save(product);

                // Record movement
                StockMovement movement = StockMovement.builder()
                        .product(product)
                        .movementType(MovementType.USED_IN_SERVICE)
                        .quantity(-item.quantity())
                        .referenceType("APPOINTMENT")
                        .referenceId(apt.getId())
                        .notes("Used in appointment #" + apt.getId())
                        .build();
                stockMovementRepository.save(movement);
            }
        }

        // Tax
        BigDecimal taxRate = req.taxRate() != null ? req.taxRate() : BigDecimal.ZERO;
        BigDecimal taxAmount = subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(taxAmount);

        // Invoice number
        String invoiceNumber = generateInvoiceNumber(principal.getBarberId());

        Invoice invoice = Invoice.builder()
                .appointment(apt)
                .barber(apt.getBarber())
                .client(apt.getClient())
                .invoiceNumber(invoiceNumber)
                .issuedAt(LocalDateTime.now())
                .subtotal(subtotal)
                .taxRate(taxRate)
                .taxAmount(taxAmount)
                .total(total)
                .notes(req.notes())
                .lines(lines)
                .build();

        lines.forEach(line -> line.setInvoice(invoice));
        Invoice saved = invoiceRepository.save(invoice);

        // Complete the appointment
        apt.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(apt);

        // Generate PDF if requested
        if (req.generatePdf()) {
            String pdfPath = pdfGeneratorService.generateInvoicePdf(saved);
            saved.setPdfUrl(pdfPath);
            invoiceRepository.save(saved);
        }

        return saved;
    }

    private String generateInvoiceNumber(Long barberId) {
        String prefix = "INV-" + barberId + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        String last = invoiceRepository.findLastInvoiceNumber(barberId).orElse(prefix + "0000");
        try {
            int seq = Integer.parseInt(last.substring(last.lastIndexOf('-') + 1)) + 1;
            return prefix + String.format("%04d", seq);
        } catch (NumberFormatException e) {
            return prefix + "0001";
        }
    }
}
