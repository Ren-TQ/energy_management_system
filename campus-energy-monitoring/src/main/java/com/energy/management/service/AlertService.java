package com.energy.management.service;

import com.energy.management.dto.request.AlertRequest;
import com.energy.management.dto.response.AlertResponse;
import org.springframework.data.domain.Page;

public interface AlertService {
    Page<AlertResponse> getAlerts(AlertRequest request);
    AlertResponse getAlertById(Long id);
    void resolveAlert(Long id);
    long getUnresolvedAlertCount();
}