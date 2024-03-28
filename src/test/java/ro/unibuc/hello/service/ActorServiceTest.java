package ro.unibuc.hello.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.hello.data.entity.Actor;
import ro.unibuc.hello.data.repository.ActorRepository;
import ro.unibuc.hello.dto.tmdb.ActorDto;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.utils.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ActorServiceTest {
    @Mock
    ActorRepository actorRepository;

    @InjectMocks
    ActorService actorService = new ActorService();

    @Test
    void testSaveActor() {
        final ActorDto actorDto = TestUtils.buildActorDto();
        final Actor actor = TestUtils.buildTestActor();
        actor.setId(null);

        when(actorRepository.save(ArgumentMatchers.any())).thenAnswer(i -> i.getArgument(0));

        final Actor savedActor = actorService.saveActor(actorDto);
        assertEquals(actor, savedActor);
    }

    @Test
    void testDeleteActor() {
        final String id = "id";
        when(actorRepository.existsById(id)).thenReturn(true);
        actorService.deleteActor(id);
        verify(actorRepository).deleteById(id);
    }

    @Test
    void testDeleteActorThrowsEntityNotFoundException() {
        final String id = "id";
        when(actorRepository.existsById(id)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> actorService.deleteActor(id));
    }
}
