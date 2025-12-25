-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at DATETIME,
    last_login DATETIME
    );

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