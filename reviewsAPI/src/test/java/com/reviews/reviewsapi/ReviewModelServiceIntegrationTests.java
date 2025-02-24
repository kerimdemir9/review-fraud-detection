package com.reviews.reviewsapi;

import lombok.val;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ReviewModelServiceIntegrationTests extends TestBase {
    private static ReviewModel review1;
    private static ReviewModel review2;
    private static ReviewModel review3;

    public void insertNewReview1() {
        review1 = reviewService.saveReview(Review
                .builder()
                .review("This is a good product.")
                .isFraud(false)
                .build());
    }

    public void insertNewReview2() {
        review2 = reviewService.saveReview(Review
                .builder()
                .review("Great thing!")
                .isFraud(true)
                .build());
    }

    public void insertNewReview3() {
        review3 = reviewService.saveReview(Review
                .builder()
                .review("This is not a good product.")
                .isFraud(false)
                .build());
    }

    @BeforeEach
    public void setup() {
        reviewService.hardDeleteAll();
    }

    @Test
    public void testCreateReview() {
        insertNewReview1();
        
        assertThat(review1).isNotNull();
        assertThat(review1.getId()).isNotNull();

        val foundReview = reviewService.getLegitReviews().get(0);
        assertThat(foundReview).isNotNull();
        assertThat(review1.getReview()).isEqualTo(foundReview.getReview());
    }
}


