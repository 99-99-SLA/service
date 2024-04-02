package ro.unibuc.hello.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ro.unibuc.hello.data.entity.Actor;
import ro.unibuc.hello.data.repository.ActorRepository;
import ro.unibuc.hello.dto.tmdb.ActorDto;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.utils.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("IT")
public class ActorServiceTestIT {

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    ActorService actorService;

    @Test
    void testSaveActor() {
        final ActorDto actorDto = TestUtils.buildActorDto();
        final Actor actor = TestUtils.buildTestActor();
        final Actor savedActor = actorService.saveActor(actorDto);

        actor.setId(savedActor.getId());
        assertEquals(actor, savedActor);
    }

    @Test
    void testDeleteActor() {
        final ActorDto actorDto = TestUtils.buildActorDto();
        assertFalse(actorService.getActorByTmdbId(actorDto.getTmdbId()).isPresent());

        final Actor savedActor = actorService.saveActor(actorDto);

        assertTrue(actorService.getActorByTmdbId(savedActor.getTmdbId()).isPresent());

        actorService.deleteActor(savedActor.getId());
        assertFalse(actorService.getActorByTmdbId(savedActor.getTmdbId()).isPresent());
    }

    @Test
    void testDeleteActorThrowsEntityNotFoundException() {
        final String id = "id";
        assertThrows(EntityNotFoundException.class, () -> actorService.deleteActor(id));
    }

}
