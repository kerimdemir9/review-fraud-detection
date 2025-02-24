package com.reviews.reviewsapi;

import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    final KafkaTemplate<String, Review> kafkaTemplate;
    final ReviewService reviewService;

    private static final String TOPIC = "reviews-topic";

    public ReviewController(KafkaTemplate<String, Review> kafkaTemplate, ReviewService reviewService) {
        this.kafkaTemplate = kafkaTemplate;
        this.reviewService = reviewService;
    }

    @PostMapping("/submit")
    public ResponseEntity<ReviewResponse> submitReview(@RequestBody Review reviewDTO) {
        kafkaTemplate.send(TOPIC, reviewDTO);
        return ResponseEntity.ok(
                ReviewResponse.builder()
                        .response("Review submitted for analysis.")
                        .build());
    }

    @PostMapping("/update")
    public ResponseEntity<Review> updateReview(@RequestBody Review review) {

        val updatedReview = reviewService.saveReview(review);

        return ResponseEntity.ok(Review.builder()
                .review(updatedReview.getReview())
                .isFraud(updatedReview.getIsFraud())
                .probability(updatedReview.getProbability())
                .build());
    }

    @GetMapping("/legit")
    public ResponseEntity<List<Review>> getLegitReviews() {
        return ResponseEntity.ok(mapReviews(reviewService.getLegitReviews()));
    }

    @GetMapping("/fraud")
    public ResponseEntity<List<Review>> getFraudReviews() {
        return ResponseEntity.ok(mapReviews(reviewService.getFraudReviews()));
    }

    private List<Review> mapReviews(List<ReviewModel> reviews) {
        return reviews.stream().map(review ->
                        Review.builder()
                                .review(review.getReview())
                                .isFraud(review.getIsFraud())
                                .probability(review.getProbability())
                                .build())
                .collect(Collectors.toList());
    }
}

