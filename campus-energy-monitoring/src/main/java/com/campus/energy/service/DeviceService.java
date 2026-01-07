package com.campus.energy.service;

import com.campus.energy.dto.DeviceDTO;
import com.campus.energy.entity.Building;
import com.campus.energy.entity.Device;
import com.campus.energy.enums.DeviceStatus;
import com.campus.energy.exception.BusinessException;
import com.campus.energy.repository.BuildingRepository;
import com.campus.energy.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备信息服务层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final BuildingRepository buildingRepository;
    
    /**
     * 获取所有设备列表
     */
    public List<DeviceDTO> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取设备信息
     */
    public DeviceDTO getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在，ID: " + id));
        return convertToDTO(device);
    }
    
    /**
     * 根据建筑ID获取设备列表
     */
    public List<DeviceDTO> getDevicesByBuildingId(Long buildingId) {
        return deviceRepository.findByBuildingId(buildingId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取在线设备列表
     */
    public List<DeviceDTO> getOnlineDevices() {
        return deviceRepository.findAllOnlineDevices().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 创建设备
     */
    @Transactional
    public DeviceDTO createDevice(DeviceDTO dto) {
        // 检查序列号是否已存在
        if (deviceRepository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new BusinessException("设备序列号已存在: " + dto.getSerialNumber());
        }
        
        // 检查建筑是否存在
        Building building = buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + dto.getBuildingId()));
        
        // 检查同一建筑同一房间是否已有设备（绑定关系约束）
        if (deviceRepository.existsByBuildingIdAndRoomNumber(dto.getBuildingId(), dto.getRoomNumber())) {
            throw new BusinessException(String.format("建筑[%s]的房间[%s]已绑定其他设备，同一房间只能绑定一个有效电表",
                    building.getName(), dto.getRoomNumber()));
        }
        
        Device device = Device.builder()
                .name(dto.getName())
                .serialNumber(dto.getSerialNumber())
                .status(dto.getStatus() != null ? dto.getStatus() : DeviceStatus.ONLINE)
                .ratedPower(dto.getRatedPower())
                .building(building)
                .roomNumber(dto.getRoomNumber())
                .usageDescription(dto.getUsageDescription())
                .build();
        
        device = deviceRepository.save(device);
        log.info("创建设备成功: {} (SN: {})", device.getName(), device.getSerialNumber());
        
        return convertToDTO(device);
    }
    
    /**
     * 更新设备信息
     */
    @Transactional
    public DeviceDTO updateDevice(Long id, DeviceDTO dto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在，ID: " + id));
        
        // 检查序列号是否被其他设备使用
        if (!device.getSerialNumber().equals(dto.getSerialNumber())
                && deviceRepository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new BusinessException("设备序列号已被使用: " + dto.getSerialNumber());
        }
        
        // 如果更换了建筑或房间，检查绑定约束
        if (!device.getBuilding().getId().equals(dto.getBuildingId())
                || !device.getRoomNumber().equals(dto.getRoomNumber())) {
            if (deviceRepository.existsByBuildingIdAndRoomNumber(dto.getBuildingId(), dto.getRoomNumber())) {
                throw new BusinessException("目标房间已绑定其他设备");
            }
        }
        
        Building building = buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + dto.getBuildingId()));
        
        device.setName(dto.getName());
        device.setSerialNumber(dto.getSerialNumber());
        if (dto.getStatus() != null) {
            device.setStatus(dto.getStatus());
        }
        device.setRatedPower(dto.getRatedPower());
        device.setBuilding(building);
        device.setRoomNumber(dto.getRoomNumber());
        device.setUsageDescription(dto.getUsageDescription());
        
        device = deviceRepository.save(device);
        log.info("更新设备成功: {}", device.getName());
        
        return convertToDTO(device);
    }
    
    /**
     * 更新设备状态
     */
    @Transactional
    public DeviceDTO updateDeviceStatus(Long id, DeviceStatus status) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在，ID: " + id));
        
        device.setStatus(status);
        device = deviceRepository.save(device);
        log.info("更新设备状态: {} -> {}", device.getName(), status);
        
        return convertToDTO(device);
    }
    
    /**
     * 删除设备
     */
    @Transactional
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在，ID: " + id));
        
        deviceRepository.delete(device);
        log.info("删除设备成功: {} (SN: {})", device.getName(), device.getSerialNumber());
    }
    
    /**
     * 获取设备实体（内部使用）
     */
    public Device getDeviceEntity(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在，ID: " + id));
    }
    
    /**
     * 转换为DTO
     */
    private DeviceDTO convertToDTO(Device device) {
        return DeviceDTO.builder()
                .id(device.getId())
                .name(device.getName())
                .serialNumber(device.getSerialNumber())
                .status(device.getStatus())
                .statusLabel(device.getStatus().getLabel())
                .ratedPower(device.getRatedPower())
                .buildingId(device.getBuilding().getId())
                .buildingName(device.getBuilding().getName())
                .roomNumber(device.getRoomNumber())
                .usageDescription(device.getUsageDescription())
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .build();
    }
}

