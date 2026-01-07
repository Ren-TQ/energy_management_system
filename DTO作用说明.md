# DTO（数据传输对象）作用说明

## 📋 什么是DTO

**DTO（Data Transfer Object）** 是数据传输对象，用于在不同层之间传输数据，特别是在Controller层和前端之间。

---

## 🎯 DTO的核心作用

### 1. **保护领域模型（Entity）**

#### 问题场景
如果直接暴露Entity给前端，会带来以下问题：
- 暴露数据库表结构细节
- 暴露内部实现细节
- 可能泄露敏感信息
- Entity与API接口强耦合

#### DTO解决方案
```java
// ❌ 不推荐：直接返回Entity
@GetMapping
public List<Device> getAllDevices() {
    return deviceRepository.findAll();  // 暴露了Entity内部结构
}

// ✅ 推荐：返回DTO
@GetMapping
public Result<List<DeviceDTO>> getAllDevices() {
    return Result.success(deviceService.getAllDevices());  // 返回DTO
}
```

#### 项目中的体现

**Entity（Device.java）**：
```java
@Entity
public class Device {
    @Id
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)  // 延迟加载
    @JoinColumn(name = "building_id")
    private Building building;  // 关联对象，包含完整建筑信息
    
    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private List<EnergyData> energyDataList;  // 关联的能耗数据列表
    
    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private List<Alert> alerts;  // 关联的告警列表
    
    // ... 其他字段
}
```

**DTO（DeviceDTO.java）**：
```java
public class DeviceDTO {
    private Long id;
    private String name;
    private String serialNumber;
    private DeviceStatus status;
    private Long buildingId;        // 只返回ID，不返回完整Building对象
    private String buildingName;     // 只返回建筑名称，便于前端显示
    // 不包含 energyDataList 和 alerts，减少数据传输量
}
```

**优势**：
- ✅ 隐藏了Entity的关联关系（`building`、`energyDataList`、`alerts`）
- ✅ 只返回前端需要的数据（`buildingId`、`buildingName`）
- ✅ 避免了延迟加载问题（LazyInitializationException）

---

### 2. **控制数据传输内容**

#### 场景：不同接口返回不同数据

**场景1：列表接口** - 只需要基本信息
```java
// DeviceDTO - 列表接口使用
public class DeviceDTO {
    private Long id;
    private String name;
    private String serialNumber;
    private DeviceStatus status;
    private String buildingName;  // 只包含建筑名称
}
```

**场景2：详情接口** - 需要完整信息
```java
// DeviceDetailDTO - 详情接口使用（可以扩展）
public class DeviceDetailDTO extends DeviceDTO {
    private List<EnergyDataDTO> recentEnergyData;  // 最近能耗数据
    private List<AlertDTO> recentAlerts;            // 最近告警
    private BuildingDTO building;                    // 完整建筑信息
}
```

**项目中的体现**：
- 列表接口返回 `DeviceDTO`（轻量级）
- 详情接口可以返回扩展的DTO（包含关联数据）

---

### 3. **数据验证（Validation）**

#### Entity层
```java
@Entity
public class Device {
    @Column(name = "name", nullable = false, length = 100)
    private String name;  // 数据库约束
}
```

#### DTO层（API层验证）
```java
public class DeviceDTO {
    @NotBlank(message = "设备名称不能为空")
    @Size(max = 100, message = "设备名称长度不能超过100")
    private String name;  // API层验证，在Controller中生效
    
    @NotNull(message = "额定功率不能为空")
    @Positive(message = "额定功率必须大于0")
    private Double ratedPower;  // 业务规则验证
}
```

**验证流程**：
```
前端请求 → Controller（@Valid验证DTO） → Service → Repository → Entity
```

**项目中的使用**：
```java
@PostMapping
public Result<DeviceDTO> createDevice(@Valid @RequestBody DeviceDTO dto) {
    // @Valid注解会验证DTO中的验证规则
    // 如果验证失败，会抛出MethodArgumentNotValidException
    return Result.success(deviceService.createDevice(dto));
}
```

---

### 4. **解耦API接口和数据库结构**

#### 问题场景
如果直接使用Entity：
- 数据库表结构变化会影响API接口
- API版本升级困难
- 前后端耦合严重

#### DTO解决方案
```java
// 数据库表结构变化
@Entity
@Table(name = "t_device")
public class Device {
    @Column(name = "device_name")  // 数据库字段名变化
    private String name;
}

// API接口保持不变
public class DeviceDTO {
    private String name;  // API字段名不变，前端不受影响
}
```

**项目中的体现**：
- Entity字段名：`serial_number`（数据库命名）
- DTO字段名：`serialNumber`（Java驼峰命名）
- 前端接收：`serialNumber`（JSON格式）

---

### 5. **减少网络传输量**

#### 场景对比

**直接返回Entity**：
```json
{
  "id": 1,
  "name": "设备1",
  "building": {
    "id": 1,
    "name": "建筑1",
    "locationCode": "BLD001",
    "floorCount": 6,
    "category": "教学楼",
    "description": "...",
    "devices": [ /* 所有设备列表 */ ],  // 循环引用！
    "createdAt": "...",
    "updatedAt": "..."
  },
  "energyDataList": [ /* 所有能耗数据 */ ],  // 大量数据！
  "alerts": [ /* 所有告警 */ ],  // 大量数据！
  "createdAt": "...",
  "updatedAt": "..."
}
```

**返回DTO**：
```json
{
  "id": 1,
  "name": "设备1",
  "serialNumber": "METER_001",
  "status": "ONLINE",
  "buildingId": 1,
  "buildingName": "建筑1",  // 只返回需要的字段
  "roomNumber": "301"
  // 不包含关联数据，减少传输量
}
```

**优势**：
- ✅ 减少数据传输量（不包含关联对象）
- ✅ 提高API响应速度
- ✅ 减少前端解析时间

---

### 6. **支持API版本控制**

#### 场景：API版本升级

**V1版本DTO**：
```java
public class DeviceDTO {
    private Long id;
    private String name;
    private String serialNumber;
}
```

**V2版本DTO**（新增字段）：
```java
public class DeviceDTOV2 {
    private Long id;
    private String name;
    private String serialNumber;
    private String location;  // 新增字段
    private String manufacturer;  // 新增字段
}
```

**优势**：
- ✅ 旧版本API继续使用V1 DTO
- ✅ 新版本API使用V2 DTO
- ✅ 前端可以逐步迁移

---

### 7. **数据转换和格式化**

#### 项目中的转换示例

**Entity → DTO转换**：
```java
// DeviceService.java
private DeviceDTO convertToDTO(Device device) {
    return DeviceDTO.builder()
            .id(device.getId())
            .name(device.getName())
            .serialNumber(device.getSerialNumber())
            .status(device.getStatus())
            .statusLabel(device.getStatus().getLabel())  // 枚举转字符串
            .ratedPower(device.getRatedPower())
            .buildingId(device.getBuilding().getId())    // 关联对象转ID
            .buildingName(device.getBuilding().getName()) // 关联对象转名称
            .roomNumber(device.getRoomNumber())
            .usageDescription(device.getUsageDescription())
            .createdAt(device.getCreatedAt())
            .updatedAt(device.getUpdatedAt())
            .build();
}
```

**转换内容**：
- ✅ 枚举值转换为可读标签（`status` → `statusLabel`）
- ✅ 关联对象转换为ID和名称（`building` → `buildingId` + `buildingName`）
- ✅ 过滤不需要的字段（不包含`energyDataList`、`alerts`）

---

## 📊 DTO vs Entity 对比

| 特性 | Entity | DTO |
|------|--------|-----|
| **用途** | 数据持久化 | 数据传输 |
| **包含内容** | 所有字段 + 关联关系 | 只包含需要的字段 |
| **JPA注解** | ✅ 有（@Entity、@Table等） | ❌ 无 |
| **验证注解** | ❌ 无 | ✅ 有（@NotBlank、@NotNull等） |
| **关联关系** | ✅ 有（@OneToMany、@ManyToOne） | ❌ 无（只包含ID或名称） |
| **序列化** | 可能有问题（延迟加载） | ✅ 安全 |
| **版本控制** | 困难 | ✅ 容易 |

---

## 🔄 项目中的数据流转

### 完整流程

```
1. 前端请求
   ↓
2. Controller层
   - 接收DTO（@RequestBody DeviceDTO）
   - 验证DTO（@Valid）
   ↓
3. Service层
   - DTO → Entity转换
   - 业务逻辑处理
   - Entity → DTO转换
   ↓
4. Repository层
   - 保存/查询Entity
   ↓
5. Controller层
   - 返回DTO（Result<DeviceDTO>）
   ↓
6. JSON响应
   - 序列化为JSON
   ↓
7. 前端接收
   - 解析JSON为对象
```

### 代码示例

**Controller层**：
```java
@PostMapping
public Result<DeviceDTO> createDevice(@Valid @RequestBody DeviceDTO dto) {
    // 接收DTO，验证通过后调用Service
    return Result.success(deviceService.createDevice(dto));
}
```

**Service层**：
```java
@Transactional
public DeviceDTO createDevice(DeviceDTO dto) {
    // 1. DTO → Entity转换
    Device device = Device.builder()
            .name(dto.getName())
            .serialNumber(dto.getSerialNumber())
            .ratedPower(dto.getRatedPower())
            .building(buildingRepository.findById(dto.getBuildingId()).orElseThrow())
            .roomNumber(dto.getRoomNumber())
            .build();
    
    // 2. 保存Entity
    device = deviceRepository.save(device);
    
    // 3. Entity → DTO转换
    return convertToDTO(device);
}
```

---

## ✅ DTO的优势总结

1. **安全性**
   - 隐藏内部实现细节
   - 防止敏感信息泄露
   - 避免暴露数据库结构

2. **性能**
   - 减少数据传输量
   - 避免延迟加载问题
   - 提高API响应速度

3. **灵活性**
   - 支持API版本控制
   - 不同接口返回不同数据
   - 易于扩展和修改

4. **可维护性**
   - API接口与数据库解耦
   - 清晰的职责分离
   - 易于测试

5. **数据验证**
   - API层数据验证
   - 业务规则验证
   - 统一的错误处理

---

## 📝 项目中的DTO使用

### DTO列表

| DTO类 | 对应Entity | 用途 |
|------|----------|------|
| `DeviceDTO` | `Device` | 设备信息传输 |
| `BuildingDTO` | `Building` | 建筑信息传输 |
| `EnergyDataDTO` | `EnergyData` | 能耗数据传输 |
| `AlertDTO` | `Alert` | 告警信息传输 |
| `UserDTO` | `User` | 用户信息传输 |
| `StatisticsDTO` | - | 统计数据传输（聚合数据） |
| `Result<T>` | - | 统一响应格式 |

### 转换方法位置

所有Service类都有`convertToDTO()`方法：
- `DeviceService.convertToDTO(Device)`
- `BuildingService.convertToDTO(Building)`
- `EnergyDataService.convertToDTO(EnergyData)`
- `AlertService.convertToDTO(Alert)`
- `AuthService.convertToDTO(User)`

---

## 💡 最佳实践

1. **每个Entity对应一个DTO**
   - 保持一对一关系
   - 便于管理和维护

2. **DTO只包含需要的字段**
   - 不包含关联对象
   - 只包含ID或关键信息

3. **使用验证注解**
   - `@NotBlank`、`@NotNull`、`@Positive`等
   - 在Controller层使用`@Valid`验证

4. **使用Builder模式**
   - Lombok的`@Builder`注解
   - 便于对象构建

5. **转换方法放在Service层**
   - 集中管理转换逻辑
   - 便于复用和维护

---

## 🔍 常见问题

### Q1: 为什么不直接使用Entity？

**A**: 
- Entity包含JPA注解和关联关系，不适合序列化
- 直接暴露Entity会泄露数据库结构
- 延迟加载会导致LazyInitializationException

### Q2: DTO和VO（Value Object）有什么区别？

**A**: 
- **DTO**: 用于层间数据传输，关注数据传输
- **VO**: 用于展示层，关注数据展示（可以包含格式化后的数据）

### Q3: 每个接口都需要DTO吗？

**A**: 
- 对外API接口：必须使用DTO
- 内部服务调用：可以使用Entity
- 简单查询：可以使用DTO

### Q4: DTO转换性能如何？

**A**: 
- 转换开销很小（只是对象属性复制）
- 相比网络传输和数据库查询，可以忽略
- 可以使用MapStruct等工具优化

---

## 📚 相关文档

- 项目中的DTO类：`com.campus.energy.dto` 包
- 转换方法：各Service类的`convertToDTO()`方法
- 验证规则：DTO类中的验证注解

