package com.energy.management.repository;

import com.energy.management.entity.Alert;
import com.energy.management.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    Page<Alert> findByDeviceId(Long deviceId, Pageable pageable);
    
    List<Alert> findByIsResolvedFalse();
    
    List<Alert> findByDeviceIdAndIsResolvedFalse(Long deviceId);
    
    List<Alert> findByAlertType(AlertType alertType);
    
    @Query("SELECT a FROM Alert a WHERE a.triggerTime BETWEEN :startTime AND :endTime " +
           "ORDER BY a.triggerTime DESC")
    List<Alert> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);
    
    long countByIsResolvedFalse();
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.triggerTime >= :startOfDay")
    long countTodayAlerts(@Param("startOfDay") LocalDateTime startOfDay);
    
    @Query("SELECT a.alertType, COUNT(a) FROM Alert a GROUP BY a.alertType")
    List<Object[]> countByAlertType();
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.alertType = :alertType " +
           "AND a.triggerTime BETWEEN :startTime AND :endTime")
    long countByAlertTypeAndTimeRange(@Param("alertType") AlertType alertType,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
    
    List<Alert> findTop10ByOrderByTriggerTimeDesc();
    
    @Query("SELECT a FROM Alert a WHERE a.device.id = :deviceId AND a.alertType = :alertType " +
           "ORDER BY a.triggerTime DESC LIMIT 1")
    Alert findLatestByDeviceIdAndAlertType(@Param("deviceId") Long deviceId,
                                           @Param("alertType") AlertType alertType);
}
