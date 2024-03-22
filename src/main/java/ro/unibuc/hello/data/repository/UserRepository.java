package ro.unibuc.hello.data.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ro.unibuc.hello.data.entity.UserEntity;

public interface UserRepository extends MongoRepository <UserEntity, String> {
    Optional <UserEntity> findByUsername(String username);
    Boolean existsByUsername();
}
