package ro.unibuc.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.hello.data.entity.Movie;
import ro.unibuc.hello.data.entity.Review;
import ro.unibuc.hello.dto.tmdb.MovieApiDto;
import ro.unibuc.hello.dto.tmdb.ReviewDto;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.exception.MovieExceptionHandler;
import ro.unibuc.hello.service.MovieService;
import ro.unibuc.hello.service.ReviewService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ro.unibuc.hello.utils.TestUtils.*;

@ExtendWith(SpringExtension.class)
class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private MovieController movieController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(movieController)
                .setControllerAdvice(new MovieExceptionHandler(new SimpleMeterRegistry()))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddMovieSuccessful() throws Exception {
        // Arrange
        final Movie movie = buildTestMovie();
        final Long tmdbId = movie.getTmdbId();

        when(movieService.addMovie(tmdbId)).thenReturn(movie);

        // Act
        final MvcResult result = mockMvc.perform(post("/movies/" + tmdbId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        Assertions.assertEquals(objectMapper.writeValueAsString(movie), result.getResponse().getContentAsString());
    }

    @Test
    void testAddMovieAlreadyExistsThrowsBadRequest() throws Exception {
        // Arrange
        final Long tmdbId = buildTestMovie().getTmdbId();
        when(movieService.addMovie(tmdbId)).thenThrow(new IllegalArgumentException("Movie already exists"));

        // Act and Assert
        mockMvc.perform(post("/movies/" + tmdbId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testAddNonExistentMovieThrowsNotFound() throws Exception {
        // Arrange
        final Long tmdbId = buildTestMovie().getTmdbId();
        when(movieService.addMovie(tmdbId)).thenThrow(new EntityNotFoundException("Movie not found"));

        // Act and Assert
        mockMvc.perform(post("/movies/" + tmdbId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchMovies() throws Exception {
        // Arrange
        final String name = "The Matrix";
        final List<MovieApiDto> movies = Collections.singletonList(buildTestMovieApiDto());


        when(movieService.searchMovie(name)).thenReturn(movies);

        // Act
        final MvcResult result = mockMvc.perform(get("/movies/search?name=" + name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        Assertions.assertEquals(objectMapper.writeValueAsString(movies), result.getResponse().getContentAsString());
    }

    @Test
    void testGetMovieById() throws Exception {
        // Arrange
        final Movie movie = buildTestMovie();
        final String id = movie.getId();

        when(movieService.getMovieById(id)).thenReturn(movie);

        // Act
        final MvcResult result = mockMvc.perform(get("/movies/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        Assertions.assertEquals(objectMapper.writeValueAsString(movie), result.getResponse().getContentAsString());
    }

    @Test
    void testGetMovieByIdThrowsNotFound() throws Exception {
        // Arrange
        final String id = "1";
        when(movieService.getMovieById(id)).thenThrow(new EntityNotFoundException("Movie not found"));

        // Act and Assert
        mockMvc.perform(get("/movies/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetMovies() throws Exception {
        // Arrange
        final List<Movie> movies = Collections.singletonList(buildTestMovie());

        when(movieService.getMovies()).thenReturn(movies);

        // Act
        final MvcResult result = mockMvc.perform(get("/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        Assertions.assertEquals(objectMapper.writeValueAsString(movies), result.getResponse().getContentAsString());
    }

    @Test
    void testDeleteMovie() throws Exception {
        // Arrange
        final String id = "1";

        // Act
        mockMvc.perform(delete("/movies/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteMovieThrowsNotFound() throws Exception {
        // Arrange
        final String id = "1";
        doThrow(new EntityNotFoundException("Movie not found")).when(movieService).deleteMovie(id);

        // Act and Assert
        mockMvc.perform(delete("/movies/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void testAddReviewSuccessful() throws Exception {
        // Arrange
        final String movieId = "1";
        final ReviewDto reviewDto = buildTestReviewDto();
        final Review review = buildTestReview();

        when(reviewService.addReview(eq(movieId), any(ReviewDto.class))).thenReturn(review);

        // Act & Assert
        mockMvc.perform(post("/movies/" + movieId + "/reviews/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto)))
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(review)));
    }

    @Test
    void testAddReviewToNonExistentMovieThrowsNotFound() throws Exception {
        // Arrange
        final String movieId = "999";
        final ReviewDto reviewDto = buildTestReviewDto();

        when(reviewService.addReview(eq(movieId), any(ReviewDto.class))).thenThrow(new EntityNotFoundException("Movie not found"));

        // Act & Assert
        mockMvc.perform(post("/movies/" + movieId + "/reviews/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetMovieReviews() throws Exception {
        // Arrange
        final String movieId = "1";
        final List<Review> reviews = Collections.singletonList(buildTestReview());

        when(reviewService.getReviewsByMovieId(movieId)).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/movies/" + movieId + "/reviews")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(reviews)));
    }

    @Test
    void testGetMovieReviewsForNonExistentMovieThrowsNotFound() throws Exception {
        // Arrange
        final String movieId = "999";

        when(reviewService.getReviewsByMovieId(movieId)).thenThrow(new EntityNotFoundException("Movie not found"));

        // Act & Assert
        mockMvc.perform(get("/movies/" + movieId + "/reviews")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
