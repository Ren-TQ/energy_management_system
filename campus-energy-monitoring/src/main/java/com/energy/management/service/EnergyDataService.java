package com.energy.management.service;

import com.energy.management.dto.chart.ChartData;
import com.energy.management.dto.chart.PowerTrendData;
import com.energy.management.dto.response.EnergyDataResponse;
import com.energy.management.entity.EnergyData;

import java.time.LocalDate;
import java.util.List;

public interface EnergyDataService {
    EnergyData saveEnergyData(EnergyData data);
    EnergyDataResponse getLatestDataByMeterId(Long meterId);
    List<EnergyDataResponse> getRecentDataByMeterId(Long meterId, int limit);
    List<EnergyDataResponse> getHistoryData(Long meterId, LocalDate startDate, LocalDate endDate);
    PowerTrendData getPowerTrendData(Long meterId, int limit);
    ChartData getTodayEnergyDistribution();
    Double calculateTodayCost();
}