package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ro.unibuc.hello.client.TheMovieDbClient;
import ro.unibuc.hello.data.entity.Movie;
import ro.unibuc.hello.data.repository.ActorRepository;
import ro.unibuc.hello.data.repository.MovieRepository;
import ro.unibuc.hello.data.repository.RoleRepository;
import ro.unibuc.hello.dto.tmdb.MovieApiDto;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.utils.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static ro.unibuc.hello.utils.TestUtils.buildTestMovie;

@SpringBootTest
@Tag("IT")
public class MovieServiceTestIT {

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ActorRepository actorRepository;

    @MockBean
    TheMovieDbClient theMovieDbClient;

    @Autowired
    ActorService actorService;

    @Autowired
    RoleService roleService;

    @Autowired
    MovieService movieService;

    @BeforeEach
    void cleanup() {
        roleRepository.deleteAll();
        actorRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void testAddMovieReturnsMovie() {
        final MovieApiDto movieApiDto = TestUtils.buildTestMovieApiDto();
        final Long tmdbId = (long) movieApiDto.getTmdbId();

        when(theMovieDbClient.getMovieById(tmdbId)).thenReturn(movieApiDto);
        when(theMovieDbClient.getCastByMovieId(tmdbId)).thenReturn(TestUtils.buildTestCastDto());

        final Movie movie = movieService.addMovie(tmdbId);

        // Assert
        assertEquals(1, movieRepository.count());
        assertEquals(movieApiDto.getTitle(), movie.getTitle());
        assertEquals(movieApiDto.getOverview(), movie.getDescription());
        assertEquals(movieApiDto.getGenres().size(), movie.getGenres().size());
        assertEquals(movieApiDto.getPopularity(), movie.getPopularity());
    }

    @Test
    void testAddMovieThrowsIllegalArgumentExceptionIfMovieAlreadyExists() {
        final MovieApiDto movieApiDto = TestUtils.buildTestMovieApiDto();
        final Long tmdbId = (long) movieApiDto.getTmdbId();

        when(theMovieDbClient.getMovieById(tmdbId)).thenReturn(movieApiDto);
        when(theMovieDbClient.getCastByMovieId(tmdbId)).thenReturn(TestUtils.buildTestCastDto());

        movieService.addMovie(tmdbId);
        assertThrows(IllegalArgumentException.class, () -> {
            // Act
            movieService.addMovie(tmdbId);
        });
    }

    @Test
    void testGetMovieByIdReturnsMovie() {
        final Movie movie = buildTestMovie();
        movieRepository.save(movie);

        final Movie foundMovie = movieService.getMovieById(movie.getId());

        assertEquals(movie, foundMovie);
    }

    @Test
    void testGetMovieByIdThrowsEntityNotFoundExceptionIfMovieNotFound() {
        // Arrange
        final String id = "1";

        assertThrows(EntityNotFoundException.class, () -> {
            // Act
            movieService.getMovieById(id);
        });
    }

    @Test
    void testGetMoviesReturnsMovies() {
        final Movie movie = buildTestMovie();
        final List<Movie> movies = List.of(movie);

        movieRepository.save(movie);
        final List<Movie> result = movieService.getMovies();

        assertEquals(movies, result);
    }

    @Test
    void testDeleteMovieDeletesMovie() {
        final Movie movie = buildTestMovie();
        final String id = movie.getId();

        assertEquals(0, movieRepository.count());
        movieRepository.save(movie);
        assertEquals(1, movieRepository.count());
        movieService.deleteMovie(id);
        assertEquals(0, movieRepository.count());
    }

    @Test
    void testDeleteMovieThrowsEntityNotFoundExceptionIfMovieNotFound() {
        // Arrange
        final String id = "1";

        assertThrows(EntityNotFoundException.class, () -> {
            // Act
            movieService.deleteMovie(id);
        });
    }

}
