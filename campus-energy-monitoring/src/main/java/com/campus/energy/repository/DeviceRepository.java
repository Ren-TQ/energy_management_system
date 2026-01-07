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
 * 设备信息数据访问层
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

