package com.energy.management.service.impl;

import com.energy.management.dto.chart.ChartData;
import com.energy.management.dto.chart.PowerTrendData;
import com.energy.management.dto.response.EnergyDataResponse;
import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import com.energy.management.exception.BusinessException;
import com.energy.management.repository.EnergyDataRepository;
import com.energy.management.repository.MeterRepository;
import com.energy.management.service.EnergyDataService;
import com.energy.management.service.strategy.CostCalculatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EnergyDataServiceImpl implements EnergyDataService {

    @Autowired
    private EnergyDataRepository energyDataRepository;

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private CostCalculatorContext costCalculatorContext;

    @Override
    @Transactional
    public EnergyData saveEnergyData(EnergyData data) {
        // 验证数据逻辑一致性
        validateEnergyData(data);

        EnergyData savedData = energyDataRepository.save(data);
        log.debug("保存能耗数据: 设备{} 功率{}W",
                data.getMeter().getSerialNumber(), data.getPower());

        return savedData;
    }

    private void validateEnergyData(EnergyData data) {
        // 验证物理公式 P = U×I
        double calculatedPower = data.getVoltage() * data.getCurrent();
        double tolerance = 0.01; // 1%容差

        if (Math.abs(data.getPower() - calculatedPower) > tolerance) {
            throw new BusinessException(String.format(
                    "数据逻辑错误: 功率(%.2fW) ≠ 电压(%.2fV) × 电流(%.2fA) = %.2fW",
                    data.getPower(), data.getVoltage(), data.getCurrent(), calculatedPower
            ));
        }

        // 验证电压范围
        if (data.getVoltage() < 0 || data.getVoltage() > 1000) {
            throw new BusinessException(String.format(
                    "电压值异常: %.2fV (应在0-1000V范围内)", data.getVoltage()
            ));
        }
    }

    @Override
    public EnergyDataResponse getLatestDataByMeterId(Long meterId) {
        List<EnergyData> dataList = energyDataRepository.findLatestDataByMeter(meterId, 1);

        if (dataList.isEmpty()) {
            return null;
        }

        return EnergyDataResponse.fromEntity(dataList.get(0));
    }

    @Override
    public List<EnergyDataResponse> getRecentDataByMeterId(Long meterId, int limit) {
        List<EnergyData> dataList = energyDataRepository.findLatestDataByMeter(meterId, limit);

        return dataList.stream()
                .map(EnergyDataResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnergyDataResponse> getHistoryData(Long meterId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<EnergyData> dataList = energyDataRepository.findByMeterIdAndTimestampBetween(meterId, start, end);

        return dataList.stream()
                .map(EnergyDataResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PowerTrendData getPowerTrendData(Long meterId, int limit) {
        List<EnergyData> dataList = energyDataRepository.findLatestDataByMeter(meterId, limit);

        Collections.reverse(dataList); // 按时间顺序

        List<String> timestamps = dataList.stream()
                .map(data -> data.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .collect(Collectors.toList());

        List<Double> powerValues = dataList.stream()
                .map(EnergyData::getPower)
                .collect(Collectors.toList());

        return new PowerTrendData(timestamps, powerValues);
    }

    @Override
    public ChartData getTodayEnergyDistribution() {
        // 获取所有建筑
        List<Object[]> results = energyDataRepository.findAll()
                .stream()
                .filter(data -> data.getTimestamp().toLocalDate().equals(LocalDate.now()))
                .collect(Collectors.groupingBy(
                        data -> data.getMeter().getBuilding().getName(),
                        Collectors.summingDouble(EnergyData::getEnergyConsumption)
                ))
                .entrySet().stream()
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()})
                .collect(Collectors.toList());

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Object[] result : results) {
            labels.add((String) result[0]);
            values.add((Double) result[1]);
        }

        return new ChartData(labels, values);
    }

    @Override
    public Double calculateTodayCost() {
        // 计算今日总能耗
        Double totalEnergy = energyDataRepository.findAll()
                .stream()
                .filter(data -> data.getTimestamp().toLocalDate().equals(LocalDate.now()))
                .mapToDouble(EnergyData::getEnergyConsumption)
                .sum();

        // 使用策略模式计算费用
        String period = costCalculatorContext.isPeakHour() ? "peak" : "offPeak";
        return costCalculatorContext.calculate(period, totalEnergy);
    }
}