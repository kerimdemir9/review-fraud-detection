package com.reviews.reviewsapi;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {
    final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<ReviewModel> getLegitReviews() {
        try {
            val found = reviewRepository.findByIsFraud(false);
            if (found.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No legit reviews found");
            }
            return found;
        } catch (final DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error getting reviews", e);
        }
    }

    public List<ReviewModel> getFraudReviews() {
        try {
            val found = reviewRepository.findByIsFraud(true);
            if (found.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No fraud reviews found");
            }
            return found;
        } catch (final DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error getting reviews", e);
        }
    }

    public ReviewModel saveReview(Review review) {
        try {
            return reviewRepository.save(ReviewModel.builder()
                    .review(review.getReview())
                    .isFraud(review.getIsFraud())
                    .probability(review.getProbability())
                    .build());
        } catch (final DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error saving review", e);
        }
    }

    public void hardDeleteAll() {
        try {
            reviewRepository.deleteAll();
        } catch (final DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error deleting reviews", e);
        }
    }
}
