package com.reviews.reviewsapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewModel, Integer> {
    List<ReviewModel> findByIsFraud(Boolean isFraud);
}
