package com.energy.management.repository;

import com.energy.management.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    boolean existsByName(String name);
    Optional<Building> findByName(String name);
}