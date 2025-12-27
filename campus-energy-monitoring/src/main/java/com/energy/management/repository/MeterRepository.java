package com.energy.management.repository;

import com.energy.management.entity.Meter;
import com.energy.management.enums.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {
    
    Optional<Meter> findBySerialNumber(String serialNumber);
    
    boolean existsBySerialNumber(String serialNumber);
    
    boolean existsByBuildingIdAndRoomNumber(Long buildingId, String roomNumber);
    
    List<Meter> findByBuildingId(Long buildingId);
    
    List<Meter> findByStatus(DeviceStatus status);
    
    @Query("SELECT m FROM Meter m WHERE m.status = 'ONLINE'")
    List<Meter> findAllOnlineDevices();
    
    Optional<Meter> findByBuildingIdAndRoomNumber(Long buildingId, String roomNumber);
    
    long countByStatus(DeviceStatus status);
    
    long countByBuildingId(Long buildingId);
    
    List<Meter> findByNameContaining(String name);
    
    @Query("SELECT m FROM Meter m WHERE m.building.id = :buildingId AND m.status = :status")
    List<Meter> findByBuildingIdAndStatus(@Param("buildingId") Long buildingId, 
                                           @Param("status") DeviceStatus status);
    
    boolean existsByBuildingIdAndRoomNumberAndStatus(Long buildingId, String roomNumber, DeviceStatus status);
}
