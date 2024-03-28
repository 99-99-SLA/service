package ro.unibuc.hello.data.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ro.unibuc.hello.utils.TestUtils.buildTestMovie;

public class MovieTest {
    private static final Movie MOVIE = buildTestMovie();

    @Test
    void test_id(){
        Assertions.assertEquals("1", MOVIE.getId());
    }

    @Test
    void test_title(){
        Assertions.assertEquals("The Matrix", MOVIE.getTitle());
    }

    @Test
    void test_tmdbId(){
        Assertions.assertEquals(603L, (long)(MOVIE.getTmdbId()));
    }

    @Test
    void test_description(){
        Assertions.assertEquals("A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.", MOVIE.getDescription());
    }

    @Test
    void test_genres(){
        final List<String> genres = List.of("Action", "Science Fiction");
        Assertions.assertEquals(genres, MOVIE.getGenres());
    }

    @Test
    void test_year(){
        Assertions.assertEquals(1999L, (long)(MOVIE.getYear()));
    }

    @Test
    void test_popularity(){
        Assertions.assertEquals(40, (long)(MOVIE.getPopularity()));
    }
}
