package com.energy.management.controller;

import com.energy.management.dto.request.AlertRequest;
import com.energy.management.dto.response.ApiResponse;
import com.energy.management.dto.response.AlertResponse;
import com.energy.management.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@Tag(name = "告警管理", description = "系统告警记录管理接口")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping
    @Operation(summary = "获取告警列表")
    public ApiResponse<Page<AlertResponse>> getAlerts(AlertRequest request) {
        Page<AlertResponse> response = alertService.getAlerts(request);
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取告警详情")
    public ApiResponse<AlertResponse> getAlert(@PathVariable Long id) {
        AlertResponse response = alertService.getAlertById(id);
        return ApiResponse.success("查询成功", response);
    }

    @PatchMapping("/{id}/resolve")
    @Operation(summary = "处理告警")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> resolveAlert(@PathVariable Long id) {
        alertService.resolveAlert(id);
        return ApiResponse.success("处理成功", null);
    }

    @GetMapping("/count/unresolved")
    @Operation(summary = "获取未处理告警数量")
    public ApiResponse<Long> getUnresolvedAlertCount() {
        long count = alertService.getUnresolvedAlertCount();
        return ApiResponse.success("查询成功", count);
    }
}