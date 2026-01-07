package com.campus.energy.controller;

import com.campus.energy.dto.AlertDTO;
import com.campus.energy.dto.common.Result;
import com.campus.energy.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 告警记录控制器
 */
@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Tag(name = "告警管理", description = "告警记录查询和处理接口")
public class AlertController {
    
    private final AlertService alertService;
    
    @GetMapping
    @Operation(summary = "获取所有告警记录（分页）")
    public Result<Page<AlertDTO>> getAllAlerts(
            @PageableDefault(size = 20, sort = "triggerTime") Pageable pageable) {
        return Result.success(alertService.getAllAlerts(pageable));
    }
    
    @GetMapping("/device/{deviceId}")
    @Operation(summary = "根据设备ID获取告警记录（分页）")
    public Result<Page<AlertDTO>> getAlertsByDeviceId(
            @Parameter(description = "设备ID") @PathVariable Long deviceId,
            @PageableDefault(size = 20, sort = "triggerTime") Pageable pageable) {
        return Result.success(alertService.getAlertsByDeviceId(deviceId, pageable));
    }
    
    @GetMapping("/unresolved")
    @Operation(summary = "获取未处理的告警")
    public Result<List<AlertDTO>> getUnresolvedAlerts() {
        return Result.success(alertService.getUnresolvedAlerts());
    }
    
    @GetMapping("/recent")
    @Operation(summary = "获取最近的告警记录")
    public Result<List<AlertDTO>> getRecentAlerts() {
        return Result.success(alertService.getRecentAlerts());
    }
    
    @GetMapping("/range")
    @Operation(summary = "根据时间范围获取告警")
    public Result<List<AlertDTO>> getAlertsByTimeRange(
            @Parameter(description = "开始时间") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return Result.success(alertService.getAlertsByTimeRange(startTime, endTime));
    }
    
    @GetMapping("/count/today")
    @Operation(summary = "获取今日告警数量")
    public Result<Long> getTodayAlertCount() {
        return Result.success(alertService.getTodayAlertCount());
    }
    
    @GetMapping("/count/unresolved")
    @Operation(summary = "获取未处理告警数量")
    public Result<Long> getUnresolvedAlertCount() {
        return Result.success(alertService.getUnresolvedAlertCount());
    }
    
    @GetMapping("/stats/type")
    @Operation(summary = "获取告警类型统计")
    public Result<Map<String, Long>> getAlertTypeStats() {
        return Result.success(alertService.getAlertTypeStats());
    }
    
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "处理告警", description = "需要管理员权限")
    public Result<AlertDTO> resolveAlert(
            @Parameter(description = "告警ID") @PathVariable Long id,
            @Parameter(description = "处理备注") @RequestParam(required = false) String resolveNote) {
        return Result.success("告警已处理", alertService.resolveAlert(id, resolveNote));
    }
}

