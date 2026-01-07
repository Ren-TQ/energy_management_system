-- ============================================
-- 智慧校园能耗监测与管理平台 - 初始数据
-- 执行前请先执行 schema.sql 创建表结构
-- ============================================

USE energy_20231120043;

-- ============================================
-- 1. 初始化用户数据
-- 密码均为: 123456 (BCrypt加密)
-- ============================================
-- 密码: 123456 (BCrypt加密后的值)
INSERT INTO t_user (username, password, real_name, email, phone, role, enabled) VALUES
('admin', '$2a$10$EIxjE5xvGvvSzVH5vwG1T.8cXKBNvN8wLqQaQ0hC6.Vx5L5gK2Xvi', '系统管理员', 'admin@campus.edu', '13800000001', 'ADMIN', 1),
('user', '$2a$10$EIxjE5xvGvvSzVH5vwG1T.8cXKBNvN8wLqQaQ0hC6.Vx5L5gK2Xvi', '普通用户', 'user@campus.edu', '13800000002', 'USER', 1);

-- ============================================
-- 2. 初始化建筑数据 (至少2栋建筑)
-- ============================================
INSERT INTO t_building (name, location_code, floor_count, category, description) VALUES
('楸苑宿舍三号楼', 'BLD_QIU_003', 7, '宿舍楼', '学生宿舍楼，共7层，每层20间宿舍'),
('力行楼', 'BLD_LIXING_001', 6, '教学楼', '教学楼，包含多个教室和阶梯教室'),
('软件学院楼', 'BLD_SOFT_001', 6, '教学楼', '软件学院楼，包含实验室和办公室'),
('图书馆', 'BLD_LIB_001', 5, '图书馆', '校图书馆，包含阅览室、自习室和多功能报告厅');

-- ============================================
-- 3. 初始化智能电表设备数据 (10个设备)
-- ============================================

-- 楸苑宿舍三号楼设备 (3个)
INSERT INTO t_device (name, serial_number, status, rated_power, building_id, room_number, usage_description) VALUES
('宿舍智能电表-01', 'METER_QIU_301', 'ONLINE', 1000, 1, '301', '严控违规电器(易跳闸)'),
('宿舍智能电表-02', 'METER_QIU_302', 'ONLINE', 1000, 1, '302', '严控违规电器(易跳闸)'),
('宿舍智能电表-03', 'METER_QIU_303', 'ONLINE', 1000, 1, '303', '严控违规电器(易跳闸)');

-- 力行楼设备 (3个)
INSERT INTO t_device (name, serial_number, status, rated_power, building_id, room_number, usage_description) VALUES
('教室智能电表-01', 'METER_LIXING_101', 'ONLINE', 3500, 2, '101', '普通教室(含柜式空调)'),
('教室智能电表-02', 'METER_LIXING_102', 'ONLINE', 3500, 2, '102', '普通教室(含柜式空调)'),
('阶梯教室主控表', 'METER_LIXING_205', 'ONLINE', 7000, 2, '205', '双空调+多媒体大屏');

-- 软件学院楼设备 (2个)
INSERT INTO t_device (name, serial_number, status, rated_power, building_id, room_number, usage_description) VALUES
('实验室专用电表', 'METER_SOFT_LAB1', 'ONLINE', 12000, 3, '306', '高性能计算实验室'),
('办公室电表', 'METER_SOFT_OFFICE', 'ONLINE', 4000, 3, '402', '教师办公室');

-- 图书馆设备 (2个)
INSERT INTO t_device (name, serial_number, status, rated_power, building_id, room_number, usage_description) VALUES
('公共区域电表', 'METER_LIB_HALL', 'ONLINE', 6000, 4, '一楼大厅', '中央空调风柜+照明'),
('阅览室电表', 'METER_LIB_READ', 'ONLINE', 3000, 4, '三楼阅览室', '密集照明+插座');

-- ============================================
-- 4. 查看插入结果
-- ============================================
SELECT '用户数据:' AS '';
SELECT id, username, real_name, role, enabled FROM t_user;

SELECT '建筑数据:' AS '';
SELECT id, name, location_code, floor_count, category FROM t_building;

SELECT '设备数据:' AS '';
SELECT d.id, d.name, d.serial_number, d.rated_power, b.name AS building_name, d.room_number 
FROM t_device d 
JOIN t_building b ON d.building_id = b.id;

