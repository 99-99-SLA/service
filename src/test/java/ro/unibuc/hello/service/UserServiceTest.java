package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ro.unibuc.hello.data.entity.UserEntity;
import ro.unibuc.hello.data.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUser_Success() {
        // Arrange
        String username = "testUser";
        String password = "testPassword";
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // Act
        userService.addUser(username, password);

        // Assert
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testAddUser_Failure_UserAlreadyExists() {
        // Arrange
        String existingUsername = "existingUser";
        String password = "testPassword";
        when(userRepository.existsByUsername(existingUsername)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(existingUsername, password);
        });
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        Long userId = 1L;

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(String.valueOf(userId));
    }

    @Test
    void testUserExists_ReturnsTrue() {
        // Arrange
        String existingUsername = "existingUser";
        when(userRepository.existsByUsername(existingUsername)).thenReturn(true);

        // Act
        boolean exists = userService.userExists(existingUsername);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testUserExists_ReturnsFalse() {
        // Arrange
        String nonExistingUsername = "nonExistingUser";
        when(userRepository.existsByUsername(nonExistingUsername)).thenReturn(false);

        // Act
        boolean exists = userService.userExists(nonExistingUsername);

        // Assert
        assertFalse(exists);
    }
}
