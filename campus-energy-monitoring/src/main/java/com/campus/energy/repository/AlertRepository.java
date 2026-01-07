package com.campus.energy.repository;

import com.campus.energy.entity.Alert;
import com.campus.energy.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警记录数据访问层
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    /**
     * 根据设备ID查找告警记录（分页）
     */
    Page<Alert> findByDeviceId(Long deviceId, Pageable pageable);
    
    /**
     * 查找未处理的告警
     */
    List<Alert> findByIsResolvedFalse();
    
    /**
     * 根据设备ID查找未处理的告警
     */
    List<Alert> findByDeviceIdAndIsResolvedFalse(Long deviceId);
    
    /**
     * 根据告警类型查找告警记录
     */
    List<Alert> findByAlertType(AlertType alertType);
    
    /**
     * 根据时间范围查找告警记录
     */
    @Query("SELECT a FROM Alert a WHERE a.triggerTime BETWEEN :startTime AND :endTime " +
           "ORDER BY a.triggerTime DESC")
    List<Alert> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计未处理告警数量
     */
    long countByIsResolvedFalse();
    
    /**
     * 统计今日告警数量
     */
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.triggerTime >= :startOfDay")
    long countTodayAlerts(@Param("startOfDay") LocalDateTime startOfDay);
    
    /**
     * 统计各类型告警数量
     */
    @Query("SELECT a.alertType, COUNT(a) FROM Alert a GROUP BY a.alertType")
    List<Object[]> countByAlertType();
    
    /**
     * 根据告警类型和时间范围统计
     */
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.alertType = :alertType " +
           "AND a.triggerTime BETWEEN :startTime AND :endTime")
    long countByAlertTypeAndTimeRange(@Param("alertType") AlertType alertType,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找最近的告警记录
     */
    List<Alert> findTop10ByOrderByTriggerTimeDesc();
    
    /**
     * 根据设备ID和告警类型查找最新告警
     */
    @Query("SELECT a FROM Alert a WHERE a.device.id = :deviceId AND a.alertType = :alertType " +
           "ORDER BY a.triggerTime DESC LIMIT 1")
    Alert findLatestByDeviceIdAndAlertType(@Param("deviceId") Long deviceId,
                                           @Param("alertType") AlertType alertType);
}

