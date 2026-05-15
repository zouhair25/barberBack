package ma.barbershop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.dto.request.product.ProductRequest;
import ma.barbershop.exception.ResourceNotFoundException;
import ma.barbershop.repository.*;
import ma.barbershop.security.UserPrincipal;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/barber/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final UserCentreSoinRepository userCentreSoinRepository;

    @GetMapping
    public ResponseEntity<List<Product>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(productRepository.findByUserCentreSoinIdAndActiveTrue(principal.getUserCentreSoinId()));
    }

    @PostMapping
    public ResponseEntity<Product> create(
            @Valid @RequestBody ProductRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserCentreSoin barber = userCentreSoinRepository.findById(principal.getUserCentreSoinId()).orElseThrow();

        Product product = Product.builder()
                .userCentreSoin(barber)
                .name(req.name())
                .description(req.description())
                .sku(req.sku())
                .priceSell(req.priceSell())
                .priceCost(req.priceCost())
                .stockQuantity(req.stockQuantity() != null ? req.stockQuantity() : 0)
                .stockAlertMin(req.stockAlertMin() != null ? req.stockAlertMin() : 5)
                .forSale(req.forSale())
                .category(req.category())
                .active(true)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {

        Product product = productRepository.findByIdAndUserCentreSoinId(id, principal.getUserCentreSoinId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setName(req.name());
        product.setDescription(req.description());
        product.setSku(req.sku());
        product.setPriceSell(req.priceSell());
        product.setPriceCost(req.priceCost());
        product.setForSale(req.forSale());
        product.setCategory(req.category());
        if (req.stockAlertMin() != null) product.setStockAlertMin(req.stockAlertMin());

        return ResponseEntity.ok(productRepository.save(product));
    }

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<Product> toggleVisibility(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        Product product = productRepository.findByIdAndUserCentreSoinId(id, principal.getUserCentreSoinId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setForSale(!product.isForSale());
        return ResponseEntity.ok(productRepository.save(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        Product product = productRepository.findByIdAndUserCentreSoinId(id, principal.getUserCentreSoinId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setActive(false);
        productRepository.save(product);
        return ResponseEntity.noContent().build();
    }
}
