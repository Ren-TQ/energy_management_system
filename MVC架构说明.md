# 后端MVC架构说明

## 📐 MVC架构概述

本项目后端采用**MVC（Model-View-Controller）架构**，这是Spring MVC框架的标准架构模式。

---

## 🏗️ MVC三层架构详解

### 1. Controller层（控制器层）- C

**位置**: `com.campus.energy.controller` 包

**职责**:
- 接收HTTP请求（GET、POST、PUT、DELETE等）
- 参数验证和解析
- 调用Service层处理业务逻辑
- 返回JSON响应（View层）

**特点**:
- 使用`@RestController`注解标识
- 不包含业务逻辑，只负责请求转发
- 所有方法返回`Result<T>`统一格式

**示例文件**:
- `DeviceController.java` - 设备管理控制器
- `BuildingController.java` - 建筑管理控制器
- `AlertController.java` - 告警管理控制器
- `EnergyDataController.java` - 能耗数据控制器

**代码示例**:
```java
@RestController
@RequestMapping("/devices")
public class DeviceController {
    private final DeviceService deviceService;  // 调用Model层
    
    @GetMapping
    public Result<List<DeviceDTO>> getAllDevices() {
        // Controller调用Service（Model层）
        // 返回Result作为View层（JSON响应）
        return Result.success(deviceService.getAllDevices());
    }
}
```

---

### 2. Model层（模型层）- M

Model层包含三个部分：

#### 2.1 Service层（业务逻辑层）

**位置**: `com.campus.energy.service` 包

**职责**:
- 处理业务逻辑（业务规则、数据验证、事务管理）
- 调用Repository层访问数据
- Entity与DTO之间的转换

**示例文件**:
- `DeviceService.java` - 设备业务逻辑
- `BuildingService.java` - 建筑业务逻辑
- `AlertService.java` - 告警业务逻辑
- `EnergyDataService.java` - 能耗数据业务逻辑

**代码示例**:
```java
@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;  // 数据访问层
    
    public List<DeviceDTO> getAllDevices() {
        // Model层：调用Repository获取Entity
        // 转换为DTO返回给Controller
        return deviceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
```

#### 2.2 Repository层（数据访问层）

**位置**: `com.campus.energy.repository` 包

**职责**:
- 封装数据访问逻辑
- 提供统一的数据访问接口
- 与数据库交互（通过JPA）

**示例文件**:
- `DeviceRepository.java` - 设备数据访问
- `BuildingRepository.java` - 建筑数据访问
- `AlertRepository.java` - 告警数据访问
- `EnergyDataRepository.java` - 能耗数据访问

**代码示例**:
```java
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    // 通过方法命名约定自动生成查询
    List<Device> findByBuildingId(Long buildingId);
    
    // 自定义查询
    @Query("SELECT d FROM Device d WHERE d.status = 'ONLINE'")
    List<Device> findAllOnlineDevices();
}
```

#### 2.3 Entity层（实体层/数据模型层）

**位置**: `com.campus.energy.entity` 包

**职责**:
- 定义数据模型结构（对应数据库表）
- 使用JPA注解映射数据库表
- 封装业务数据

**示例文件**:
- `Device.java` - 设备实体
- `Building.java` - 建筑实体
- `EnergyData.java` - 能耗数据实体
- `Alert.java` - 告警实体
- `User.java` - 用户实体

**代码示例**:
```java
@Entity
@Table(name = "t_device")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    // ... 其他字段
}
```

---

### 3. View层（视图层）- V

在RESTful API中，View层不是HTML页面，而是**JSON响应格式**。

#### 3.1 Result<T>统一响应类

**位置**: `com.campus.energy.dto.common.Result.java`

**职责**:
- 统一API响应格式
- 封装返回给客户端的数据
- 在RESTful API中，View层是JSON响应格式

**响应结构**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1704067200000
}
```

#### 3.2 DTO（数据传输对象）

**位置**: `com.campus.energy.dto` 包

**职责**:
- 作为View层的数据载体
- 隐藏Entity的内部细节
- 控制返回给前端的数据内容

**示例文件**:
- `DeviceDTO.java` - 设备数据传输对象
- `BuildingDTO.java` - 建筑数据传输对象
- `EnergyDataDTO.java` - 能耗数据传输对象
- `AlertDTO.java` - 告警数据传输对象

---

## 🔄 MVC数据流转

### 完整的数据流转过程：

```
1. HTTP请求
   ↓
2. Controller层（C）
   - 接收请求
   - 参数验证
   ↓
3. Service层（M - 业务逻辑）
   - 处理业务逻辑
   - 数据验证
   ↓
4. Repository层（M - 数据访问）
   - 访问数据库
   ↓
5. Entity实体（M - 数据模型）
   - 映射数据库表
   ↓
6. Service层（M）
   - Entity → DTO转换
   ↓
7. Controller层（C）
   - 封装为Result<T>
   ↓
8. View层（V）
   - JSON响应返回给客户端
```

### 具体示例（获取设备列表）：

```java
// 1. Controller层接收请求
@GetMapping
public Result<List<DeviceDTO>> getAllDevices() {
    // 2. 调用Service层（Model层）
    return Result.success(deviceService.getAllDevices());
}

// 3. Service层处理业务逻辑
public List<DeviceDTO> getAllDevices() {
    // 4. 调用Repository层（Model层）
    return deviceRepository.findAll().stream()
            // 5. Entity → DTO转换
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

// 6. Repository层访问数据库（Model层）
// JPA自动实现，返回Entity列表

// 7. Controller返回Result（View层）
// Spring自动序列化为JSON响应
```

---

## 📊 MVC架构图

```
┌─────────────────────────────────────────────────┐
│              HTTP Request (JSON)                │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│         Controller层 (C)                        │
│  - DeviceController                              │
│  - BuildingController                            │
│  - AlertController                              │
│  职责：接收请求、调用Service、返回响应            │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│         Service层 (M - 业务逻辑)                │
│  - DeviceService                                │
│  - BuildingService                              │
│  - AlertService                                 │
│  职责：处理业务逻辑、数据转换                    │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│      Repository层 (M - 数据访问)                 │
│  - DeviceRepository                             │
│  - BuildingRepository                           │
│  - AlertRepository                               │
│  职责：数据持久化、数据库交互                    │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│        Entity层 (M - 数据模型)                  │
│  - Device                                       │
│  - Building                                     │
│  - Alert                                        │
│  职责：定义数据模型、映射数据库表                │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│              数据库 (MySQL)                      │
└─────────────────────────────────────────────────┘

                   ▲
                   │
                   │ (Entity → DTO转换)
                   │
┌─────────────────────────────────────────────────┐
│         View层 (V)                              │
│  - Result<T> (统一响应格式)                      │
│  - DTO (数据传输对象)                           │
│  职责：JSON响应格式、数据传输                    │
└─────────────────────────────────────────────────┘
```

---

## ✅ MVC架构优势

1. **职责分离**: 每层职责明确，易于维护
2. **解耦**: Controller、Service、Repository相互独立
3. **可测试性**: 每层可以独立测试
4. **可扩展性**: 易于添加新功能
5. **代码复用**: Service层可以被多个Controller调用

---

## 📝 代码位置总结

| MVC层 | 包路径 | 主要文件 |
|------|--------|---------|
| **Controller (C)** | `com.campus.energy.controller` | `DeviceController.java`<br>`BuildingController.java`<br>`AlertController.java` |
| **Service (M)** | `com.campus.energy.service` | `DeviceService.java`<br>`BuildingService.java`<br>`AlertService.java` |
| **Repository (M)** | `com.campus.energy.repository` | `DeviceRepository.java`<br>`BuildingRepository.java`<br>`AlertRepository.java` |
| **Entity (M)** | `com.campus.energy.entity` | `Device.java`<br>`Building.java`<br>`EnergyData.java` |
| **View (V)** | `com.campus.energy.dto` | `Result.java`<br>`DeviceDTO.java`<br>`BuildingDTO.java` |

---

## 🔍 如何识别MVC架构

### Controller层特征：
- 使用`@RestController`或`@Controller`注解
- 使用`@RequestMapping`、`@GetMapping`等HTTP映射注解
- 方法返回`Result<T>`或`ResponseEntity<T>`
- 不包含业务逻辑，只调用Service

### Model层特征：
- **Service**: 使用`@Service`注解，包含业务逻辑
- **Repository**: 使用`@Repository`注解，继承`JpaRepository`
- **Entity**: 使用`@Entity`注解，映射数据库表

### View层特征：
- **Result<T>**: 统一响应格式类
- **DTO**: 数据传输对象，用于Controller和前端之间传输数据
- 最终输出为JSON格式

---

## 💡 注意事项

1. **Controller层不包含业务逻辑**: 所有业务逻辑都在Service层
2. **Entity不直接暴露**: 通过DTO传输数据，保护数据模型
3. **View层是JSON**: 在RESTful API中，View不是HTML，而是JSON响应
4. **分层调用**: Controller → Service → Repository，不能跨层调用

---

## 📚 相关文档

- Spring MVC官方文档: https://docs.spring.io/spring-framework/reference/web/webmvc.html
- 项目代码已添加详细注释，可直接查看源码了解MVC架构实现

