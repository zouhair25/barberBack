package ma.barbershop.controller;

import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.domain.enums.MovementType;
import ma.barbershop.exception.ResourceNotFoundException;
import ma.barbershop.repository.*;
import ma.barbershop.security.UserPrincipal;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/barber/stock")
@RequiredArgsConstructor
public class StockController {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    @GetMapping
    public ResponseEntity<List<Product>> getStock(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(productRepository.findByBarberIdAndActiveTrue(principal.getBarberId()));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Product>> getLowStock(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(productRepository.findLowStock(principal.getBarberId()));
    }

    @PostMapping("/adjustment")
    @Transactional
    public ResponseEntity<Product> adjust(
            @RequestBody Map<String, Object> req,
            @AuthenticationPrincipal UserPrincipal principal) {

        Long productId = Long.valueOf(req.get("productId").toString());
        int quantity = Integer.parseInt(req.get("quantity").toString());
        String notes = req.getOrDefault("notes", "").toString();

        Product product = productRepository.findByIdAndBarberId(productId, principal.getBarberId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        int newQty = product.getStockQuantity() + quantity;
        if (newQty < 0) newQty = 0;
        product.setStockQuantity(newQty);
        productRepository.save(product);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .movementType(MovementType.ADJUSTMENT)
                .quantity(quantity)
                .notes(notes)
                .build();
        stockMovementRepository.save(movement);

        return ResponseEntity.ok(product);
    }

    @GetMapping("/movements")
    public ResponseEntity<Page<StockMovement>> getMovements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(stockMovementRepository
                .findByProductBarberIdOrderByCreatedAtDesc(principal.getBarberId(), pageable));
    }
}
