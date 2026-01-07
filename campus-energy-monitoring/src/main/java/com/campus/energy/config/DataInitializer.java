package com.campus.energy.config;

import com.campus.energy.entity.Building;
import com.campus.energy.entity.Device;
import com.campus.energy.entity.User;
import com.campus.energy.enums.DeviceStatus;
import com.campus.energy.enums.UserRole;
import com.campus.energy.repository.BuildingRepository;
import com.campus.energy.repository.DeviceRepository;
import com.campus.energy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 在应用启动时初始化示例数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final BuildingRepository buildingRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        log.info("============================================");
        log.info("开始初始化示例数据...");
        log.info("============================================");
        
        initUsers();
        initBuildingsAndDevices();
        
        log.info("============================================");
        log.info("示例数据初始化完成！");
        log.info("管理员账号: admin / 123456");
        log.info("普通用户: user / 123456");
        log.info("============================================");
    }
    
    /**
     * 初始化用户
     */
    private void initUsers() {
        if (userRepository.count() > 0) {
            log.info("用户数据已存在，跳过初始化");
            return;
        }
        
        // 创建管理员
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                .realName("系统管理员")
                .email("admin@campus.edu")
                .phone("13800000001")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();
        userRepository.save(admin);
        log.info("创建管理员账号: admin");
        
        // 创建普通用户
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("123456"))
                .realName("普通用户")
                .email("user@campus.edu")
                .phone("13800000002")
                .role(UserRole.USER)
                .enabled(true)
                .build();
        userRepository.save(user);
        log.info("创建普通用户账号: user");
    }
    
    /**
     * 初始化建筑和设备
     * 根据项目要求：至少2栋建筑，10个智能电表设备
     */
    private void initBuildingsAndDevices() {
        if (buildingRepository.count() > 0) {
            log.info("建筑数据已存在，跳过初始化");
            return;
        }
        
        // 创建建筑1: 楸苑宿舍三号楼
        Building dormBuilding = Building.builder()
                .name("楸苑宿舍三号楼")
                .locationCode("BLD_QIU_003")
                .floorCount(7)
                .category("宿舍楼")
                .description("学生宿舍楼，共7层，每层20间宿舍")
                .build();
        dormBuilding = buildingRepository.save(dormBuilding);
        log.info("创建建筑: {}", dormBuilding.getName());
        
        // 创建建筑2: 力行楼（教学楼）
        Building lixingBuilding = Building.builder()
                .name("力行楼")
                .locationCode("BLD_LIXING_001")
                .floorCount(6)
                .category("教学楼")
                .description("教学楼，包含多个教室和阶梯教室")
                .build();
        lixingBuilding = buildingRepository.save(lixingBuilding);
        log.info("创建建筑: {}", lixingBuilding.getName());
        
        // 创建建筑3: 软件学院楼
        Building softBuilding = Building.builder()
                .name("软件学院楼")
                .locationCode("BLD_SOFT_001")
                .floorCount(6)
                .category("教学楼")
                .description("软件学院楼，包含实验室和办公室")
                .build();
        softBuilding = buildingRepository.save(softBuilding);
        log.info("创建建筑: {}", softBuilding.getName());
        
        // 创建建筑4: 图书馆
        Building libraryBuilding = Building.builder()
                .name("图书馆")
                .locationCode("BLD_LIB_001")
                .floorCount(5)
                .category("图书馆")
                .description("校图书馆，包含阅览室、自习室和多功能报告厅")
                .build();
        libraryBuilding = buildingRepository.save(libraryBuilding);
        log.info("创建建筑: {}", libraryBuilding.getName());
        
        // 创建10个智能电表设备
        createDevices(dormBuilding, lixingBuilding, softBuilding, libraryBuilding);
    }
    
    /**
     * 创建智能电表设备
     */
    private void createDevices(Building dorm, Building lixing, Building soft, Building library) {
        // 楸苑宿舍三号楼设备 (3个)
        createDevice("宿舍智能电表-01", "METER_QIU_301", dorm, "301", 1000.0, "严控违规电器(易跳闸)");
        createDevice("宿舍智能电表-02", "METER_QIU_302", dorm, "302", 1000.0, "严控违规电器(易跳闸)");
        createDevice("宿舍智能电表-03", "METER_QIU_303", dorm, "303", 1000.0, "严控违规电器(易跳闸)");
        
        // 力行楼设备 (3个)
        createDevice("教室智能电表-01", "METER_LIXING_101", lixing, "101", 3500.0, "普通教室(含柜式空调)");
        createDevice("教室智能电表-02", "METER_LIXING_102", lixing, "102", 3500.0, "普通教室(含柜式空调)");
        createDevice("阶梯教室主控表", "METER_LIXING_205", lixing, "205", 7000.0, "双空调+多媒体大屏");
        
        // 软件学院楼设备 (2个)
        createDevice("实验室专用电表", "METER_SOFT_LAB1", soft, "306", 12000.0, "高性能计算实验室");
        createDevice("办公室电表", "METER_SOFT_OFFICE", soft, "402", 4000.0, "教师办公室");
        
        // 图书馆设备 (2个)
        createDevice("公共区域电表", "METER_LIB_HALL", library, "一楼大厅", 6000.0, "中央空调风柜+照明");
        createDevice("阅览室电表", "METER_LIB_READ", library, "三楼阅览室", 3000.0, "密集照明+插座");
        
        log.info("创建了 10 个智能电表设备");
    }
    
    /**
     * 创建单个设备
     */
    private void createDevice(String name, String serialNumber, Building building, 
                              String roomNumber, Double ratedPower, String description) {
        Device device = Device.builder()
                .name(name)
                .serialNumber(serialNumber)
                .status(DeviceStatus.ONLINE)
                .ratedPower(ratedPower)
                .building(building)
                .roomNumber(roomNumber)
                .usageDescription(description)
                .build();
        deviceRepository.save(device);
        log.debug("创建设备: {} (SN: {})", name, serialNumber);
    }
}

