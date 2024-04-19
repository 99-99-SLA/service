package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;
import ro.unibuc.hello.data.entity.Movie;
import ro.unibuc.hello.data.entity.Review;
import ro.unibuc.hello.dto.tmdb.MovieApiDto;
import ro.unibuc.hello.dto.tmdb.ReviewDto;
import ro.unibuc.hello.service.MovieService;
import ro.unibuc.hello.service.ReviewService;


import java.util.List;

@Controller
@Slf4j
@RequestMapping("/movies")
public class MovieController {
    @Autowired private MovieService movieService;

    @Autowired
    private ReviewService reviewService; 

    @GetMapping("/search")
    @ResponseBody
    public List<MovieApiDto> searchMovies(@RequestParam(name="name", defaultValue="") String name) {
        if (name == null || name.isEmpty()) {
            log.warn("Search request received with empty name parameter");
        }
        log.info("Searching for movies with name: {}", name);
        return movieService.searchMovie(name);
    }

    @GetMapping
    @ResponseBody
    public List<Movie> getMovies() {
        log.info("Retrieving all movies");
        return movieService.getMovies();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Movie getMovieById(@PathVariable String id) {
        long startTime = System.currentTimeMillis();
        log.info("Retrieving movie with id: {}", id);
        Movie movie = movieService.getMovieById(id);
        long endTime = System.currentTimeMillis();
        log.info("Request processed in {} ms", endTime - startTime);
        return movie;
    }

    @PostMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Movie addMovie(@PathVariable Long id) {
        try {
            log.info("Adding movie with id: {}", id);
            return movieService.addMovie(id);
        } catch (Exception e) {
            log.error("Error adding movie with id {}: {}", id, e.getMessage(), e);
            throw e; // Rethrow the exception to propagate it further if needed
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovie(@PathVariable String id) {
        log.info("Deleting movie with id: {}", id);
        movieService.deleteMovie(id);
    }


    // Get all reviews for a movie
    @GetMapping("/{id}/reviews")
    @ResponseBody
    public List<Review> getMovieReviews(@PathVariable String id) {
        log.info("Retrieving reviews for movie with id: {}", id);
        return reviewService.getReviewsByMovieId(id);
    }

    // Add a review for a movie
    @PostMapping("/{movieId}/reviews/add")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@PathVariable String movieId, @RequestBody ReviewDto reviewDto) {
        log.info("Adding review for movie with id: {}", movieId);
        return reviewService.addReview(movieId, reviewDto);
    }

}
