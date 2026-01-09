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
 * ============================================
 * MVC架构 - Model层（模型层）- 业务逻辑部分
 * ============================================
 * 
 * 架构说明：
 * 本类属于MVC架构中的Model（M）层，负责：
 * 1. 处理业务逻辑（业务规则、数据验证、事务管理）
 * 2. 调用Repository层访问数据
 * 3. 实体与DTO之间的转换
 * ============================================
 * 设计模式：Facade Pattern（外观模式）
 * ============================================
 * 
 * 模式类型：结构型设计模式
 * 
 * 模式说明：
 * 外观模式为子系统中的一组接口提供一个统一的高层接口。
 * 它定义了一个更简单的接口，隐藏了子系统的复杂性。
 *
 * ============================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {
    
    // ============================================
    // 设计模式：Facade Pattern（外观模式）- 子系统
    // ============================================
    // 
    // 外观模式：子系统组件

    // 子系统职责：
    // - DeviceRepository：设备数据访问
    // - BuildingRepository：建筑数据访问
    // 
    // 外观模式体现：
    // - Service层封装了多个Repository的调用
    // - Controller只需要调用Service的简单方法
    // - 隐藏了Repository之间的复杂交互
    // ============================================
    private final DeviceRepository deviceRepository;  // 子系统：设备数据访问层
    private final BuildingRepository buildingRepository;  // 子系统：建筑数据访问层

    public List<DeviceDTO> getAllDevices() {
        // ============================================
        // 外观模式：封装子系统调用
        // 调用DeviceRepository（子系统）获取Entity列表
        // ============================================
        // MVC: Model层调用Repository（数据访问层）获取Entity
        // MVC: 将Entity转换为DTO，准备返回给Controller（View层）
        return deviceRepository.findAll().stream()
                .map(this::convertToDTO)  // 外观模式：封装Entity到DTO的转换
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
    

    @Transactional
    public DeviceDTO createDevice(DeviceDTO dto) {
        // ============================================
        // 外观模式：封装业务验证逻辑
        // 调用子系统（DeviceRepository）进行验证
        // ============================================
        // 检查序列号是否已存在
        if (deviceRepository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new BusinessException("设备序列号已存在: " + dto.getSerialNumber());
        }
        
        // ============================================
        // 外观模式：封装子系统调用
        // 调用子系统（BuildingRepository）获取建筑信息
        // ============================================
        // 检查建筑是否存在
        Building building = buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + dto.getBuildingId()));
        
        // ============================================
        // 外观模式：封装业务验证逻辑
        // 调用子系统（DeviceRepository）进行验证
        // ============================================
        // 检查同一建筑同一房间是否已有设备（绑定关系约束）
        if (deviceRepository.existsByBuildingIdAndRoomNumber(dto.getBuildingId(), dto.getRoomNumber())) {
            throw new BusinessException(String.format("建筑[%s]的房间[%s]已绑定其他设备，同一房间只能绑定一个有效电表",
                    building.getName(), dto.getRoomNumber()));
        }
        
        // ============================================
        // 设计模式：Builder Pattern（建造者模式）
        // ============================================
        // 
        // 建造者模式：使用链式调用创建Device对象

        Device device = Device.builder()
                .name(dto.getName())  // 设置设备名称
                .serialNumber(dto.getSerialNumber())  // 设置设备序列号
                .status(dto.getStatus() != null ? dto.getStatus() : DeviceStatus.ONLINE)  // 设置状态，默认ONLINE
                .ratedPower(dto.getRatedPower())  // 设置额定功率
                .building(building)  // 设置关联建筑对象
                .roomNumber(dto.getRoomNumber())  // 设置房间号
                .usageDescription(dto.getUsageDescription())  // 设置用途描述
                .build();  // 构建Device对象
        
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
    

    private DeviceDTO convertToDTO(Device device) {
        // ============================================
        // 建造者模式：使用链式调用创建DTO对象
        // ============================================
        return DeviceDTO.builder()
                .id(device.getId())  // 设置设备ID
                .name(device.getName())  // 设置设备名称
                .serialNumber(device.getSerialNumber())  // 设置设备序列号
                .status(device.getStatus())  // 设置设备状态（枚举）
                .statusLabel(device.getStatus().getLabel())  // 设置状态描述（字符串）
                .ratedPower(device.getRatedPower())  // 设置额定功率
                .buildingId(device.getBuilding().getId())  // 设置建筑ID（关联对象转ID）
                .buildingName(device.getBuilding().getName())  // 设置建筑名称（关联对象转名称）
                .roomNumber(device.getRoomNumber())  // 设置房间号
                .usageDescription(device.getUsageDescription())  // 设置用途描述
                .createdAt(device.getCreatedAt())  // 设置创建时间
                .updatedAt(device.getUpdatedAt())  // 设置更新时间
                .build();  // 构建DeviceDTO对象
    }
}

