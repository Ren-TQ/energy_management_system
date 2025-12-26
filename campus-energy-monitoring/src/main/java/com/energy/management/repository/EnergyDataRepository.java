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
import java.util.Optional;

@Repository
public interface EnergyDataRepository extends JpaRepository<EnergyData, Long> {
    
    Page<EnergyData> findByDeviceId(Long deviceId, Pageable pageable);
    
    Optional<EnergyData> findTopByDeviceIdOrderByCollectTimeDesc(Long deviceId);
    
    @Query("SELECT e FROM EnergyData e WHERE e.device.id = :deviceId " +
           "AND e.collectTime BETWEEN :startTime AND :endTime " +
           "ORDER BY e.collectTime DESC")
    List<EnergyData> findByDeviceIdAndTimeRange(@Param("deviceId") Long deviceId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT e FROM EnergyData e WHERE e.collectTime BETWEEN :startTime AND :endTime")
    List<EnergyData> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COALESCE(MAX(e.totalEnergy) - MIN(e.totalEnergy), 0) FROM EnergyData e " +
           "WHERE e.device.id = :deviceId AND e.collectTime BETWEEN :startTime AND :endTime")
    Double calculateEnergyConsumption(@Param("deviceId") Long deviceId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT e FROM EnergyData e WHERE e.collectTime = " +
           "(SELECT MAX(e2.collectTime) FROM EnergyData e2 WHERE e2.device.id = e.device.id)")
    List<EnergyData> findLatestEnergyDataForAllDevices();
    
    List<EnergyData> findByIsAbnormalTrue();
    
    @Query("SELECT e FROM EnergyData e WHERE e.isAbnormal = true " +
           "AND e.collectTime BETWEEN :startTime AND :endTime")
    List<EnergyData> findAbnormalDataByTimeRange(@Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);
    
    long countByCollectTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT e.totalEnergy FROM EnergyData e WHERE e.device.id = :deviceId " +
           "ORDER BY e.collectTime DESC LIMIT 1")
    Optional<Double> findLatestTotalEnergyByDeviceId(@Param("deviceId") Long deviceId);
}
