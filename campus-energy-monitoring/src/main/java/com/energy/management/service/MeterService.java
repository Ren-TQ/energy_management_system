package com.energy.management.service;

import com.energy.management.dto.request.MeterRequest;
import com.energy.management.dto.response.MeterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MeterService {
    MeterResponse createMeter(MeterRequest request);
    MeterResponse updateMeter(Long id, MeterRequest request);
    void deleteMeter(Long id);
    MeterResponse getMeterById(Long id);
    MeterResponse getMeterBySerialNumber(String serialNumber);
    Page<MeterResponse> getAllMeters(Pageable pageable);
    List<MeterResponse> getMetersByBuildingId(Long buildingId);
    void deactivateMeter(Long id);
}