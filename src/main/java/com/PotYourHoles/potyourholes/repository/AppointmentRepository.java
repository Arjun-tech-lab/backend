package com.PotYourHoles.potyourholes.repository;

import com.PotYourHoles.potyourholes.model.Appointments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentRepository extends MongoRepository<Appointments, String> {

    // Server-side search with pagination
    Page<Appointments> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
    Page<Appointments> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<Appointments> findByPhoneContainingIgnoreCase(String phone, Pageable pageable);
    Page<Appointments> findByAddress_CityContainingIgnoreCase(String city, Pageable pageable);
    Page<Appointments> findByAddress_StateContainingIgnoreCase(String state, Pageable pageable);
}
