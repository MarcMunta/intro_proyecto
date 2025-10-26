package com.intermodular.intro_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.intermodular.intro_backend.Nurse;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, Integer> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<Nurse> findByFirstNameIgnoreCase(String firstName);
    Nurse findByEmail(String email);
}
