package com.campus.energy.repository;

import com.campus.energy.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 建筑信息数据访问层
 */
@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    
    /**
     * 根据位置编号查找建筑
     */
    Optional<Building> findByLocationCode(String locationCode);
    
    /**
     * 根据名称查找建筑
     */
    Optional<Building> findByName(String name);
    
    /**
     * 根据建筑用途分类查找建筑列表
     */
    List<Building> findByCategory(String category);
    
    /**
     * 检查位置编号是否存在
     */
    boolean existsByLocationCode(String locationCode);
    
    /**
     * 检查建筑名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 查询所有建筑类别
     */
    @Query("SELECT DISTINCT b.category FROM Building b")
    List<String> findAllCategories();
    
    /**
     * 根据名称模糊查询
     */
    List<Building> findByNameContaining(String name);
}

