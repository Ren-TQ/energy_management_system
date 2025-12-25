package com.energy.management.dto.chart;

import lombok.Data;
import java.util.List;

@Data
public class ChartData {
    private List<String> labels;
    private List<Double> values;

    public ChartData(List<String> labels, List<Double> values) {
        this.labels = labels;
        this.values = values;
    }
}