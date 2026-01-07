package com.campus.energy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 建筑区域信息实体类
 * 用于存储校园内各建筑物的基本信息
 */
@Entity
@Table(name = "t_building")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Building {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 建筑名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * 位置编号
     */
    @Column(name = "location_code", nullable = false, length = 50)
    private String locationCode;
    
    /**
     * 楼层数
     */
    @Column(name = "floor_count", nullable = false)
    private Integer floorCount;
    
    /**
     * 建筑用途分类
     * 如：教学楼、宿舍楼、办公楼、图书馆等
     */
    @Column(name = "category", nullable = false, length = 50)
    private String category;
    
    /**
     * 建筑描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 该建筑下的所有智能电表设备
     */
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Device> devices = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

