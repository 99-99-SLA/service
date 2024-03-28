package ro.unibuc.hello.data.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActorTest {
    private static final Actor ACTOR = new Actor("id", "name", 1L);

    @Test
    void test_id(){
        assertEquals("id", ACTOR.getId());
    }

    @Test
    void test_name(){
        assertEquals("name", ACTOR.getName());
    }

    @Test
    void test_tmdbId(){
        assertEquals(1L, (long) ACTOR.getTmdbId());
    }
}
