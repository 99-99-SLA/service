package ro.unibuc.hello.data.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ro.unibuc.hello.utils.TestUtils.buildTestUser;

public class UserTest {
    private static final UserEntity USER = buildTestUser();

    @Test
    void test_id(){
        Assertions.assertEquals("1", USER.getId());
    }

    @Test
    void test_username(){
        Assertions.assertEquals("exampleUser", USER.getUsername());
    }

    @Test
    void test_password(){
        Assertions.assertEquals("examplePassword", USER.getPassword());
    }

    @Test
    void test_roles(){
        final List<String> roles = List.of("USER");
        Assertions.assertEquals(roles, USER.getRoles());
    }
}