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
    boolean existsBySerialNumber(String serialNumber);
    
    boolean existsByBuildingIdAndRoomNumber(Long buildingId, String roomNumber);

    Optional<Meter> findBySerialNumber(String serialNumber);

    List<Meter> findByBuildingId(Long buildingId);

    List<Meter> findByStatus(DeviceStatus status);

    @Query("SELECT m FROM Meter m WHERE m.building.id = :buildingId AND m.roomNumber = :roomNumber AND m.status = :status")
    Optional<Meter> findMeterByBuildingAndRoomAndStatus(
            @Param("buildingId") Long buildingId,
            @Param("roomNumber") String roomNumber,
            @Param("status") DeviceStatus status
    );
    
    boolean existsByBuildingIdAndRoomNumberAndStatus(Long buildingId, String roomNumber, DeviceStatus status);
}