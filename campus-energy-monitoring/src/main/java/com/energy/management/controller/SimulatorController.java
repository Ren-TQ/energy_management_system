package com.energy.management.controller;

import com.energy.management.dto.common.Result;
import com.energy.management.simulator.EnergySimulatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simulator")
@RequiredArgsConstructor
@Tag(name = "模拟器管理", description = "能耗数据模拟器控制接口")
public class SimulatorController {
    
    private final EnergySimulatorService simulatorService;
    
    @GetMapping("/status")
    @Operation(summary = "获取模拟器状态")
    public Result<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", simulatorService.isSimulatorEnabled());
        status.put("generatedDataCount", simulatorService.getGeneratedDataCount());
        return Result.success(status);
    }
    
    @PostMapping("/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "开启/关闭模拟器", description = "需要管理员权限")
    public Result<Map<String, Object>> toggleSimulator(@RequestParam boolean enabled) {
        simulatorService.setSimulatorEnabled(enabled);
        
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", enabled);
        status.put("message", enabled ? "模拟器已开启" : "模拟器已关闭");
        
        return Result.success(status);
    }
    
    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "手动触发数据生成", description = "需要管理员权限，立即为所有在线设备生成一次能耗数据")
    public Result<String> triggerGeneration() {
        simulatorService.triggerManualGeneration();
        return Result.success("已触发数据生成");
    }
}

