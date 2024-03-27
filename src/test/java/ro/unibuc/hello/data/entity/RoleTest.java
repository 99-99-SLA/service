package ro.unibuc.hello.data.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ro.unibuc.hello.utils.TestUtils.buildTestActor;
import static ro.unibuc.hello.utils.TestUtils.buildTestMovie;

public class RoleTest {
    private static final Role ROLE = new Role("id", buildTestActor(), buildTestMovie());

    @Test
    void test_id(){
        Assertions.assertEquals("id", ROLE.getId());
    }

    @Test
    void test_actor(){
        Assertions.assertEquals(buildTestActor(), ROLE.getActor());
    }

    @Test
    void test_movie(){
        Assertions.assertEquals(buildTestMovie(), ROLE.getMovie());
    }
}
