package com.energy.management.dto.request;

import lombok.Data;

@Data
public class AlertRequest {
    private String serialNumber;
    private String alertType;
    private String startDate;
    private String endDate;
    private Integer page = 0;
    private Integer size = 10;
}