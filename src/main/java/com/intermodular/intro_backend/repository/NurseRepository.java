package com.intermodular.intro_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.intermodular.intro_backend.entity.NurseEntity;

@Repository
public interface NurseRepository extends JpaRepository<NurseEntity, Integer> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<NurseEntity> findByFirstNameIgnoreCase(String firstName);
}
