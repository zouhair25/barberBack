package ma.barbershop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.barbershop.domain.entity.*;
import ma.barbershop.domain.enums.AppointmentStatus;
import ma.barbershop.dto.request.review.ReviewRequest;
import ma.barbershop.exception.*;
import ma.barbershop.repository.*;
import ma.barbershop.security.UserPrincipal;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;

    @PostMapping("/user/appointments/{appointmentId}/review")
    public ResponseEntity<Review> submitReview(
            @PathVariable Long appointmentId,
            @Valid @RequestBody ReviewRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {

        Appointment apt = appointmentRepository.findByIdAndClientId(appointmentId, principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        if (apt.getStatus() != AppointmentStatus.COMPLETED) {
            throw new BusinessException("Can only review completed appointments");
        }

        if (reviewRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new BusinessException("Review already submitted for this appointment");
        }

        Review review = Review.builder()
                .appointment(apt)
                .userCentreSoin(apt.getUserCentreSoin())
                .client(apt.getClient())
                .rating(req.rating())
                .comment(req.comment())
                .visible(true)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewRepository.save(review));
    }

    @GetMapping("/barber/reviews")
    public ResponseEntity<Page<Review>> barberReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reviewRepository
                .findByUserCentreSoinIdOrderByCreatedAtDesc(principal.getUserCentreSoinId(), pageable));
    }

    @PatchMapping("/barber/reviews/{id}/visibility")
    public ResponseEntity<Review> toggleVisibility(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));

        if (!review.getUserCentreSoin().getId().equals(principal.getUserCentreSoinId())) {
            throw new UnauthorizedAccessException();
        }

        review.setVisible(!review.isVisible());
        return ResponseEntity.ok(reviewRepository.save(review));
    }
}
