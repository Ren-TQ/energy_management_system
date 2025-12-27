package com.energy.management.repository;

import com.energy.management.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    
    Optional<Building> findByLocationCode(String locationCode);
    
    Optional<Building> findByName(String name);
    
    List<Building> findByCategory(String category);
    
    boolean existsByLocationCode(String locationCode);
    
    boolean existsByName(String name);
    
    @Query("SELECT DISTINCT b.category FROM Building b")
    List<String> findAllCategories();
    
    List<Building> findByNameContaining(String name);
}
