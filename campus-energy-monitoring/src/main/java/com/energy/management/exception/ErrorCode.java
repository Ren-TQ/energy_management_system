package com.energy.management.exception;

public enum ErrorCode {
    // 通用错误
    INTERNAL_ERROR(500, "系统内部错误"),

    // 业务错误
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_EXISTS(1002, "用户已存在"),
    INVALID_CREDENTIALS(1003, "用户名或密码错误"),

    // 建筑相关
    BUILDING_NOT_FOUND(2001, "建筑不存在"),
    BUILDING_EXISTS(2002, "建筑名称已存在"),
    BUILDING_HAS_METERS(2003, "建筑下存在设备，无法删除"),

    // 设备相关
    METER_NOT_FOUND(3001, "设备不存在"),
    METER_SN_EXISTS(3002, "设备序列号已存在"),
    ROOM_HAS_ACTIVE_METER(3003, "该房间已存在活跃设备"),

    // 数据相关
    DATA_VALIDATION_ERROR(4001, "数据验证失败"),
    INVALID_PHYSICS_FORMULA(4002, "物理公式验证失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}