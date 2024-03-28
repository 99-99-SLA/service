package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.unibuc.hello.data.entity.Movie;
import ro.unibuc.hello.data.entity.Review;
import ro.unibuc.hello.dto.tmdb.ReviewDto;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.data.repository.MovieRepository;
import ro.unibuc.hello.data.repository.ReviewRepository;
import ro.unibuc.hello.service.ReviewService;
import ro.unibuc.hello.utils.TestUtils;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        // This is optional if you have setup code
    }

    @Test
    void testAddReview() {
        // Arrange
        String movieId = "1";
        Movie movie = TestUtils.buildTestMovie();
        ReviewDto reviewDto = TestUtils.buildTestReviewDto();
        Review review = TestUtils.buildTestReview();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        Review savedReview = reviewService.addReview(movieId, reviewDto);

        // Assert
        assertEquals(review.getComment(), savedReview.getComment());
        assertEquals(review.getRating(), savedReview.getRating());
    }

    @Test
    void testAddReviewThrowsEntityNotFoundExceptionIfMovieNotFound() {
        // Arrange
        String movieId = "999";
        ReviewDto reviewDto = TestUtils.buildTestReviewDto();

        when(movieRepository.findById(movieId)).thenThrow(new EntityNotFoundException("Movie not found with id: " + movieId));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> reviewService.addReview(movieId, reviewDto));
    }

    @Test
    void testGetReviewsByMovieId() {
        // Arrange
        String movieId = "1";
        Movie movie = TestUtils.buildTestMovie();
        List<Review> reviews = List.of(TestUtils.buildTestReview());

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(reviewRepository.findByMovieId(movieId)).thenReturn(reviews);

        // Act
        List<Review> fetchedReviews = reviewService.getReviewsByMovieId(movieId);

        // Assert
        assertEquals(reviews.size(), fetchedReviews.size());
    }

    @Test
    void testGetReviewsByMovieIdThrowsEntityNotFoundExceptionIfMovieNotFound() {
        // Arrange
        String movieId = "999";

        when(movieRepository.findById(movieId)).thenThrow(new EntityNotFoundException("Movie not found with id: " + movieId));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> reviewService.getReviewsByMovieId(movieId));
    }
}

