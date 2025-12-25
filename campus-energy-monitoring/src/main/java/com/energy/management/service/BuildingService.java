package com.energy.management.service;

import com.energy.management.dto.request.BuildingRequest;
import com.energy.management.dto.response.BuildingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BuildingService {
    BuildingResponse createBuilding(BuildingRequest request);
    BuildingResponse updateBuilding(Long id, BuildingRequest request);
    void deleteBuilding(Long id);
    BuildingResponse getBuildingById(Long id);
    Page<BuildingResponse> getAllBuildings(Pageable pageable);
    List<BuildingResponse> getAllBuildings();
}