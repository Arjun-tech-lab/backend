package com.PotYourHoles.potyourholes.repository;

import com.PotYourHoles.potyourholes.model.AImodel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIRepository extends MongoRepository<AImodel, String> {
    // Basic CRUD operations
}
