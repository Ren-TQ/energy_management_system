package com.campus.energy.repository;

import com.campus.energy.entity.EnergyData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 能耗数据数据访问层
 */
@Repository
public interface EnergyDataRepository extends JpaRepository<EnergyData, Long> {
    
    /**
     * 根据设备ID查找能耗数据（分页）
     */
    Page<EnergyData> findByDeviceId(Long deviceId, Pageable pageable);
    
    /**
     * 根据设备ID查找最新的能耗数据
     */
    Optional<EnergyData> findTopByDeviceIdOrderByCollectTimeDesc(Long deviceId);
    
    /**
     * 根据设备ID和时间范围查找能耗数据
     */
    @Query("SELECT e FROM EnergyData e WHERE e.device.id = :deviceId " +
           "AND e.collectTime BETWEEN :startTime AND :endTime " +
           "ORDER BY e.collectTime DESC")
    List<EnergyData> findByDeviceIdAndTimeRange(@Param("deviceId") Long deviceId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找时间范围内的所有能耗数据
     */
    @Query("SELECT e FROM EnergyData e WHERE e.collectTime BETWEEN :startTime AND :endTime")
    List<EnergyData> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计设备在指定时间范围内的总用电量
     */
    @Query("SELECT COALESCE(MAX(e.totalEnergy) - MIN(e.totalEnergy), 0) FROM EnergyData e " +
           "WHERE e.device.id = :deviceId AND e.collectTime BETWEEN :startTime AND :endTime")
    Double calculateEnergyConsumption(@Param("deviceId") Long deviceId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找所有设备的最新能耗数据
     */
    @Query("SELECT e FROM EnergyData e WHERE e.collectTime = " +
           "(SELECT MAX(e2.collectTime) FROM EnergyData e2 WHERE e2.device.id = e.device.id)")
    List<EnergyData> findLatestEnergyDataForAllDevices();
    
    /**
     * 查找异常数据
     */
    List<EnergyData> findByIsAbnormalTrue();
    
    /**
     * 根据时间范围查找异常数据
     */
    @Query("SELECT e FROM EnergyData e WHERE e.isAbnormal = true " +
           "AND e.collectTime BETWEEN :startTime AND :endTime")
    List<EnergyData> findAbnormalDataByTimeRange(@Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内的数据条数
     */
    long countByCollectTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取设备最新累计用电量
     */
    @Query("SELECT e.totalEnergy FROM EnergyData e WHERE e.device.id = :deviceId " +
           "ORDER BY e.collectTime DESC LIMIT 1")
    Optional<Double> findLatestTotalEnergyByDeviceId(@Param("deviceId") Long deviceId);
}

