package com.energy.management.service.impl;

import com.energy.management.dto.request.MeterRequest;
import com.energy.management.dto.response.MeterResponse;
import com.energy.management.entity.Building;
import com.energy.management.entity.Meter;
import com.energy.management.exception.BusinessException;
import com.energy.management.repository.BuildingRepository;
import com.energy.management.repository.MeterRepository;
import com.energy.management.service.MeterService;
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
public class MeterServiceImpl implements MeterService {

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Override
    @Transactional
    public MeterResponse createMeter(MeterRequest request) {
        // 检查序列号是否已存在
        if (meterRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new BusinessException("设备序列号已存在");
        }

        // 检查建筑是否存在
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new BusinessException("指定的建筑不存在"));

        // Pattern: Business Rule - 检查同一建筑下房间号是否已存在有效设备
        if (meterRepository.existsByBuildingIdAndRoomNumberAndStatus(
                request.getBuildingId(), request.getRoomNumber(), com.energy.management.enums.DeviceStatus.ONLINE)) {
            throw new BusinessException("该建筑下房间号已存在有效设备");
        }

        Meter meter = new Meter();
        meter.setName(request.getName());
        meter.setSerialNumber(request.getSerialNumber());
        meter.setRatedPower(request.getRatedPower());
        meter.setBuilding(building);
        meter.setRoomNumber(request.getRoomNumber());
        if (request.getUsageDescription() != null) {
            meter.setUsageDescription(request.getUsageDescription());
        }

        Meter savedMeter = meterRepository.save(meter);
        log.info("创建设备成功: {}", savedMeter.getName());

        return MeterResponse.fromEntity(savedMeter);
    }

    @Override
    @Transactional
    public MeterResponse updateMeter(Long id, MeterRequest request) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在"));

        // 检查建筑是否存在
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new BusinessException("指定的建筑不存在"));

        // Pattern: Business Rule - 检查序列号冲突（排除自身）
        if (!meter.getSerialNumber().equals(request.getSerialNumber()) &&
                meterRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new BusinessException("设备序列号已存在");
        }

        // Pattern: Business Rule - 检查房间号冲突（排除自身）
        if (!(meter.getBuilding().getId().equals(request.getBuildingId()) && 
                meter.getRoomNumber().equals(request.getRoomNumber())) &&
                meterRepository.existsByBuildingIdAndRoomNumberAndStatus(
                        request.getBuildingId(), request.getRoomNumber())) {
            throw new BusinessException("该建筑下房间号已存在有效设备");
        }

        meter.setName(request.getName());
        meter.setSerialNumber(request.getSerialNumber());
        meter.setRatedPower(request.getRatedPower());
        meter.setBuilding(building);
        meter.setRoomNumber(request.getRoomNumber());

        Meter updatedMeter = meterRepository.save(meter);
        log.info("更新设备成功: {}", updatedMeter.getName());

        return MeterResponse.fromEntity(updatedMeter);
    }

    @Override
    @Transactional
    public void deleteMeter(Long id) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在"));

        meterRepository.delete(meter);
        log.info("删除设备成功: {}", meter.getName());
    }

    @Override
    @Transactional
    public void deactivateMeter(Long id) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在"));

        meter.setStatus(com.energy.management.enums.DeviceStatus.DECOMMISSIONED);
        meterRepository.save(meter);
        log.info("停用设备成功: {}", meter.getName());
    }

    @Override
    public MeterResponse getMeterById(Long id) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("设备不存在"));

        return MeterResponse.fromEntity(meter);
    }

    @Override
    public MeterResponse getMeterBySerialNumber(String serialNumber) {
        Meter meter = meterRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new BusinessException("设备不存在"));

        return MeterResponse.fromEntity(meter);
    }

    @Override
    public Page<MeterResponse> getAllMeters(Pageable pageable) {
        return meterRepository.findAll(pageable)
                .map(MeterResponse::fromEntity);
    }

    @Override
    public List<MeterResponse> getMetersByBuildingId(Long buildingId) {
        // 检查建筑是否存在
        if (!buildingRepository.existsById(buildingId)) {
            throw new BusinessException("指定的建筑不存在");
        }

        return meterRepository.findByBuildingId(buildingId).stream()
                .map(MeterResponse::fromEntity)
                .collect(Collectors.toList());
    }
}