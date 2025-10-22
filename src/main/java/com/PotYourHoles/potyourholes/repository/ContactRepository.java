package com.PotYourHoles.potyourholes.repository;

import com.PotYourHoles.potyourholes.model.Contactus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends MongoRepository<Contactus, String> {
    // Optional: add custom queries if needed
}
