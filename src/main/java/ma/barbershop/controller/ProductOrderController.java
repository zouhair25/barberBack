package ma.barbershop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.domain.enums.*;
import ma.barbershop.dto.request.order.ProductOrderRequest;
import ma.barbershop.exception.ResourceNotFoundException;
import ma.barbershop.repository.*;
import ma.barbershop.security.UserPrincipal;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/barber/orders")
@RequiredArgsConstructor
public class ProductOrderController {

    private final ProductOrderRepository orderRepository;
    private final UserCentreSoinRepository userCentreSoinRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    @GetMapping
    public ResponseEntity<Page<ProductOrder>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(orderRepository
                .findByUserCentreSoinIdOrderByOrderDateDesc(principal.getUserCentreSoins().getFirst().getId(), PageRequest.of(page, size)));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ProductOrder> create(
            @Valid @RequestBody ProductOrderRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserCentreSoin userCentreSoin = userCentreSoinRepository.findById(principal.getUserCentreSoins().getFirst().getId()).orElseThrow();

        Supplier supplier = null;
        if (req.supplierId() != null) {
            supplier = supplierRepository.findByIdAndUserCentreSoinId(req.supplierId(), principal.getUserCentreSoins().getFirst().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", req.supplierId()));
        }

        List<ProductOrderLine> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        ProductOrder order = ProductOrder.builder()
                .userCentreSoin(userCentreSoin)
                .supplier(supplier)
                .orderDate(LocalDateTime.now())
                .expectedDate(req.expectedDate())
                .status(OrderStatus.ORDERED)
                .notes(req.notes())
                .build();

        for (ProductOrderRequest.OrderLine l : req.lines()) {
            Product product = productRepository.findByIdAndUserCentreSoinId(l.productId(), principal.getUserCentreSoins().getFirst().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", l.productId()));

            BigDecimal lineTotal = l.unitCost().multiply(BigDecimal.valueOf(l.quantityOrdered()));
            total = total.add(lineTotal);

            ProductOrderLine line = ProductOrderLine.builder()
                    .order(order)
                    .product(product)
                    .quantityOrdered(l.quantityOrdered())
                    .unitCost(l.unitCost())
                    .totalCost(lineTotal)
                    .build();
            lines.add(line);
        }

        order.setLines(lines);
        order.setTotalAmount(total);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderRepository.save(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductOrder> get(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(orderRepository.findByIdAndUserCentreSoinId(id, principal.getUserCentreSoins().getFirst().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", id)));
    }

    @PatchMapping("/{id}/receive")
    @Transactional
    public ResponseEntity<ProductOrder> receive(
            @PathVariable Long id,
            @RequestBody Map<String, Object> req,
            @AuthenticationPrincipal UserPrincipal principal) {

        ProductOrder order = orderRepository.findByIdAndUserCentreSoinId(id, principal.getUserCentreSoins().getFirst().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        order.getLines().forEach(line -> {
            Product product = line.getProduct();
            int received = line.getQuantityOrdered();
            product.setStockQuantity(product.getStockQuantity() + received);
            productRepository.save(product);

            line.setQuantityReceived(received);

            StockMovement movement = StockMovement.builder()
                    .product(product)
                    .movementType(MovementType.IN)
                    .quantity(received)
                    .referenceType("ORDER")
                    .referenceId(order.getId())
                    .notes("Order #" + order.getId() + " received")
                    .build();
            stockMovementRepository.save(movement);
        });

        order.setStatus(OrderStatus.RECEIVED);
        order.setReceivedDate(java.time.LocalDate.now());
        return ResponseEntity.ok(orderRepository.save(order));
    }
}
