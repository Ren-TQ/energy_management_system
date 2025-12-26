package com.energy.management.config;

import com.energy.management.entity.Building;
import com.energy.management.entity.Meter;
import com.energy.management.entity.User;
import com.energy.management.enums.DeviceStatus;
import com.energy.management.enums.UserRole;
import com.energy.management.repository.BuildingRepository;
import com.energy.management.repository.MeterRepository;
import com.energy.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final BuildingRepository buildingRepository;
    private final MeterRepository meterRepository;
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
    
    private void initUsers() {
        if (userRepository.count() > 0) {
            log.info("用户数据已存在，跳过初始化");
            return;
        }
        
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setEmail("admin@campus.edu");
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
        log.info("创建管理员账号: admin");
        
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setEmail("user@campus.edu");
        user.setRole(UserRole.USER);
        userRepository.save(user);
        log.info("创建普通用户账号: user");
    }
    
    private void initBuildingsAndDevices() {
        if (buildingRepository.count() > 0) {
            log.info("建筑数据已存在，跳过初始化");
            return;
        }
        
        Building lixingBuilding = Building.builder()
                .name("力行楼")
                .locationCode("BLD_LX_001")
                .floorCount(6)
                .category("教学楼")
                .description("主要用于教学和实验")
                .build();
        lixingBuilding = buildingRepository.save(lixingBuilding);
        log.info("创建建筑: {}", lixingBuilding.getName());
        
        Building dormBuilding = Building.builder()
                .name("椒苑宿舍三号楼")
                .locationCode("BLD_DORM_003")
                .floorCount(7)
                .category("宿舍楼")
                .description("学生宿舍楼")
                .build();
        dormBuilding = buildingRepository.save(dormBuilding);
        log.info("创建建筑: {}", dormBuilding.getName());
        
        Building libraryBuilding = Building.builder()
                .name("图书馆")
                .locationCode("BLD_LIB_001")
                .floorCount(5)
                .category("图书馆")
                .description("图书阅览和自习")
                .build();
        libraryBuilding = buildingRepository.save(libraryBuilding);
        log.info("创建建筑: {}", libraryBuilding.getName());
        
        createDevices(lixingBuilding, dormBuilding, libraryBuilding);
    }
    
    private void createDevices(Building lixing, Building dorm, Building library) {
        createDevice("实验室电表A", "METER_LX_LAB_301", lixing, "301", 5000.0);
        createDevice("实验室电表B", "METER_LX_LAB_302", lixing, "302", 5000.0);
        createDevice("教室电表", "METER_LX_CLASS_201", lixing, "201", 3000.0);
        createDevice("办公室电表", "METER_LX_OFFICE_401", lixing, "401", 2000.0);
        
        createDevice("宿舍电表301", "METER_DORM_301", dorm, "301", 1000.0);
        createDevice("宿舍电表302", "METER_DORM_302", dorm, "302", 1000.0);
        createDevice("宿舍电表303", "METER_DORM_303", dorm, "303", 1000.0);
        createDevice("宿舍电表304", "METER_DORM_304", dorm, "304", 1000.0);
        
        createDevice("阅览室电表", "METER_LIB_READ_301", library, "三楼阅览室", 3000.0);
        createDevice("公共区域电表", "METER_LIB_HALL_001", library, "一楼大厅", 6000.0);
        
        log.info("创建了 10 个智能电表设备");
    }
    
    private void createDevice(String name, String serialNumber, Building building, 
                              String roomNumber, Double ratedPower) {
        Meter meter = Meter.builder()
                .name(name)
                .serialNumber(serialNumber)
                .status(DeviceStatus.ONLINE)
                .ratedPower(ratedPower)
                .building(building)
                .roomNumber(roomNumber)
                .build();
        meterRepository.save(meter);
        log.debug("创建设备: {} (SN: {})", name, serialNumber);
    }
}

