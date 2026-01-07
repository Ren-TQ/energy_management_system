package com.campus.energy.service;

import com.campus.energy.dto.BuildingDTO;
import com.campus.energy.entity.Building;
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
 * 本类属于MVC架构中的Model（M）层，负责处理建筑相关的业务逻辑
 * 
 * MVC职责划分：
 * - Controller (C): BuildingController - 接收请求
 * - Model (M): 本类（Service层）- 处理业务逻辑
 *              BuildingRepository - 数据访问
 *              Building实体 - 数据模型
 * - View (V): Result<BuildingDTO> - JSON响应
 * ============================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BuildingService {
    
    // ============================================
    // MVC架构 - Model层（模型层）- 数据访问部分
    // Repository层：负责数据持久化，属于Model的一部分
    // ============================================
    private final BuildingRepository buildingRepository;
    private final DeviceRepository deviceRepository;
    
    /**
     * 获取所有建筑列表
     */
    public List<BuildingDTO> getAllBuildings() {
        return buildingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取建筑信息
     */
    public BuildingDTO getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + id));
        return convertToDTO(building);
    }
    
    /**
     * 创建建筑
     */
    @Transactional
    public BuildingDTO createBuilding(BuildingDTO dto) {
        // 检查位置编号是否已存在
        if (buildingRepository.existsByLocationCode(dto.getLocationCode())) {
            throw new BusinessException("位置编号已存在: " + dto.getLocationCode());
        }
        
        // 检查名称是否已存在
        if (buildingRepository.existsByName(dto.getName())) {
            throw new BusinessException("建筑名称已存在: " + dto.getName());
        }
        
        Building building = Building.builder()
                .name(dto.getName())
                .locationCode(dto.getLocationCode())
                .floorCount(dto.getFloorCount())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .build();
        
        building = buildingRepository.save(building);
        log.info("创建建筑成功: {}", building.getName());
        
        return convertToDTO(building);
    }
    
    /**
     * 更新建筑信息
     */
    @Transactional
    public BuildingDTO updateBuilding(Long id, BuildingDTO dto) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + id));
        
        // 检查位置编号是否被其他建筑使用
        if (!building.getLocationCode().equals(dto.getLocationCode()) 
                && buildingRepository.existsByLocationCode(dto.getLocationCode())) {
            throw new BusinessException("位置编号已被使用: " + dto.getLocationCode());
        }
        
        building.setName(dto.getName());
        building.setLocationCode(dto.getLocationCode());
        building.setFloorCount(dto.getFloorCount());
        building.setCategory(dto.getCategory());
        building.setDescription(dto.getDescription());
        
        building = buildingRepository.save(building);
        log.info("更新建筑成功: {}", building.getName());
        
        return convertToDTO(building);
    }
    
    /**
     * 删除建筑
     */
    @Transactional
    public void deleteBuilding(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + id));
        
        // 检查是否有关联设备
        long deviceCount = deviceRepository.countByBuildingId(id);
        if (deviceCount > 0) {
            throw new BusinessException("该建筑下还有" + deviceCount + "个设备，无法删除");
        }
        
        buildingRepository.delete(building);
        log.info("删除建筑成功: {}", building.getName());
    }
    
    /**
     * 获取所有建筑分类
     */
    public List<String> getAllCategories() {
        return buildingRepository.findAllCategories();
    }
    
    /**
     * 转换为DTO
     */
    private BuildingDTO convertToDTO(Building building) {
        int deviceCount = (int) deviceRepository.countByBuildingId(building.getId());
        
        return BuildingDTO.builder()
                .id(building.getId())
                .name(building.getName())
                .locationCode(building.getLocationCode())
                .floorCount(building.getFloorCount())
                .category(building.getCategory())
                .description(building.getDescription())
                .deviceCount(deviceCount)
                .createdAt(building.getCreatedAt())
                .updatedAt(building.getUpdatedAt())
                .build();
    }
}

