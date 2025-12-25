package com.energy.management.service.impl;

import com.energy.management.dto.request.BuildingRequest;
import com.energy.management.dto.response.BuildingResponse;
import com.energy.management.entity.Building;
import com.energy.management.exception.BusinessException;
import com.energy.management.repository.BuildingRepository;
import com.energy.management.service.BuildingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BuildingServiceImpl implements BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    @Override
    @Transactional
    public BuildingResponse createBuilding(BuildingRequest request) {
        // 检查建筑名称是否已存在
        if (buildingRepository.existsByName(request.getName())) {
            throw new BusinessException("建筑名称已存在");
        }

        Building building = new Building();
        building.setName(request.getName());
        building.setLocationCode(request.getLocationCode());
        building.setFloors(request.getFloors());
        building.setType(request.getType());

        Building savedBuilding = buildingRepository.save(building);
        log.info("创建建筑成功: {}", savedBuilding.getName());

        return BuildingResponse.fromEntity(savedBuilding);
    }

    @Override
    @Transactional
    public BuildingResponse updateBuilding(Long id, BuildingRequest request) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("建筑不存在"));

        // 检查名称冲突（排除自身）
        if (!building.getName().equals(request.getName()) &&
                buildingRepository.existsByName(request.getName())) {
            throw new BusinessException("建筑名称已存在");
        }

        building.setName(request.getName());
        building.setLocationCode(request.getLocationCode());
        building.setFloors(request.getFloors());
        building.setType(request.getType());

        Building updatedBuilding = buildingRepository.save(building);
        log.info("更新建筑成功: {}", updatedBuilding.getName());

        return BuildingResponse.fromEntity(updatedBuilding);
    }

    @Override
    @Transactional
    public void deleteBuilding(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("建筑不存在"));

        // 检查是否有关联的设备
        if (building.getMeters() != null && !building.getMeters().isEmpty()) {
            throw new BusinessException("该建筑下存在设备，无法删除");
        }

        buildingRepository.delete(building);
        log.info("删除建筑成功: {}", building.getName());
    }

    @Override
    public BuildingResponse getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("建筑不存在"));

        return BuildingResponse.fromEntity(building);
    }

    @Override
    public Page<BuildingResponse> getAllBuildings(Pageable pageable) {
        return buildingRepository.findAll(pageable)
                .map(BuildingResponse::fromEntity);
    }

    @Override
    public List<BuildingResponse> getAllBuildings() {
        return buildingRepository.findAll().stream()
                .map(BuildingResponse::fromEntity)
                .collect(Collectors.toList());
    }
}