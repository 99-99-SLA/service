package ro.unibuc.hello.e2e.steps;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import ro.unibuc.hello.data.entity.Movie;
import ro.unibuc.hello.data.repository.ActorRepository;
import ro.unibuc.hello.data.repository.MovieRepository;
import ro.unibuc.hello.data.repository.RoleRepository;
import ro.unibuc.hello.dto.Greeting;
import ro.unibuc.hello.dto.tmdb.MovieApiDto;
import ro.unibuc.hello.dto.tmdb.PagedApiResponseDto;
import ro.unibuc.hello.e2e.util.HeaderSetup;
import ro.unibuc.hello.e2e.util.ResponseErrorHandler;
import ro.unibuc.hello.e2e.util.ResponseResults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment= WebEnvironment.DEFINED_PORT)
public class MovieSteps {

    public static ResponseResults latestResponse = null;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static String movieId = null;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MovieRepository movieRepository;

    private static final String HOST = "https://host.docker.internal:8080";

    @Given("^the database is empty")
    public void setup() {
        roleRepository.deleteAll();
        actorRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Given("^the client calls /hello-world")
    public void the_client_issues_GET_hello() {
        executeGet(HOST + "/hello-world");
    }

    @When("^the client searches a movie with the query ((?:\\w+\\s?)+)")
    public void movieSearch(String query) {
        executeGet(HOST + "/movies/search?name=" + query);
    }

    @And("^the client receives at least one result with the title ((?:\\w+\\s?)+)")
    public void atLeastOneResultReceived(String query) {
        final String body = latestResponse.getBody();
        final JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, MovieApiDto.class);
        try {
            final List<MovieApiDto> response = objectMapper.readValue(body, type);
            assertThat("Response received is empty", response.size() > 0);
            assertEquals(query, response.get(0).getTitle());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @And("^the client receives no results")
    public void noResultsReceived() {
        final String body = latestResponse.getBody();
        final JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, MovieApiDto.class);
        try {
            final List<MovieApiDto> response = objectMapper.readValue(body, type);
            assertThat("Response received is not empty", response.size() == 0);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @When("^the client adds a movie with the id from the search results")
    public void executeAddMovie() {
        final String body = latestResponse.getBody();
        final JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, MovieApiDto.class);
        try {
            final List<MovieApiDto> response = objectMapper.readValue(body, type);
            final MovieApiDto movieApiDto = response.get(0);
            final String url = HOST + "/movies/" + movieApiDto.getTmdbId();
            executePost(url);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @And("^the movie is saved in the database with the name ((?:\\w+\\s?)+)")
    public void movieIsSaved(String query) {
        final String url = HOST + "/movies";
        executeGet(url);
        final String body = latestResponse.getBody();
        final JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, Movie.class);

        final List<Movie> movies;
        try {
            movies = objectMapper.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, movies.size());
        assertEquals(query, movies.get(0).getTitle());
        movieId = movies.get(0).getId();
    }

    @And("^the actors are saved in the database")
    public void actorsAreSaved() {
        assertThat("No actors are saved", actorRepository.count() > 0);
    }

    @And("^the roles are saved in the database")
    public void rolesAreSaved() {
        assertThat("No roles are saved", roleRepository.count() > 0);
    }

    @When("^the client searches for the saved movie")
    public void executeFetchMovie() {
        final String url = HOST + "/movies/" + movieId;
        executeGet(url);
    }

    @And("^the client receives the movie named ((?:\\w+\\s?)+)")
    public void movieIsReceived(String query) {
        final String body = latestResponse.getBody();
        try {
            final Movie movie = objectMapper.readValue(body, Movie.class);
            assertEquals(query, movie.getTitle());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @When("^the client deletes the saved movie")
    public void executeDeleteMovie() {
        final String url = HOST + "/movies/" + movieId;
        executeDelete(url);
    }

    @And("^the movie is deleted from the database")
    public void movieIsDeleted() {
        final String url = HOST + "/movies";
        executeGet(url);
        final String body = latestResponse.getBody();
        final JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, Movie.class);

        final List<Movie> movies;
        try {
            movies = objectMapper.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertEquals(0, movies.size());
    }

    @And("^the actors are deleted from the database")
    public void actorsAreDeleted() {
        assertThat("Actors are not deleted", actorRepository.count() == 0);
    }

    @And("^the roles are deleted from the database")
    public void rolesAreDeleted() {
        assertThat("Roles are not deleted", roleRepository.count() == 0);
    }


    @Then("^the client receives status code of (\\d+)$")
    public void the_client_receives_status_code_of(int statusCode) throws Throwable {
        final HttpStatus currentStatusCode = latestResponse.getTheResponse().getStatusCode();
        assertThat("status code is incorrect : " + latestResponse.getBody(), currentStatusCode.value(), is(statusCode));
    }

    @And("^the client receives response (.+)$")
    public void the_client_receives_response(String response) throws JsonProcessingException {
        String latestResponseBody = latestResponse.getBody();
        Greeting greeting = new ObjectMapper().readValue(latestResponseBody, Greeting.class);
        assertThat("Response received is incorrect", greeting.getContent(), is(response));
    }

    public void executeGet(String url) {
        executeCall(HttpMethod.GET, url);
    }

    public void executePost(String url) {
        executeCall(HttpMethod.POST, url);
    }

    public void executeDelete(String url) {
        executeCall(HttpMethod.DELETE, url);
    }

    private void executeCall(HttpMethod httpMethod, String url) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        final HeaderSetup requestCallback = new HeaderSetup(headers);
        final ResponseErrorHandler errorHandler = new ResponseErrorHandler();

        restTemplate.setErrorHandler(errorHandler);
        latestResponse = restTemplate.execute(url, httpMethod, requestCallback, response -> {
            if (errorHandler.getHadError()) {
                return (errorHandler.getResults());
            } else {
                return (new ResponseResults(response));
            }
        });
    }

}
