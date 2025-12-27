package com.energy.management.service;

import com.energy.management.dto.BuildingDTO;
import com.energy.management.entity.Building;
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
public class BuildingService {
    
    private final BuildingRepository buildingRepository;
    private final MeterRepository meterRepository;
    
    public List<BuildingDTO> getAllBuildings() {
        return buildingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public BuildingDTO getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + id));
        return convertToDTO(building);
    }
    
    @Transactional
    public BuildingDTO createBuilding(BuildingDTO dto) {
        if (buildingRepository.existsByLocationCode(dto.getLocationCode())) {
            throw new BusinessException("位置编号已存在: " + dto.getLocationCode());
        }
        
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
    
    @Transactional
    public BuildingDTO updateBuilding(Long id, BuildingDTO dto) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + id));
        
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
    
    @Transactional
    public void deleteBuilding(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("建筑不存在，ID: " + id));
        
        long deviceCount = meterRepository.countByBuildingId(id);
        if (deviceCount > 0) {
            throw new BusinessException("该建筑下还有" + deviceCount + "个设备，无法删除");
        }
        
        buildingRepository.delete(building);
        log.info("删除建筑成功: {}", building.getName());
    }
    
    public List<String> getAllCategories() {
        return buildingRepository.findAllCategories();
    }
    
    private BuildingDTO convertToDTO(Building building) {
        int deviceCount = (int) meterRepository.countByBuildingId(building.getId());
        
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

