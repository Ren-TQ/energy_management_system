package com.campus.energy.enums;

import lombok.Getter;

/**
 * 告警类型枚举
 */
@Getter
public enum AlertType {
    
    POWER_OVERLOAD("功率过载", "实时功率超过设备额定功率阈值"),
    VOLTAGE_HIGH("电压过高", "电压超过正常范围上限"),
    VOLTAGE_LOW("电压过低", "电压低于正常范围下限"),
    CURRENT_ABNORMAL("电流异常", "电流数据异常"),
    DEVICE_OFFLINE("设备离线", "设备通讯中断");
    
    private final String label;
    private final String description;
    
    AlertType(String label, String description) {
        this.label = label;
        this.description = description;
    }
}

