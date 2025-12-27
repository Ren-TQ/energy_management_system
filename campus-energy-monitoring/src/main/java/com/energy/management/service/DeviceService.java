package com.energy.management.service;

import com.energy.management.dto.DeviceDTO;
import com.energy.management.entity.Building;
import com.energy.management.entity.Meter;
import com.energy.management.enums.DeviceStatus;
import com.energy.management.exception.BusinessException;
import com.energy.management.repository.BuildingRepository;
import com.energy.management.repository.MeterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {
    
    private final MeterRepository meterRepository;
    private final BuildingRepository buildingRepository;
    
    public List<DeviceDTO> getAllDevices() {
        return meterRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public DeviceDTO getDeviceById(Long id) {
        Meter device = meterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在，ID: " + id));
        return convertToDTO(device);
    }
    
    public List<DeviceDTO> getDevicesByBuildingId(Long buildingId) {
        return meterRepository.findByBuildingId(buildingId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<DeviceDTO> getOnlineDevices() {
        return meterRepository.findAllOnlineDevices().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public DeviceDTO createDevice(DeviceDTO dto) {
        if (meterRepository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new BusinessException("设备序列号已存在: " + dto.getSerialNumber());
        }
        
        Building building = buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + dto.getBuildingId()));
        
        if (meterRepository.existsByBuildingIdAndRoomNumber(dto.getBuildingId(), dto.getRoomNumber())) {
            throw new BusinessException(String.format("建筑[%s]的房间[%s]已绑定其他设备，同一房间只能绑定一个有效电表",
                    building.getName(), dto.getRoomNumber()));
        }
        
        Meter device = Meter.builder()
                .name(dto.getName())
                .serialNumber(dto.getSerialNumber())
                .status(dto.getStatus() != null ? dto.getStatus() : DeviceStatus.ONLINE)
                .ratedPower(dto.getRatedPower())
                .building(building)
                .roomNumber(dto.getRoomNumber())
                .usageDescription(dto.getUsageDescription())
                .build();
        
        device = meterRepository.save(device);
        log.info("创建设备成功: {} (SN: {})", device.getName(), device.getSerialNumber());
        
        return convertToDTO(device);
    }
    
    @Transactional
    public DeviceDTO updateDevice(Long id, DeviceDTO dto) {
        Meter device = meterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在，ID: " + id));
        
        if (!device.getSerialNumber().equals(dto.getSerialNumber())
                && meterRepository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new BusinessException("设备序列号已被使用: " + dto.getSerialNumber());
        }
        
        if (!device.getBuilding().getId().equals(dto.getBuildingId())
                || !device.getRoomNumber().equals(dto.getRoomNumber())) {
            if (meterRepository.existsByBuildingIdAndRoomNumber(dto.getBuildingId(), dto.getRoomNumber())) {
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
        
        device = meterRepository.save(device);
        log.info("更新设备成功: {}", device.getName());
        
        return convertToDTO(device);
    }
    
    @Transactional
    public DeviceDTO updateDeviceStatus(Long id, DeviceStatus status) {
        Meter device = meterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在，ID: " + id));
        
        device.setStatus(status);
        device = meterRepository.save(device);
        log.info("更新设备状态: {} -> {}", device.getName(), status);
        
        return convertToDTO(device);
    }
    
    @Transactional
    public void deleteDevice(Long id) {
        Meter device = meterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在，ID: " + id));
        
        meterRepository.delete(device);
        log.info("删除设备成功: {} (SN: {})", device.getName(), device.getSerialNumber());
    }
    
    public Meter getDeviceEntity(Long id) {
        return meterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在，ID: " + id));
    }
    
    private DeviceDTO convertToDTO(Meter device) {
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

