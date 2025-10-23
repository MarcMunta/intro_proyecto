package com.intermodular.intro_backend;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NurseRepository extends JpaRepository<Nurse, Integer> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<Nurse> findByFirstNameIgnoreCase(String firstName);

}
