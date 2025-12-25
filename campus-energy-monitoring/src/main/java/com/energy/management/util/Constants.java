package com.energy.management.util;

public class Constants {

    // JWT配置
    public static final long JWT_EXPIRATION = 86400000L; // 24小时

    // 模拟数据配置
    public static final int DATA_GENERATION_INTERVAL = 5000; // 5秒
    public static final int ANOMALY_INTERVAL_MIN = 20;
    public static final int ANOMALY_INTERVAL_MAX = 50;

    // 电压正常范围
    public static final double VOLTAGE_MIN = 198.0;  // -10%
    public static final double VOLTAGE_MAX = 242.0;  // +10%
    public static final double VOLTAGE_STANDARD = 220.0;

    // 电费费率（元/度）
    public static final double PEAK_RATE = 0.8;
    public static final double OFF_PEAK_RATE = 0.4;

    // 时间定义
    public static final int DAY_START_HOUR = 8;
    public static final int DAY_END_HOUR = 22;

    // 分页默认值
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // 设备状态
    public static final String DEVICE_STATUS_ONLINE = "ONLINE";
    public static final String DEVICE_STATUS_OFFLINE = "OFFLINE";

    // 用户角色
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
}