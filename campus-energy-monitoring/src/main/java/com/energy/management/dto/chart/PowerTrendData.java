package com.energy.management.dto.chart;

import lombok.Data;
import java.util.List;

@Data
public class PowerTrendData {
    private List<String> timestamps;
    private List<Double> powerValues;

    public PowerTrendData(List<String> timestamps, List<Double> powerValues) {
        this.timestamps = timestamps;
        this.powerValues = powerValues;
    }
}