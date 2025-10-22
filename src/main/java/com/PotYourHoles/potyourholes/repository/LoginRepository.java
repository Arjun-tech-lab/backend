package com.PotYourHoles.potyourholes.repository;

import com.PotYourHoles.potyourholes.model.Login;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends MongoRepository<Login, String> {

    // Find by email
    Optional<Login> findByEmail(String email);

    // Find by username
    Optional<Login> findByUsername(String username);

    // Find by email OR username (used in signup to check if user exists)
    Optional<Login> findByEmailOrUsername(String email, String username);
}
