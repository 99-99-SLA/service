package ro.unibuc.hello.utils;

import ro.unibuc.hello.data.entity.Actor;
import ro.unibuc.hello.data.entity.Movie;
import ro.unibuc.hello.data.entity.Review;
import ro.unibuc.hello.dto.tmdb.ActorDto;
import ro.unibuc.hello.dto.tmdb.CastDto;
import ro.unibuc.hello.dto.tmdb.MovieApiDto;
import ro.unibuc.hello.dto.tmdb.PagedApiResponseDto;
import ro.unibuc.hello.dto.tmdb.ReviewDto;
import ro.unibuc.hello.data.entity.Review;
import ro.unibuc.hello.dto.tmdb.ReviewDto;

import java.time.LocalDate;

import java.util.List;

public class TestUtils {
    public static Movie buildTestMovie() {
        return Movie.builder()
                .id("1")
                .title("The Matrix")
                .description("A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.")
                .genres(List.of("Action", "Science Fiction"))
                .year(1999L)
                .popularity(40)
                .tmdbId(603L)
                .build();
    }

    public static MovieApiDto buildTestMovieApiDto() {
        return MovieApiDto.builder()
                .title("The Matrix")
                .overview("A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.")
                .genres(List.of())
                .releaseDate("1999-03-30")
                .popularity(40)
                .tmdbId(603)
                .build();
    }

    public static ActorDto buildActorDto() {
        return ActorDto.builder()
                .tmdbId(6384L)
                .name("Keanu Reeves")
                .build();
    }

    public static Actor buildTestActor() {
        return Actor.builder()
                .id("1")
                .name("Keanu Reeves")
                .tmdbId(6384L)
                .build();
    }

    public static CastDto buildTestCastDto() {
        return CastDto.builder()
                .cast(List.of(buildActorDto()))
                .build();
    }

    public static PagedApiResponseDto<MovieApiDto> buildTestMoviePagedApiResponseDto(MovieApiDto movieApiDto) {
        return new PagedApiResponseDto<>(1, List.of(movieApiDto), 1, 1);
    }

    public static Review buildTestReview() {
        return Review.builder()
                .id("1")
                .movieId("1") // Assuming this is the ID of the movie the review is for
                .username("User123")
                .comment("An iconic movie that redefined its genre.")
                .rating(9)
                .build();
    }

    public static ReviewDto buildTestReviewDto() {
        return ReviewDto.builder()
                .username("User123")
                .movieId("1") // This should match with the movie ID you're testing against
                .rating(9)
                .comment("An iconic movie that redefined its genre.")
                .build();
    }
}
