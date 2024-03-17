package ro.unibuc.hello.data.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ro.unibuc.hello.data.entity.User;

public interface UserRepository extends MongoRepository <User, String> {

    Optional <User> findByUsername(String username);
}
