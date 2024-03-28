package ro.unibuc.hello.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.hello.data.entity.Actor;
import ro.unibuc.hello.data.entity.Movie;
import ro.unibuc.hello.data.entity.Role;
import ro.unibuc.hello.data.repository.RoleRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ro.unibuc.hello.utils.TestUtils.buildTestActor;
import static ro.unibuc.hello.utils.TestUtils.buildTestMovie;

@ExtendWith(SpringExtension.class)
public class RoleServiceTest {
    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    RoleService roleService = new RoleService();

    @Test
    void testAddRole() {
        final Actor actor = buildTestActor();
        final Movie movie = buildTestMovie();
        when(roleRepository.save(ArgumentMatchers.any())).thenAnswer(i -> i.getArgument(0));

        final Role savedRole = roleService.addRole(actor, movie);
        assertEquals(actor, savedRole.getActor());
        assertEquals(movie, savedRole.getMovie());
    }

    @Test
    void testDeleteRolesByMovieId() {
        final String movieId = "movieId";
        final List<Role> roles = List.of(new Role("id", buildTestActor(), buildTestMovie()));
        when(roleRepository.findByMovieId(movieId)).thenReturn(roles);
        roleService.deleteRolesByMovieId(movieId);

        verify(roleRepository).deleteById("id");
    }
}
