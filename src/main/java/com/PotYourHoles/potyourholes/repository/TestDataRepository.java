package com.PotYourHoles.potyourholes.repository;

import com.PotYourHoles.potyourholes.model.TestData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDataRepository extends MongoRepository<TestData, String> {
}
