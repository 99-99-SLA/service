package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ro.unibuc.hello.data.entity.Actor;
import ro.unibuc.hello.data.entity.Movie;
import ro.unibuc.hello.data.entity.Role;
import ro.unibuc.hello.data.repository.RoleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ro.unibuc.hello.utils.TestUtils.buildTestActor;
import static ro.unibuc.hello.utils.TestUtils.buildTestMovie;

@SpringBootTest
@Tag("IT")
public class RoleServiceTestIT {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleService roleService;

    @BeforeEach
    void cleanup() {
        roleRepository.deleteAll();
    }

    @Test
    void testAddRole() {
        final Actor actor = buildTestActor();
        final Movie movie = buildTestMovie();

        final Role savedRole = roleService.addRole(actor, movie);
        assertEquals(1, roleRepository.count());
        assertEquals(actor, savedRole.getActor());
        assertEquals(movie, savedRole.getMovie());
    }

    @Test
    void testDeleteRolesByMovieId() {
        final Role role = new Role("id", buildTestActor(), buildTestMovie());
        assertEquals(0, roleRepository.count());
        roleRepository.save(role);
        assertEquals(1, roleRepository.count());
        roleService.deleteRolesByMovieId(role.getMovie().getId());
        assertEquals(0, roleRepository.count());
    }

    @Test
    void testDeleteRolesByMovieIdWhenNoRoles() {
        final String movieId = "movieId";
        assertEquals(0, roleRepository.count());
        roleService.deleteRolesByMovieId(movieId);
        assertEquals(0, roleRepository.count());
    }
}
