-- ============================================
-- 智慧校园能耗监测与管理平台 - MySQL表结构
-- 数据库: energy_20231120043
-- 字符集: utf8mb4
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS energy_20231120043 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE energy_20231120043;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色: ADMIN-管理员, USER-普通用户',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用: 1-启用, 0-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role (role),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 建筑信息表
-- ============================================
DROP TABLE IF EXISTS t_building;
CREATE TABLE t_building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '建筑名称',
    location_code VARCHAR(50) NOT NULL UNIQUE COMMENT '位置编号',
    floor_count INT NOT NULL COMMENT '楼层数',
    category VARCHAR(50) NOT NULL COMMENT '建筑用途分类',
    description VARCHAR(500) COMMENT '建筑描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='建筑信息表';

-- ============================================
-- 3. 智能电表设备表
-- ============================================
DROP TABLE IF EXISTS t_device;
CREATE TABLE t_device (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '设备名称',
    serial_number VARCHAR(50) NOT NULL UNIQUE COMMENT '唯一设备序列号(SN)',
    status VARCHAR(20) NOT NULL DEFAULT 'ONLINE' COMMENT '通讯状态: ONLINE-在线, OFFLINE-离线, MAINTENANCE-维护中, DECOMMISSIONED-已停用',
    rated_power DOUBLE NOT NULL COMMENT '额定功率阈值(W)',
    building_id BIGINT NOT NULL COMMENT '所属建筑ID',
    room_number VARCHAR(50) NOT NULL COMMENT '房间号',
    usage_description VARCHAR(200) COMMENT '设备用途描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_device_building FOREIGN KEY (building_id) REFERENCES t_building(id) ON DELETE RESTRICT,
    CONSTRAINT uk_building_room UNIQUE (building_id, room_number),
    INDEX idx_status (status),
    INDEX idx_building_id (building_id),
    INDEX idx_serial_number (serial_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能电表设备表';

-- ============================================
-- 4. 能耗数据表
-- ============================================
DROP TABLE IF EXISTS t_energy_data;
CREATE TABLE t_energy_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    device_id BIGINT NOT NULL COMMENT '所属设备ID',
    voltage DOUBLE NOT NULL COMMENT '当前电压(V)',
    current DOUBLE NOT NULL COMMENT '当前电流(A)',
    power DOUBLE NOT NULL COMMENT '当前实时功率(W)',
    total_energy DOUBLE NOT NULL COMMENT '累计用电量(kWh)',
    is_abnormal TINYINT(1) DEFAULT 0 COMMENT '数据是否异常: 1-异常, 0-正常',
    collect_time DATETIME NOT NULL COMMENT '采集时间戳',
    CONSTRAINT fk_energy_device FOREIGN KEY (device_id) REFERENCES t_device(id) ON DELETE CASCADE,
    INDEX idx_device_id (device_id),
    INDEX idx_collect_time (collect_time),
    INDEX idx_device_time (device_id, collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗数据表';

-- ============================================
-- 5. 告警记录表
-- ============================================
DROP TABLE IF EXISTS t_alert;
CREATE TABLE t_alert (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    device_id BIGINT NOT NULL COMMENT '所属设备ID',
    alert_type VARCHAR(50) NOT NULL COMMENT '告警类型: POWER_OVERLOAD-功率过载, VOLTAGE_HIGH-电压过高, VOLTAGE_LOW-电压过低, CURRENT_ABNORMAL-电流异常, DEVICE_OFFLINE-设备离线',
    alert_value DOUBLE NOT NULL COMMENT '告警数值',
    threshold_value DOUBLE COMMENT '阈值',
    description VARCHAR(500) NOT NULL COMMENT '告警详情',
    is_resolved TINYINT(1) DEFAULT 0 COMMENT '是否已处理: 1-已处理, 0-未处理',
    resolved_at DATETIME COMMENT '处理时间',
    resolve_note VARCHAR(500) COMMENT '处理备注',
    trigger_time DATETIME NOT NULL COMMENT '触发时间',
    CONSTRAINT fk_alert_device FOREIGN KEY (device_id) REFERENCES t_device(id) ON DELETE CASCADE,
    INDEX idx_device_id (device_id),
    INDEX idx_trigger_time (trigger_time),
    INDEX idx_is_resolved (is_resolved),
    INDEX idx_alert_type (alert_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

