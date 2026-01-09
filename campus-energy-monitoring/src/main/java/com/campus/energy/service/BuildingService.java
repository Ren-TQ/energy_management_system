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
 * 
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
 * 在此项目中的应用：
 * - Facade（外观类）：BuildingService（本类）
 *   - 为Controller层提供简化的建筑操作接口
 *   - 隐藏了Repository层的复杂交互
 *   - 封装了业务逻辑和数据转换
 * 
 * - Subsystem（子系统）：
 *   - BuildingRepository：建筑数据访问层
 *   - DeviceRepository：设备数据访问层（用于统计设备数量）
 *   - Entity/DTO转换逻辑
 *   - 业务验证逻辑
 * 
 * - Client（客户端）：BuildingController
 *   - 只需要调用Service的简单方法
 *   - 不需要了解Repository的复杂交互
 * 
 * 外观模式优势体现：
 * 1. 简化客户端调用：
 * 2. 隐藏子系统复杂性：
 * 3. 降低耦合度：
 *    - Controller与Repository解耦
 *    - 子系统变化不影响Controller
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BuildingService {
    
    // ============================================
    // 设计模式：Facade Pattern（外观模式）- 子系统
    // ============================================
    // 
    // 外观模式：子系统组件
    // 子系统职责：
    // - BuildingRepository：建筑数据访问
    // - DeviceRepository：设备数据访问（用于统计设备数量）
    // ============================================
    private final BuildingRepository buildingRepository;  // 子系统：建筑数据访问层
    private final DeviceRepository deviceRepository;  // 子系统：设备数据访问层
    
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
     * 
     * ============================================
     * 设计模式：Facade Pattern（外观模式）
     * ============================================
     * 
     * 外观模式：封装复杂的业务逻辑和子系统交互
     * 
     * 执行流程：
     * 1. 调用BuildingRepository查找建筑
     * 2. 调用DeviceRepository检查关联设备数量
     * 3. 业务验证：如果有关联设备，抛出异常
     * 4. 调用BuildingRepository删除建筑
     * 
     * 外观模式优势体现：
     * - 客户端只需要调用deleteBuilding(id)
     * - 不需要知道内部调用了哪些Repository
     * - 不需要知道业务验证逻辑
     * - 所有复杂的子系统交互都被封装在此方法中
     * ============================================
     */
    @Transactional
    public void deleteBuilding(Long id) {
        // ============================================
        // 外观模式：封装子系统调用
        // 调用BuildingRepository查找建筑
        // ============================================
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + id));
        
        // ============================================
        // 外观模式：封装业务验证逻辑
        // 调用DeviceRepository检查关联设备
        // 外观模式：隐藏了多个Repository的交互
        // ============================================
        // 检查是否有关联设备
        long deviceCount = deviceRepository.countByBuildingId(id);
        if (deviceCount > 0) {
            throw new BusinessException("该建筑下还有" + deviceCount + "个设备，无法删除");
        }
        
        // ============================================
        // 外观模式：封装子系统调用
        // 调用BuildingRepository删除建筑
        // ============================================
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
     * 
     * ============================================
     * 设计模式：Facade Pattern（外观模式）
     * ============================================
     * 
     * 外观模式：封装Entity到DTO的转换逻辑
     * 
     * 执行流程：
     * 1. 调用DeviceRepository统计设备数量（子系统调用）
     * 2. 封装Entity属性到DTO
     * 3. 返回DTO对象
     * 
     * 外观模式优势体现：
     * - 隐藏了Entity到DTO的转换细节
     * - 隐藏了需要调用DeviceRepository统计设备数量的逻辑
     * - 客户端不需要知道转换过程
     * ============================================
     */
    private BuildingDTO convertToDTO(Building building) {
        // ============================================
        // 外观模式：封装子系统调用
        // 调用DeviceRepository统计设备数量
        // 外观模式：隐藏了需要查询设备数量的复杂性
        // ============================================
        int deviceCount = (int) deviceRepository.countByBuildingId(building.getId());
        
        // ============================================
        // 外观模式：封装数据转换逻辑
        // 将Entity转换为DTO，隐藏转换细节
        // ============================================
        return BuildingDTO.builder()
                .id(building.getId())
                .name(building.getName())
                .locationCode(building.getLocationCode())
                .floorCount(building.getFloorCount())
                .category(building.getCategory())
                .description(building.getDescription())
                .deviceCount(deviceCount)  // 外观模式：封装了设备数量统计
                .createdAt(building.getCreatedAt())
                .updatedAt(building.getUpdatedAt())
                .build();
    }
}

