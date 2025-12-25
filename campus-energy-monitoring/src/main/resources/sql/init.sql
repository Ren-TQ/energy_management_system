-- ============================================
-- 智慧校园能耗监测与管理平台 - 数据库初始化脚本
-- 此文件用于 Spring Boot 自动初始化数据库
-- ============================================

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_login DATETIME COMMENT '最后登录时间',
    INDEX idx_role (role),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建建筑表
CREATE TABLE IF NOT EXISTS building (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '建筑名称',
    location_code VARCHAR(50) COMMENT '位置编号',
    floors INT COMMENT '楼层数',
    type VARCHAR(50) COMMENT '建筑类型',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_name (name),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='建筑信息表';

-- 创建智能电表设备表
CREATE TABLE IF NOT EXISTS meter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    serial_number VARCHAR(100) NOT NULL UNIQUE COMMENT '唯一设备序列号(SN)',
    status VARCHAR(20) NOT NULL DEFAULT 'ONLINE' COMMENT '设备状态',
    power_threshold DOUBLE NOT NULL COMMENT '功率阈值(W)',
    building_id BIGINT NOT NULL COMMENT '所属建筑ID',
    room_number VARCHAR(50) NOT NULL COMMENT '房间号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    FOREIGN KEY (building_id) REFERENCES building(id) ON DELETE CASCADE,
    UNIQUE KEY uk_building_room_active (building_id, room_number, is_active),
    INDEX idx_status (status),
    INDEX idx_building_id (building_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能电表设备表';

-- 创建能耗数据表
CREATE TABLE IF NOT EXISTS energy_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    meter_id BIGINT NOT NULL COMMENT '所属电表ID',
    voltage DOUBLE COMMENT '电压(V)',
    current DOUBLE COMMENT '电流(A)',
    power DOUBLE NOT NULL COMMENT '功率(W)',
    energy DOUBLE COMMENT '累计用电量(kWh)',
    collect_time DATETIME NOT NULL COMMENT '采集时间',
    FOREIGN KEY (meter_id) REFERENCES meter(id) ON DELETE CASCADE,
    INDEX idx_meter_id (meter_id),
    INDEX idx_collect_time (collect_time),
    INDEX idx_meter_time (meter_id, collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能耗数据表';

-- 创建告警记录表
CREATE TABLE IF NOT EXISTS alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    meter_id BIGINT NOT NULL COMMENT '所属电表ID',
    alert_type VARCHAR(50) NOT NULL COMMENT '告警类型',
    alert_value DOUBLE COMMENT '告警数值',
    threshold_value DOUBLE COMMENT '阈值',
    description VARCHAR(500) COMMENT '告警描述',
    is_resolved BOOLEAN DEFAULT FALSE COMMENT '是否已处理',
    resolved_at DATETIME COMMENT '处理时间',
    resolve_note VARCHAR(500) COMMENT '处理备注',
    trigger_time DATETIME NOT NULL COMMENT '触发时间',
    FOREIGN KEY (meter_id) REFERENCES meter(id) ON DELETE CASCADE,
    INDEX idx_meter_id (meter_id),
    INDEX idx_trigger_time (trigger_time),
    INDEX idx_is_resolved (is_resolved),
    INDEX idx_alert_type (alert_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

-- ============================================
-- 初始化数据（如果不存在）
-- 注意：密码为 123456 (BCrypt加密后的值)
-- ============================================

-- 初始化用户数据
-- 密码: 123456 (BCrypt: $2a$10$EIxjE5xvGvvSzVH5vwG1T.8cXKBNvN8wLqQaQ0hC6.Vx5L5gK2Xvi)
INSERT IGNORE INTO users (username, password, email, role, created_at) VALUES
('admin', '$2a$10$EIxjE5xvGvvSzVH5vwG1T.8cXKBNvN8wLqQaQ0hC6.Vx5L5gK2Xvi', 'admin@campus.edu', 'ADMIN', NOW()),
('user', '$2a$10$EIxjE5xvGvvSzVH5vwG1T.8cXKBNvN8wLqQaQ0hC6.Vx5L5gK2Xvi', 'user@campus.edu', 'USER', NOW());

-- 初始化建筑数据
INSERT IGNORE INTO building (id, name, location_code, floors, type, created_at) VALUES
(1, '力行楼', 'BLD_LX_001', 6, 'CLASSROOM', NOW()),
(2, '椒苑宿舍三号楼', 'BLD_DORM_003', 7, 'DORMITORY', NOW()),
(3, '图书馆', 'BLD_LIB_001', 5, 'LIBRARY', NOW());

-- 初始化设备数据（10个智能电表）
INSERT IGNORE INTO meter (id, device_name, serial_number, status, power_threshold, building_id, room_number, created_at, is_active) VALUES
-- 力行楼设备 (4个)
(1, '实验室电表A', 'METER_LX_LAB_301', 'ONLINE', 5000.0, 1, '301', NOW(), true),
(2, '实验室电表B', 'METER_LX_LAB_302', 'ONLINE', 5000.0, 1, '302', NOW(), true),
(3, '教室电表', 'METER_LX_CLASS_201', 'ONLINE', 3000.0, 1, '201', NOW(), true),
(4, '办公室电表', 'METER_LX_OFFICE_401', 'ONLINE', 2000.0, 1, '401', NOW(), true),
-- 宿舍楼设备 (4个)
(5, '宿舍电表301', 'METER_DORM_301', 'ONLINE', 1000.0, 2, '301', NOW(), true),
(6, '宿舍电表302', 'METER_DORM_302', 'ONLINE', 1000.0, 2, '302', NOW(), true),
(7, '宿舍电表303', 'METER_DORM_303', 'ONLINE', 1000.0, 2, '303', NOW(), true),
(8, '宿舍电表304', 'METER_DORM_304', 'ONLINE', 1000.0, 2, '304', NOW(), true),
-- 图书馆设备 (2个)
(9, '阅览室电表', 'METER_LIB_READ_301', 'ONLINE', 3000.0, 3, '三楼阅览室', NOW(), true),
(10, '公共区域电表', 'METER_LIB_HALL_001', 'ONLINE', 6000.0, 3, '一楼大厅', NOW(), true);