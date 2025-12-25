package com.energy.management.service.impl;

import com.energy.management.dto.request.AlertRequest;
import com.energy.management.dto.response.AlertResponse;
import com.energy.management.entity.Alert;
import com.energy.management.exception.BusinessException;
import com.energy.management.repository.AlertRepository;
import com.energy.management.service.AlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Override
    public Page<AlertResponse> getAlerts(AlertRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "triggerTime")
        );

        // 解析查询参数
        Alert.AlertType alertType = null;
        if (request.getAlertType() != null && !request.getAlertType().isEmpty()) {
            try {
                alertType = Alert.AlertType.valueOf(request.getAlertType());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("无效的告警类型");
            }
        }

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (request.getStartDate() != null && !request.getStartDate().isEmpty()) {
            startDate = LocalDate.parse(request.getStartDate()).atStartOfDay();
        }
        if (request.getEndDate() != null && !request.getEndDate().isEmpty()) {
            endDate = LocalDate.parse(request.getEndDate()).plusDays(1).atStartOfDay();
        }

        Page<Alert> alerts = alertRepository.findByCriteria(
                request.getSerialNumber(),
                alertType,
                startDate,
                endDate,
                pageable
        );

        return alerts.map(AlertResponse::fromEntity);
    }

    @Override
    public AlertResponse getAlertById(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException("告警记录不存在"));

        return AlertResponse.fromEntity(alert);
    }

    @Override
    @Transactional
    public void resolveAlert(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new BusinessException("告警记录不存在"));

        if (alert.getIsResolved()) {
            throw new BusinessException("告警记录已处理");
        }

        alert.setIsResolved(true);
        alertRepository.save(alert);

        log.info("处理告警成功: ID={}, 类型={}", id, alert.getAlertType());
    }

    @Override
    public long getUnresolvedAlertCount() {
        return alertRepository.countUnresolvedAlerts();
    }
}