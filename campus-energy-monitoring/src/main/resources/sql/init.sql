-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME,
    last_login DATETIME
);

-- 使用更简单的密码哈希，匹配前端提示的密码
INSERT INTO users (username, password, email, role, created_at) VALUES
    ('admin', '$2b$12$QkLZdtZKWHlIvzUbu6M1Fe2GmEMm3rKgsTR6SeLNaTMRT7tc9Yhvq', 'admin@energy.com', 'ADMIN', NOW())
ON DUPLICATE KEY UPDATE 
    password=VALUES(password), 
    email=VALUES(email), 
    role=VALUES(role);

INSERT INTO users (username, password, email, role, created_at) VALUES
    ('user', '$2a$10$NgsO5VNFr9qnMJFYw9WisuH5GOIgaOv3I8qo9OjaBJAHDUaT6bF5i', 'user@energy.com', 'USER', NOW())
ON DUPLICATE KEY UPDATE 
    password=VALUES(password), 
    email=VALUES(email), 
    role=VALUES(role);

-- 创建建筑表
CREATE TABLE IF NOT EXISTS building (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location_code VARCHAR(50) NOT NULL,
    floors INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at DATETIME,
    UNIQUE KEY uk_name (name)
);

INSERT INTO building (name, location_code, floors, type, created_at) VALUES
    ('楸苑宿舍三号楼', 'DORM-003', 6, 'DORMITORY', NOW()),
    ('力行楼', 'TEACH-001', 5, 'CLASSROOM', NOW()),
    ('软件学院楼', 'SOFTWARE-001', 6, 'LABORATORY', NOW()),
    ('图书馆', 'LIB-001', 4, 'LIBRARY', NOW())
ON DUPLICATE KEY UPDATE 
    name=VALUES(name), 
    location_code=VALUES(location_code), 
    floors=VALUES(floors), 
    type=VALUES(type);

-- 创建设备表
CREATE TABLE IF NOT EXISTS meter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_name VARCHAR(100) NOT NULL,
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    power_threshold DOUBLE NOT NULL,
    building_id BIGINT,
    room_number VARCHAR(50),
    created_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (building_id) REFERENCES building(id),
    UNIQUE KEY uk_building_room_active (building_id, room_number, is_active)
);

INSERT INTO meter (device_name, serial_number, status, power_threshold, building_id, room_number, created_at, is_active) VALUES
    ('宿舍智能电表-01', 'METER_QUJ_301', 'ONLINE', 1000.0, 1, '301', NOW(), true),
    ('宿舍智能电表-02', 'METER_QUJ_302', 'ONLINE', 1000.0, 1, '302', NOW(), true),
    ('宿舍智能电表-03', 'METER_QUJ_303', 'ONLINE', 1000.0, 1, '303', NOW(), true),
    ('教室智能电表-01', 'METER_LIXING_101', 'ONLINE', 3500.0, 2, '101', NOW(), true),
    ('教室智能电表-02', 'METER_LIXING_102', 'ONLINE', 3500.0, 2, '102', NOW(), true),
    ('阶梯教室主控表', 'METER_LIXING_205', 'ONLINE', 7000.0, 2, '205', NOW(), true),
    ('实验室专用电表', 'METER_SOFT_LAB1', 'ONLINE', 12000.0, 3, '306', NOW(), true),
    ('办公室电表', 'METER_SOFT_OFFICE', 'ONLINE', 4000.0, 3, '402', NOW(), true),
    ('公共区域电表', 'METER_LIB_HALL', 'ONLINE', 6000.0, 4, '一楼大厅', NOW(), true),
    ('阅览室电表', 'METER_LIB_READ', 'ONLINE', 3000.0, 4, '三楼阅览室', NOW(), true)
ON DUPLICATE KEY UPDATE 
    device_name=VALUES(device_name),
    status=VALUES(status),
    power_threshold=VALUES(power_threshold),
    building_id=VALUES(building_id),
    room_number=VALUES(room_number),
    is_active=VALUES(is_active);