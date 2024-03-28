package ro.unibuc.hello.data.entity;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReviewTest {
    private static final Instant NOW = Instant.now();
    private static final Review REVIEW = Review.builder()
            .id("reviewId")
            .username("user123")
            .movieId("movieId")
            .rating(5)
            .comment("Great movie!")
            .createdAt(NOW)
            .build();

    @Test
    void testId() {
        assertEquals("reviewId", REVIEW.getId());
    }

    @Test
    void testUsername() {
        assertEquals("user123", REVIEW.getUsername());
    }

    @Test
    void testMovieId() {
        assertEquals("movieId", REVIEW.getMovieId());
    }

    @Test
    void testRating() {
        assertEquals(5, REVIEW.getRating());
    }

    @Test
    void testComment() {
        assertEquals("Great movie!", REVIEW.getComment());
    }

    @Test
    void testCreatedAt() {
        assertEquals(NOW, REVIEW.getCreatedAt());
    }
}
