package com.energy.management.repository;

import com.energy.management.entity.Alert;
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

    Page<Alert> findByIsResolved(boolean isResolved, Pageable pageable);

    @Query("SELECT a FROM Alert a WHERE " +
            "(:serialNumber IS NULL OR a.meter.serialNumber = :serialNumber) AND " +
            "(:alertType IS NULL OR a.alertType = :alertType) AND " +
            "(:startDate IS NULL OR a.triggerTime >= :startDate) AND " +
            "(:endDate IS NULL OR a.triggerTime <= :endDate)")
    Page<Alert> findByCriteria(
            @Param("serialNumber") String serialNumber,
            @Param("alertType") Alert.AlertType alertType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    List<Alert> findByMeterIdAndIsResolvedFalse(Long meterId);

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.isResolved = false")
    long countUnresolvedAlerts();
}