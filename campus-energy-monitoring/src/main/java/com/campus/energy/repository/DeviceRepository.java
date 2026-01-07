package com.campus.energy.repository;

import com.campus.energy.entity.Device;
import com.campus.energy.enums.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================
 * MVC架构 - Model层（模型层）- 数据访问部分
 * ============================================
 * 
 * 架构说明：
 * 本接口属于MVC架构中的Model（M）层，负责：
 * 1. 封装数据访问逻辑
 * 2. 提供统一的数据访问接口
 * 3. 与数据库交互（通过JPA）
 * 
 * MVC职责划分：
 * - Controller (C): DeviceController - 接收请求
 * - Model (M): 本接口（Repository层）- 数据访问
 *              DeviceService - 业务逻辑
 *              Device实体 - 数据模型
 * - View (V): Result<DeviceDTO> - JSON响应
 * 
 * 数据流转：
 * Service → Repository（本接口）→ JPA → 数据库
 *              ↓
 *          Entity对象
 *              ↓
 * Service ← Entity（返回给Service层）
 * ============================================
 * 
 * ============================================
 * 设计模式：Repository Pattern（仓储模式）
 * ============================================
 * 
 * 模式类型：数据访问层设计模式
 * 
 * 模式说明：
 * Repository模式将数据访问逻辑封装起来，为业务层提供统一的数据访问接口。
 * 它隔离了业务逻辑和数据访问逻辑，使业务逻辑可以独立于数据访问层进行测试。
 * 
 * 在此项目中的应用：
 * - 继承Spring Data JPA的JpaRepository接口
 * - 提供标准的CRUD操作
 * - 通过方法命名约定自动生成查询方法
 * - 支持自定义查询（@Query注解）
 * 
 * 优势：
 * 1. 统一数据访问接口
 * 2. 业务逻辑与数据访问解耦
 * 3. 易于测试（可以Mock Repository）
 * 4. 支持多种数据源切换
 * 
 * 代码位置：
 * - 所有Repository接口都在com.campus.energy.repository包下
 * - 使用位置：所有Service层通过Repository访问数据
 * ============================================
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    /**
     * 根据序列号查找设备
     */
    Optional<Device> findBySerialNumber(String serialNumber);
    
    /**
     * 检查序列号是否存在
     */
    boolean existsBySerialNumber(String serialNumber);
    
    /**
     * 检查建筑和房间号组合是否已存在设备
     */
    boolean existsByBuildingIdAndRoomNumber(Long buildingId, String roomNumber);
    
    /**
     * 根据建筑ID查找设备列表
     */
    List<Device> findByBuildingId(Long buildingId);
    
    /**
     * 根据状态查找设备列表
     */
    List<Device> findByStatus(DeviceStatus status);
    
    /**
     * 查找所有在线设备
     */
    @Query("SELECT d FROM Device d WHERE d.status = 'ONLINE'")
    List<Device> findAllOnlineDevices();
    
    /**
     * 根据建筑ID和房间号查找设备
     */
    Optional<Device> findByBuildingIdAndRoomNumber(Long buildingId, String roomNumber);
    
    /**
     * 统计各状态设备数量
     */
    long countByStatus(DeviceStatus status);
    
    /**
     * 根据建筑ID统计设备数量
     */
    long countByBuildingId(Long buildingId);
    
    /**
     * 根据设备名称模糊查询
     */
    List<Device> findByNameContaining(String name);
    
    /**
     * 查询指定建筑下指定状态的设备
     */
    @Query("SELECT d FROM Device d WHERE d.building.id = :buildingId AND d.status = :status")
    List<Device> findByBuildingIdAndStatus(@Param("buildingId") Long buildingId, 
                                           @Param("status") DeviceStatus status);
}

