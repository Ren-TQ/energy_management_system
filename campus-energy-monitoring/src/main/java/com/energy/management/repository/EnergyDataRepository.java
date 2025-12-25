package com.energy.management.repository;

import com.energy.management.entity.EnergyData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnergyDataRepository extends JpaRepository<EnergyData, Long> {

    List<EnergyData> findByMeterIdOrderByTimestampDesc(Long meterId, Pageable pageable);

    @Query("SELECT e FROM EnergyData e WHERE e.meter.id = :meterId AND e.timestamp BETWEEN :start AND :end ORDER BY e.timestamp DESC")
    List<EnergyData> findByMeterIdAndTimestampBetween(
            @Param("meterId") Long meterId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT e FROM EnergyData e WHERE e.timestamp >= :startTime ORDER BY e.timestamp DESC")
    List<EnergyData> findRecentData(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT SUM(e.energyConsumption) FROM EnergyData e WHERE e.meter.building.id = :buildingId AND DATE(e.timestamp) = CURDATE()")
    Double findTodayConsumptionByBuildingId(@Param("buildingId") Long buildingId);

    @Query(value = "SELECT * FROM energy_data WHERE meter_id = :meterId ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<EnergyData> findLatestDataByMeter(@Param("meterId") Long meterId, @Param("limit") int limit);
}