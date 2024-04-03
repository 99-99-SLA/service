package ro.unibuc.hello.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.hello.client.TheMovieDbClient;
import ro.unibuc.hello.data.InformationEntity;
import ro.unibuc.hello.data.InformationRepository;
import ro.unibuc.hello.data.entity.Movie;
import ro.unibuc.hello.data.repository.MovieRepository;
import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.dto.tmdb.MovieApiDto;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.utils.TestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ro.unibuc.hello.utils.TestUtils.buildTestMovie;

@ExtendWith(SpringExtension.class)
class MovieServiceTest {

    @Mock
    MovieRepository movieRepository;

    @Mock
    TheMovieDbClient theMovieDbClient;

    @Mock
    ActorService actorService;

    @Mock
    RoleService roleService;

    @InjectMocks
    MovieService movieService = new MovieService();

    @Test
    void testSearchMovieReturnsMovie() {
        // Arrange
        final MovieApiDto movieApiDto = TestUtils.buildTestMovieApiDto();
        String name = "matrix";

        when(theMovieDbClient.searchMovie(name)).thenReturn(TestUtils.buildTestMoviePagedApiResponseDto(movieApiDto));

        // Act
        List<MovieApiDto> movies = movieService.searchMovie(name);

        // Assert
        assertEquals(1, movies.size());
    }

    @Test
    void testSearchMovieReturnsNotFoundIfClientDoesReturn404() {
        // Arrange
        String name = "matrix";

        when(theMovieDbClient.searchMovie(name)).thenThrow(new EntityNotFoundException("Resource not found"));


        assertThrows(EntityNotFoundException.class, () -> {
            // Act
           movieService.searchMovie(name);
        });
    }

    @Test
    void testAddMovieReturnsMovie() {
        // Arrange
        final MovieApiDto movieApiDto = TestUtils.buildTestMovieApiDto();
        final Long tmdbId = (long) movieApiDto.getTmdbId();

        when(movieRepository.findByTmdbId(tmdbId)).thenReturn(Optional.empty());
        when(theMovieDbClient.getMovieById(tmdbId)).thenReturn(movieApiDto);
        when(theMovieDbClient.getCastByMovieId(tmdbId)).thenReturn(TestUtils.buildTestCastDto());
        when(movieRepository.save(ArgumentMatchers.any(Movie.class))).thenAnswer(i -> i.getArgument(0));
        // Act
        final Movie movie = movieService.addMovie(tmdbId);

        // Assert
        assertEquals(movieApiDto.getTitle(), movie.getTitle());
        assertEquals(movieApiDto.getOverview(), movie.getDescription());
        assertEquals(movieApiDto.getGenres().size(), movie.getGenres().size());
        assertEquals(movieApiDto.getPopularity(), movie.getPopularity());
    }

    @Test
    void testAddMovieThrowsIllegalArgumentExceptionIfMovieAlreadyExists() {
        // Arrange
        final MovieApiDto movieApiDto = TestUtils.buildTestMovieApiDto();
        final Long tmdbId = (long) movieApiDto.getTmdbId();

        when(movieRepository.findByTmdbId(tmdbId)).thenReturn(Optional.of(buildTestMovie()));

        assertThrows(IllegalArgumentException.class, () -> {
            // Act
            movieService.addMovie(tmdbId);
        });
    }

    @Test
    void testAddMovieThrowsEntityNotFoundExceptionIfClientDoesReturn404() {
        // Arrange
        final Long tmdbId = 1L;

        when(movieRepository.findByTmdbId(tmdbId)).thenReturn(Optional.empty());
        when(theMovieDbClient.getMovieById(tmdbId)).thenThrow(new EntityNotFoundException("Resource not found"));

        assertThrows(EntityNotFoundException.class, () -> {
            // Act
            movieService.addMovie(tmdbId);
        });
    }

    @Test
    void testGetMovieByIdReturnsMovie() {
        // Arrange
        final Movie movie = buildTestMovie();
        final String id = movie.getId();

        when(movieRepository.findById(id)).thenReturn(Optional.of(movie));

        // Act
        final Movie result = movieService.getMovieById(id);

        // Assert
        assertEquals(movie, result);
    }

    @Test
    void testGetMovieByIdThrowsEntityNotFoundExceptionIfMovieNotFound() {
        // Arrange
        final String id = "1";

        when(movieRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            // Act
            movieService.getMovieById(id);
        });
    }

    @Test
    void testGetMoviesReturnsMovies() {
        // Arrange
        final List<Movie> movies = List.of(buildTestMovie());

        when(movieRepository.findAll()).thenReturn(movies);

        // Act
        final List<Movie> result = movieService.getMovies();

        // Assert
        assertEquals(movies, result);
    }

    @Test
    void testDeleteMovieDeletesMovie() {
        // Arrange
        final Movie movie = buildTestMovie();
        final String id = movie.getId();

        when(movieRepository.findById(id)).thenReturn(Optional.of(movie));

        // Act
        movieService.deleteMovie(id);

        // Verify that the delete method was called
        verify(movieRepository).delete(movie);
        // Verify that the delete method was called with the movie object
        verify(movieRepository).delete(movie);
    }

    @Test
    void testDeleteMovieThrowsEntityNotFoundExceptionIfMovieNotFound() {
        // Arrange
        final String id = "1";

        when(movieRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            // Act
            movieService.deleteMovie(id);
        });
    }
}