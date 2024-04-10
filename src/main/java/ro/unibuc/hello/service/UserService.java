package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.unibuc.hello.data.entity.UserEntity;
import ro.unibuc.hello.data.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void addUser(String username, String password) {
        if (userExists(username)) {
            throw new IllegalArgumentException("User already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        System.out.println("User added successfully");
    }
    

    public void deleteUser(Long userId) {
        userRepository.deleteById(String.valueOf(userId));
        System.out.println("User deleted successfully");
    }    

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
