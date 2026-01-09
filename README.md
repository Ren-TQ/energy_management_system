# 智慧校园能耗监测与管理平台

## 项目介绍

智慧校园能耗监测与管理平台是一个基于Spring Boot + Vue 3的现代化能耗监测系统，用于对校园内智能电表设备的电压、电流、功率及能耗数据进行实时采集、存储、异常告警及可视化展示。

### 核心功能

- **建筑管理**: 管理校园建筑信息
- **设备管理**: 管理智能电表设备，支持设备状态监控
- **能耗监测**: 实时采集和展示设备能耗数据
- **异常告警**: 自动检测异常并生成告警记录
- **数据可视化**: 多维度数据统计和图表展示
- **用户权限**: 支持管理员和普通用户两种角色

---

## 技术栈

### 后端技术栈

- **框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **安全**: Spring Security + JWT
- **API文档**: Knife4j (Swagger 3)
- **构建工具**: Maven
- **Java版本**: JDK 17+

### 前端技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **UI组件库**: Element Plus
- **图表库**: ECharts
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios

---

## 项目结构

```
energy_management/
├── campus-energy-monitoring/     # 后端项目
│   ├── src/main/java/
│   │   └── com/campus/energy/
│   │       ├── controller/       # 控制器层
│   │       ├── service/         # 服务层
│   │       ├── repository/      # 数据访问层
│   │       ├── entity/          # 实体类
│   │       ├── dto/             # 数据传输对象
│   │       ├── pattern/        # 设计模式实现
│   │       │   ├── factory/     # 工厂模式
│   │       │   ├── observer/    # 观察者模式
│   │       │   └── strategy/    # 策略模式
│   │       ├── security/        # 安全配置
│   │       ├── simulator/       # 数据模拟器
│   │       └── exception/       # 异常处理
│   ├── sql/                     # SQL脚本
│   │   ├── schema.sql          # 表结构
│   │   └── data.sql            # 初始数据
│   └── pom.xml                  # Maven配置
│
└── campus-energy-frontend/       # 前端项目
    ├── src/
    │   ├── api/                 # API接口
    │   ├── views/               # 页面组件
    │   ├── stores/              # 状态管理
    │   ├── router/              # 路由配置
    │   └── utils/               # 工具类
    └── package.json             # 依赖配置
```

---

## 数据库配置

### 1. 创建数据库

数据库名称格式：`energy_20231120043`

```sql
CREATE DATABASE IF NOT EXISTS energy_20231120043 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### 2. 执行SQL脚本

按顺序执行以下SQL文件：

1. **创建表结构**
   ```bash
   mysql -u root -p energy_20231120043 < campus-energy-monitoring/sql/schema.sql
   ```

2. **初始化数据**
   ```bash
   mysql -u root -p energy_20231120043 < campus-energy-monitoring/sql/data.sql
   ```

### 3. 配置数据库连接

编辑 `campus-energy-monitoring/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3309/energy_20231120043?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&allowPublicKeyRetrieval=true
    username: root      # 修改为你的数据库用户名
    password: 123456    # 修改为你的数据库密码
```

### 4. 数据库表说明

- **t_user**: 用户表（管理员、普通用户）
- **t_building**: 建筑信息表
- **t_device**: 智能电表设备表
- **t_energy_data**: 能耗数据表
- **t_alert**: 告警记录表

---

## 启动方式

### 后端启动

1. **环境要求**
   - JDK 17+
   - Maven 3.6+
   - MySQL 8.0+

2. **配置数据库**
   - 修改 `application.yml` 中的数据库连接信息

3. **启动应用**
   ```bash
   cd campus-energy-monitoring
   mvn clean install
   mvn spring-boot:run
   ```

4. **验证启动**
   - 访问: http://localhost:8080/api/swagger-ui.html
   - 查看API文档

### 前端启动

1. **环境要求**
   - Node.js 16+
   - npm 或 pnpm

2. **安装依赖**
   ```bash
   cd campus-energy-frontend
   npm install
   # 或
   pnpm install
   ```

3. **启动开发服务器**
   ```bash
   npm run dev
   # 或
   pnpm dev
   ```

4. **访问应用**
   - 前端地址: http://localhost:5173
   - 后端API: http://localhost:8080/api

---

## 默认账号

### 管理员账号
- **用户名**: `admin`
- **密码**: `123456`
- **权限**: 所有功能（增删改查）

### 普通用户账号
- **用户名**: `user`
- **密码**: `123456`
- **权限**: 仅查看功能

---

## 核心功能说明

### 1. 数据模拟器

- **定时任务**: 每5秒自动生成一次能耗数据
- **数据特征**:
  - 电压: 210V-235V（正态分布）
  - 功率: 根据时间段动态变化
    - 日间（8:00-22:00）: 额定功率的20%-90%
    - 夜间（22:00-8:00）: 10W-100W
  - 电流: 根据公式 `I = P / U` 计算
- **异常注入**: 每30条正常数据生成1条异常数据

### 2. 告警系统

- **告警类型**:
  - 功率过载（超过额定功率120%）
  - 电压过高（>242V）
  - 电压过低（<198V）
- **实现方式**: 观察者模式 + 策略模式
- **告警处理**: 自动保存到数据库并记录日志

### 3. 设计模式

项目实现了6种设计模式，详见代码注释：

1. **Factory Pattern（工厂模式）**: 创建正常/异常能耗数据
2. **Observer Pattern（观察者模式）**: 告警通知机制
3. **Strategy Pattern（策略模式）**: 告警判断策略
4. **Builder Pattern（建造者模式）**: 对象构建
5. **Facade Pattern（外观模式）**: Service层封装
6. **Adapter Pattern（适配器模式）**: API适配

---

## 2.3 核心类设计（UML类图）

### 2.3.1 系统架构概述

本系统采用经典的三层架构模式（Controller-Service-Repository），结合多种设计模式实现业务功能。系统核心类主要分为以下几个层次：

1. **表现层（Controller Layer）**: 负责接收HTTP请求，参数验证，调用服务层
2. **业务层（Service Layer）**: 处理业务逻辑，协调各Repository和设计模式组件
3. **数据访问层（Repository Layer）**: 封装数据库操作，提供数据持久化接口
4. **实体层（Entity Layer）**: 定义数据模型，映射数据库表结构
5. **设计模式层（Pattern Layer）**: 实现工厂模式、观察者模式、策略模式等

### 2.3.2 核心实体类设计

#### 实体类关系图

```
┌─────────────┐
│    User     │
│─────────────│
│ +id: Long   │
│ +username   │
│ +password   │
│ +role       │
│ +enabled    │
└─────────────┘

┌─────────────┐         ┌─────────────┐
│  Building   │◄────────│   Device    │
│─────────────│   1     │─────────────│
│ +id: Long   │    *    │ +id: Long   │
│ +name       │         │ +name       │
│ +locationCode│        │ +serialNumber│
│ +floorCount │         │ +status     │
│ +category   │         │ +ratedPower │
│ +devices    │         │ +building   │
└─────────────┘         │ +roomNumber │
                        └──────┬──────┘
                               │ 1
                               │
                               │ *
                        ┌──────▼──────┐
                        │ EnergyData  │
                        │─────────────│
                        │ +id: Long   │
                        │ +device     │
                        │ +voltage    │
                        │ +current    │
                        │ +power      │
                        │ +totalEnergy│
                        │ +collectTime│
                        │ +isAbnormal │
                        └──────┬──────┘
                               │
                               │ *
                        ┌──────▼──────┐
                        │    Alert    │
                        │─────────────│
                        │ +id: Long   │
                        │ +device     │
                        │ +alertType  │
                        │ +alertValue │
                        │ +description│
                        │ +isResolved │
                        │ +triggerTime│
                        └─────────────┘
```

#### 实体类详细说明

**1. User（用户实体）**
- **职责**: 存储系统用户信息，包括管理员和普通用户
- **主要属性**:
  - `id`: 主键ID
  - `username`: 用户名（唯一）
  - `password`: 加密密码
  - `role`: 用户角色（UserRole枚举：ADMIN/USER）
  - `enabled`: 账户是否启用
  - `lastLoginTime`: 最后登录时间
- **关系**: 独立实体，与其他实体无直接关联

**2. Building（建筑实体）**
- **职责**: 存储校园建筑信息
- **主要属性**:
  - `id`: 主键ID
  - `name`: 建筑名称
  - `locationCode`: 位置编号
  - `floorCount`: 楼层数
  - `category`: 建筑用途分类
  - `description`: 建筑描述
- **关系**: 
  - 一对多关联 `Device`（一个建筑包含多个设备）
  - 通过 `@OneToMany` 注解维护关联关系

**3. Device（设备实体）**
- **职责**: 存储智能电表设备信息
- **主要属性**:
  - `id`: 主键ID
  - `name`: 设备名称
  - `serialNumber`: 设备序列号（唯一）
  - `status`: 设备状态（DeviceStatus枚举：ONLINE/OFFLINE/FAULT）
  - `ratedPower`: 额定功率阈值（W）
  - `roomNumber`: 房间号
  - `usageDescription`: 设备用途描述
- **关系**:
  - 多对一关联 `Building`（多个设备属于一个建筑）
  - 一对多关联 `EnergyData`（一个设备产生多条能耗数据）
  - 一对多关联 `Alert`（一个设备可能产生多条告警记录）

**4. EnergyData（能耗数据实体）**
- **职责**: 存储智能电表采集的实时能耗数据
- **主要属性**:
  - `id`: 主键ID
  - `device`: 所属设备（外键）
  - `voltage`: 当前电压（V）
  - `current`: 当前电流（A）
  - `power`: 当前实时功率（W）
  - `totalEnergy`: 累计用电量（kWh）
  - `collectTime`: 采集时间戳
  - `isAbnormal`: 数据是否异常标记
- **关系**:
  - 多对一关联 `Device`（多条数据属于一个设备）
  - 数据通过工厂模式创建（NormalEnergyDataFactory/AbnormalEnergyDataFactory）

**5. Alert（告警记录实体）**
- **职责**: 存储设备异常告警信息
- **主要属性**:
  - `id`: 主键ID
  - `device`: 所属设备（外键）
  - `alertType`: 告警类型（AlertType枚举：POWER_OVERLOAD/VOLTAGE_HIGH/VOLTAGE_LOW）
  - `alertValue`: 告警数值
  - `thresholdValue`: 阈值（用于对比）
  - `description`: 告警详情
  - `isResolved`: 是否已处理
  - `resolvedAt`: 处理时间
  - `triggerTime`: 触发时间
- **关系**:
  - 多对一关联 `Device`（多条告警属于一个设备）
  - 通过策略模式生成（PowerOverloadAlertStrategy/VoltageAbnormalAlertStrategy）
  - 通过观察者模式通知（AlertSubject → AlertObserver）

### 2.3.3 服务层类设计

#### 服务层类图

```
┌─────────────────────┐
│   AuthService       │
│─────────────────────│
│ +login()            │
│ +register()         │
│ +validateToken()    │
└──────────┬──────────┘
           │
           │ uses
           ▼
┌─────────────────────┐
│  JwtTokenProvider   │
│─────────────────────│
│ +generateToken()    │
│ +validateToken()    │
└─────────────────────┘

┌─────────────────────┐
│  BuildingService    │
│─────────────────────│
│ +getAllBuildings()  │
│ +getBuildingById()  │
│ +createBuilding()   │
│ +updateBuilding()   │
│ +deleteBuilding()   │
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│ BuildingRepository  │
└─────────────────────┘

┌─────────────────────┐
│   DeviceService     │
│─────────────────────│
│ +getAllDevices()    │
│ +getDeviceById()    │
│ +createDevice()     │
│ +updateDevice()     │
│ +deleteDevice()     │
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│  DeviceRepository   │
└─────────────────────┘

┌─────────────────────┐
│ EnergyDataService   │◄─── Facade Pattern
│─────────────────────│
│ +saveEnergyData()   │
│ +getEnergyDataByDeviceId()│
│ +getLatestEnergyData()│
│ +getEnergyDataByTimeRange()│
│ +calculateEnergyConsumption()│
│ +convertToDTO()     │
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│EnergyDataRepository │
└─────────────────────┘

┌─────────────────────┐
│   AlertService      │
│─────────────────────│
│ +checkAlerts()      │
│ +getAllAlerts()     │
│ +resolveAlert()     │
│ +notifyObservers()  │
└──────────┬──────────┘
           │ uses
           ├─────────────────┐
           ▼                 ▼
┌─────────────────────┐  ┌─────────────────────┐
│  AlertRepository    │  │   AlertSubject      │◄─── Observer Pattern
└─────────────────────┘  │─────────────────────│
                         │ +registerObserver() │
                         │ +removeObserver()   │
                         │ +notifyObservers()  │
                         └──────────┬──────────┘
                                    │
                                    │ *
                         ┌──────────▼──────────┐
                         │   AlertObserver     │
                         │────────────────────│
                         │ +onAlert()         │
                         └──────────┬──────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    ▼                               ▼
        ┌─────────────────────┐      ┌─────────────────────┐
        │DatabaseAlertObserver│      │  LogAlertObserver    │
        │─────────────────────│      │─────────────────────│
        │ +onAlert()          │      │ +onAlert()          │
        └─────────────────────┘      └─────────────────────┘

┌─────────────────────┐
│ StatisticsService   │
│─────────────────────│
│ +getBuildingStats() │
│ +getDeviceStats()   │
│ +getEnergyStats()   │
└─────────────────────┘

┌─────────────────────┐
│EnergySimulatorService│
│─────────────────────│
│ +generateEnergyData()│
│ +startSimulation()   │
│ +stopSimulation()    │
└──────────┬──────────┘
           │ uses
           ├─────────────────┐
           ▼                 ▼
┌─────────────────────┐  ┌─────────────────────┐
│EnergyDataRepository │  │  EnergyDataFactory  │◄─── Factory Pattern
└─────────────────────┘  │─────────────────────│
                         │ +createEnergyData() │
                         │ +getFactoryName()  │
                         └──────────┬──────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    ▼                               ▼
        ┌─────────────────────┐      ┌─────────────────────┐
        │NormalEnergyDataFactory│    │AbnormalEnergyDataFactory│
        │─────────────────────│      │─────────────────────│
        │ +createEnergyData() │      │ +createEnergyData() │
        └─────────────────────┘      └─────────────────────┘
```

#### 服务层类详细说明

**1. AuthService（认证服务）**
- **职责**: 处理用户认证和授权逻辑
- **主要方法**:
  - `login(username, password)`: 用户登录，验证凭证并生成JWT令牌
  - `register(userDTO)`: 用户注册
  - `validateToken(token)`: 验证JWT令牌有效性
- **依赖**: `UserRepository`, `JwtTokenProvider`, `PasswordEncoder`

**2. BuildingService（建筑服务）**
- **职责**: 处理建筑相关的业务逻辑
- **主要方法**:
  - `getAllBuildings()`: 获取所有建筑列表
  - `getBuildingById(id)`: 根据ID获取建筑详情
  - `createBuilding(buildingDTO)`: 创建新建筑
  - `updateBuilding(id, buildingDTO)`: 更新建筑信息
  - `deleteBuilding(id)`: 删除建筑
- **依赖**: `BuildingRepository`
- **设计模式**: 使用Builder模式构建Building对象

**3. DeviceService（设备服务）**
- **职责**: 处理设备相关的业务逻辑
- **主要方法**:
  - `getAllDevices()`: 获取所有设备列表
  - `getDeviceById(id)`: 根据ID获取设备详情
  - `getDevicesByBuildingId(buildingId)`: 获取指定建筑下的所有设备
  - `createDevice(deviceDTO)`: 创建新设备
  - `updateDevice(id, deviceDTO)`: 更新设备信息
  - `deleteDevice(id)`: 删除设备
- **依赖**: `DeviceRepository`, `BuildingRepository`
- **设计模式**: 使用Builder模式构建Device对象

**4. EnergyDataService（能耗数据服务）**
- **职责**: 处理能耗数据相关的业务逻辑
- **主要方法**:
  - `saveEnergyData(energyData)`: 保存能耗数据
  - `getEnergyDataByDeviceId(deviceId, pageable)`: 分页获取设备能耗数据
  - `getLatestEnergyData(deviceId)`: 获取设备最新能耗数据
  - `getEnergyDataByTimeRange(deviceId, startTime, endTime)`: 获取时间范围内的能耗数据
  - `calculateEnergyConsumption(deviceId, startTime, endTime)`: 计算指定时间范围内的用电量
  - `getLatestTotalEnergy(deviceId)`: 获取设备最新累计用电量
  - `convertToDTO(energyData)`: 实体转DTO
- **依赖**: `EnergyDataRepository`
- **设计模式**: 
  - **Facade Pattern（外观模式）**: 为Controller层提供简化的接口，隐藏Repository层的复杂交互
  - 封装了Entity到DTO的转换逻辑
  - 封装了分页、时间范围查询等复杂操作

**5. AlertService（告警服务）**
- **职责**: 处理告警相关的业务逻辑，协调策略模式和观察者模式
- **主要方法**:
  - `checkAlerts(device, energyData)`: 检查是否需要触发告警（使用策略模式）
  - `getAllAlerts(pageable)`: 分页获取所有告警记录
  - `getAlertsByDeviceId(deviceId)`: 获取指定设备的告警记录
  - `resolveAlert(alertId, resolveNote)`: 处理告警（标记为已解决）
  - `notifyObservers(alert)`: 通知观察者（使用观察者模式）
- **依赖**: `AlertRepository`, `AlertSubject`, `List<AlertStrategy>`
- **设计模式**:
  - **Strategy Pattern（策略模式）**: 使用不同的策略判断不同类型的告警
  - **Observer Pattern（观察者模式）**: 通过AlertSubject通知所有观察者

**6. StatisticsService（统计服务）**
- **职责**: 处理数据统计相关的业务逻辑
- **主要方法**:
  - `getBuildingStats()`: 获取建筑能耗统计
  - `getDeviceStats()`: 获取设备能耗统计
  - `getEnergyStats(startTime, endTime)`: 获取指定时间范围内的能耗统计
- **依赖**: `EnergyDataRepository`, `DeviceRepository`, `BuildingRepository`

**7. EnergySimulatorService（数据模拟器服务）**
- **职责**: 定时生成模拟能耗数据
- **主要方法**:
  - `generateEnergyData()`: 生成能耗数据（使用工厂模式）
  - `startSimulation()`: 启动模拟任务
  - `stopSimulation()`: 停止模拟任务
- **依赖**: `EnergyDataRepository`, `DeviceRepository`, `EnergyDataFactory`
- **设计模式**:
  - **Factory Pattern（工厂模式）**: 根据配置选择NormalEnergyDataFactory或AbnormalEnergyDataFactory创建数据

### 2.3.4 控制器层类设计

#### 控制器层类图

```
┌─────────────────────┐
│  AuthController     │
│─────────────────────│
│ +login()            │
│ +register()         │
│ +getCurrentUser()   │
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│    AuthService      │
└─────────────────────┘

┌─────────────────────┐
│ BuildingController  │
│─────────────────────│
│ +getAllBuildings()  │
│ +getBuildingById()  │
│ +createBuilding()   │
│ +updateBuilding()   │
│ +deleteBuilding()   │
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│  BuildingService    │
└─────────────────────┘

┌─────────────────────┐
│  DeviceController   │
│─────────────────────│
│ +getAllDevices()    │
│ +getDeviceById()    │
│ +getDevicesByBuilding()│
│ +createDevice()     │
│ +updateDevice()     │
│ +deleteDevice()     │
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│   DeviceService     │
└─────────────────────┘

┌─────────────────────┐
│EnergyDataController │
│─────────────────────│
│ +getEnergyData()    │
│ +getLatestEnergyData()│
│ +getEnergyDataByTimeRange()│
│ +getTodayEnergyData()│
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│ EnergyDataService   │
└─────────────────────┘

┌─────────────────────┐
│  AlertController    │
│─────────────────────│
│ +getAllAlerts()     │
│ +getAlertsByDevice()│
│ +resolveAlert()     │
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│   AlertService      │
└─────────────────────┘

┌─────────────────────┐
│StatisticsController │
│─────────────────────│
│ +getBuildingStats() │
│ +getDeviceStats()   │
│ +getEnergyStats()   │
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│ StatisticsService   │
└─────────────────────┘

┌─────────────────────┐
│SimulatorController  │
│─────────────────────│
│ +startSimulation()  │
│ +stopSimulation()   │
│ +getSimulatorStatus()│
└──────────┬──────────┘
           │ uses
           ▼
┌─────────────────────┐
│EnergySimulatorService│
└─────────────────────┘
```

#### 控制器层类详细说明

所有Controller类遵循RESTful API设计规范，使用`@RestController`注解，统一返回`Result<T>`格式的响应。

**1. AuthController（认证控制器）**
- **职责**: 处理用户认证相关的HTTP请求
- **主要端点**:
  - `POST /api/auth/login`: 用户登录
  - `POST /api/auth/register`: 用户注册
  - `GET /api/auth/me`: 获取当前用户信息
- **安全**: 登录和注册端点公开访问，其他端点需要JWT认证

**2. BuildingController（建筑控制器）**
- **职责**: 处理建筑管理相关的HTTP请求
- **主要端点**:
  - `GET /api/buildings`: 获取所有建筑列表
  - `GET /api/buildings/{id}`: 获取建筑详情
  - `POST /api/buildings`: 创建新建筑（需要ADMIN权限）
  - `PUT /api/buildings/{id}`: 更新建筑信息（需要ADMIN权限）
  - `DELETE /api/buildings/{id}`: 删除建筑（需要ADMIN权限）

**3. DeviceController（设备控制器）**
- **职责**: 处理设备管理相关的HTTP请求
- **主要端点**:
  - `GET /api/devices`: 获取所有设备列表
  - `GET /api/devices/{id}`: 获取设备详情
  - `GET /api/devices/building/{buildingId}`: 获取指定建筑下的设备
  - `POST /api/devices`: 创建新设备（需要ADMIN权限）
  - `PUT /api/devices/{id}`: 更新设备信息（需要ADMIN权限）
  - `DELETE /api/devices/{id}`: 删除设备（需要ADMIN权限）

**4. EnergyDataController（能耗数据控制器）**
- **职责**: 处理能耗数据查询相关的HTTP请求
- **主要端点**:
  - `GET /api/energy-data/device/{deviceId}`: 分页获取设备能耗数据
  - `GET /api/energy-data/device/{deviceId}/latest`: 获取设备最新能耗数据
  - `GET /api/energy-data/device/{deviceId}/range`: 获取时间范围内的能耗数据
  - `GET /api/energy-data/device/{deviceId}/today`: 获取今日能耗数据
  - `GET /api/energy-data/latest`: 获取所有设备的最新能耗数据

**5. AlertController（告警控制器）**
- **职责**: 处理告警相关的HTTP请求
- **主要端点**:
  - `GET /api/alerts`: 分页获取所有告警记录
  - `GET /api/alerts/device/{deviceId}`: 获取指定设备的告警记录
  - `PUT /api/alerts/{id}/resolve`: 处理告警（标记为已解决）

**6. StatisticsController（统计控制器）**
- **职责**: 处理数据统计相关的HTTP请求
- **主要端点**:
  - `GET /api/statistics/buildings`: 获取建筑能耗统计
  - `GET /api/statistics/devices`: 获取设备能耗统计
  - `GET /api/statistics/energy`: 获取能耗统计（支持时间范围）

**7. SimulatorController（模拟器控制器）**
- **职责**: 控制数据模拟器的启动和停止
- **主要端点**:
  - `POST /api/simulator/start`: 启动数据模拟器（需要ADMIN权限）
  - `POST /api/simulator/stop`: 停止数据模拟器（需要ADMIN权限）
  - `GET /api/simulator/status`: 获取模拟器运行状态

### 2.3.5 设计模式类设计

#### 工厂模式（Factory Pattern）类图

```
┌─────────────────────┐
│ EnergyDataFactory   │◄─── Interface
│─────────────────────│
│ +createEnergyData() │
│ +getFactoryName()   │
└──────────┬──────────┘
           │
           │ implements
    ┌──────┴──────┐
    ▼             ▼
┌─────────────────────┐  ┌─────────────────────┐
│NormalEnergyDataFactory│ │AbnormalEnergyDataFactory│
│─────────────────────│  │─────────────────────│
│ +createEnergyData() │  │ +createEnergyData() │
│ +getFactoryName()   │  │ +getFactoryName()   │
└─────────────────────┘  └─────────────────────┘
```

**工厂模式说明**:
- **接口**: `EnergyDataFactory` - 定义创建能耗数据的统一接口
- **具体工厂**: 
  - `NormalEnergyDataFactory` - 创建正常能耗数据（电压210V-235V，功率在正常范围内）
  - `AbnormalEnergyDataFactory` - 创建异常能耗数据（电压过高/过低，功率过载）
- **使用场景**: `EnergySimulatorService`根据配置选择不同的工厂创建数据
- **优势**: 解耦对象创建和使用，符合开闭原则

#### 观察者模式（Observer Pattern）类图

```
┌─────────────────────┐
│   AlertSubject       │◄─── Subject（被观察者）
│─────────────────────│
│ -observers: List    │
│ +registerObserver() │
│ +removeObserver()   │
│ +notifyObservers()  │
└──────────┬──────────┘
           │
           │ 1
           │
           │ *
┌──────────▼──────────┐
│   AlertObserver     │◄─── Interface
│─────────────────────│
│ +onAlert(Alert)     │
└──────────┬──────────┘
           │
           │ implements
    ┌──────┴──────┐
    ▼             ▼
┌─────────────────────┐  ┌─────────────────────┐
│DatabaseAlertObserver│  │  LogAlertObserver   │
│─────────────────────│  │─────────────────────│
│ +onAlert(Alert)     │  │ +onAlert(Alert)     │
│ -saveToDatabase()   │  │ -logToFile()        │
└─────────────────────┘  └─────────────────────┘
```

**观察者模式说明**:
- **主题类**: `AlertSubject` - 管理观察者列表，当告警触发时通知所有观察者
- **观察者接口**: `AlertObserver` - 定义观察者的统一接口
- **具体观察者**:
  - `DatabaseAlertObserver` - 将告警保存到数据库
  - `LogAlertObserver` - 将告警记录到日志文件
- **使用场景**: `AlertService`检测到告警后，通过`AlertSubject`通知所有观察者
- **优势**: 解耦告警产生和告警处理，支持动态添加新的观察者

#### 策略模式（Strategy Pattern）类图

```
┌─────────────────────┐
│   AlertStrategy     │◄─── Interface
│─────────────────────│
│ +checkAlert()       │
│ +getStrategyName() │
└──────────┬──────────┘
           │
           │ implements
    ┌──────┴──────┐
    ▼             ▼
┌─────────────────────┐  ┌─────────────────────┐
│PowerOverloadAlertStrategy│ │VoltageAbnormalAlertStrategy│
│─────────────────────│  │─────────────────────│
│ +checkAlert()       │  │ +checkAlert()       │
│ -checkPowerOverload()│  │ -checkVoltageHigh()│
│ +getStrategyName()  │  │ -checkVoltageLow() │
└─────────────────────┘  │ +getStrategyName()  │
                         └─────────────────────┘
```

**策略模式说明**:
- **策略接口**: `AlertStrategy` - 定义告警检查的统一接口
- **具体策略**:
  - `PowerOverloadAlertStrategy` - 检查功率是否超过额定功率的120%
  - `VoltageAbnormalAlertStrategy` - 检查电压是否在正常范围内（198V-242V）
- **上下文**: `AlertService` - 持有所有策略实现的列表，遍历执行所有策略
- **使用场景**: 不同类型的告警有不同的判断逻辑，需要独立封装
- **优势**: 算法可以自由切换，避免多重条件判断，符合开闭原则

### 2.3.6 数据访问层类设计

#### Repository层类图

```
┌─────────────────────┐
│  JpaRepository<T,ID>│◄─── Spring Data JPA
└──────────┬──────────┘
           │
           │ extends
    ┌──────┴──────┐
    ▼             ▼
┌─────────────────────┐  ┌─────────────────────┐
│  UserRepository     │  │ BuildingRepository  │
│─────────────────────│  │─────────────────────│
│ +findByUsername()   │  │ +findByName()       │
└─────────────────────┘  └─────────────────────┘

┌─────────────────────┐
│  DeviceRepository   │
│─────────────────────│
│ +findBySerialNumber()│
│ +findByBuildingId() │
│ +findByStatus()     │
└─────────────────────┘

┌─────────────────────┐
│EnergyDataRepository │
│─────────────────────│
│ +findByDeviceId()    │
│ +findByTimeRange()  │
│ +findLatestEnergyData()│
│ +calculateEnergyConsumption()│
└─────────────────────┘

┌─────────────────────┐
│  AlertRepository     │
│─────────────────────│
│ +findByDeviceId()    │
│ +findByAlertType()   │
│ +findByIsResolved()  │
│ +findByTriggerTimeBetween()│
└─────────────────────┘
```

**Repository层说明**:
- 所有Repository接口继承自`JpaRepository<T, ID>`，提供基础的CRUD操作
- 使用Spring Data JPA的方法命名约定自动生成查询方法
- 复杂查询使用`@Query`注解自定义SQL或JPQL

### 2.3.7 安全认证类设计

#### 安全认证类图

```
┌─────────────────────┐
│JwtTokenProvider     │
│─────────────────────│
│ +generateToken()    │
│ +validateToken()    │
│ +getUsernameFromToken()│
│ +getExpirationDate()│
└──────────┬──────────┘
           │
           │ uses
           ▼
┌─────────────────────┐
│JwtAuthenticationFilter│
│─────────────────────│
│ +doFilterInternal() │
│ -extractToken()     │
└──────────┬──────────┘
           │
           │ uses
           ▼
┌─────────────────────┐
│CustomUserDetailsService│
│─────────────────────│
│ +loadUserByUsername()│
└──────────┬──────────┘
           │
           │ uses
           ▼
┌─────────────────────┐
│  UserRepository     │
└─────────────────────┘
```

**安全认证说明**:
- `JwtTokenProvider`: 负责JWT令牌的生成和验证
- `JwtAuthenticationFilter`: 拦截HTTP请求，提取并验证JWT令牌
- `CustomUserDetailsService`: 实现Spring Security的UserDetailsService接口，从数据库加载用户信息
- `SecurityConfig`: Spring Security配置类，配置认证和授权规则

### 2.3.8 完整系统类图（简化版）

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller Layer                        │
├─────────────────────────────────────────────────────────────┤
│ AuthController │ BuildingController │ DeviceController      │
│ EnergyDataController │ AlertController │ StatisticsController│
└───────────────────────────┬─────────────────────────────────┘
                            │ uses
┌───────────────────────────▼─────────────────────────────────┐
│                      Service Layer                           │
├─────────────────────────────────────────────────────────────┤
│ AuthService │ BuildingService │ DeviceService                │
│ EnergyDataService │ AlertService │ StatisticsService          │
│ EnergySimulatorService                                        │
└───────────────────────────┬─────────────────────────────────┘
                            │ uses
        ┌───────────────────┴───────────────────┐
        │                                       │
┌───────▼────────┐              ┌──────────────▼──────────────┐
│ Repository Layer│              │      Pattern Layer          │
├─────────────────┤              ├──────────────────────────────┤
│ UserRepository  │              │ EnergyDataFactory           │
│ BuildingRepository│            │ AlertSubject                │
│ DeviceRepository│              │ AlertStrategy               │
│ EnergyDataRepository│          │ AlertObserver               │
│ AlertRepository │              └──────────────────────────────┘
└────────┬────────┘
         │ uses
┌────────▼────────┐
│  Entity Layer   │
├─────────────────┤
│ User            │
│ Building        │
│ Device          │
│ EnergyData      │
│ Alert           │
└─────────────────┘
```

### 2.3.9 类设计总结

#### 设计原则应用

1. **单一职责原则（SRP）**
   - 每个类都有明确的单一职责
   - Controller负责接收请求，Service负责业务逻辑，Repository负责数据访问

2. **开闭原则（OCP）**
   - 通过策略模式和工厂模式实现扩展开放、修改关闭
   - 新增告警类型只需添加新的策略实现，无需修改现有代码

3. **依赖倒置原则（DIP）**
   - 高层模块（Service）依赖抽象（Repository接口），不依赖具体实现
   - 通过Spring的依赖注入实现控制反转

4. **接口隔离原则（ISP）**
   - 每个接口职责单一，如`AlertStrategy`只定义告警检查方法
   - 避免接口臃肿，提高灵活性

5. **里氏替换原则（LSP）**
   - 所有具体工厂类可以替换`EnergyDataFactory`接口
   - 所有具体策略类可以替换`AlertStrategy`接口

#### 设计模式应用总结

1. **工厂模式**: 解耦能耗数据的创建过程，支持正常和异常数据的动态创建
2. **观察者模式**: 实现告警的松耦合通知机制，支持动态添加新的告警处理方式
3. **策略模式**: 封装不同的告警判断算法，支持运行时动态选择策略
4. **建造者模式**: 通过Lombok的`@Builder`注解实现，简化复杂对象的构建
5. **外观模式**: Service层为Controller层提供简化的接口，隐藏子系统复杂性
6. **适配器模式**: DTO对象适配Entity对象，实现数据传输和业务模型的分离

#### 架构优势

1. **分层清晰**: Controller-Service-Repository三层架构，职责明确
2. **易于扩展**: 通过设计模式支持功能扩展，符合开闭原则
3. **易于测试**: 依赖注入使得单元测试更容易编写
4. **易于维护**: 代码结构清晰，符合SOLID原则
5. **性能优化**: 使用JPA的懒加载机制，避免N+1查询问题

---

## API文档

启动后端后，访问以下地址查看API文档：

- **Knife4j**: http://localhost:8080/api/doc.html
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html

---

## 配置说明

### 数据模拟器配置

```yaml
simulator:
  enabled: true              # 是否启用模拟器
  interval: 5000            # 数据生成间隔（毫秒）
  anomaly-frequency: 30    # 异常数据频率（每N条正常数据生成1条异常）
```

### 告警阈值配置

```yaml
alert:
  voltage:
    min: 198    # 电压下限（220V的90%）
    max: 242    # 电压上限（220V的110%）
  power:
    overload-ratio: 1.2    # 功率过载比例（120%）
```

---

## 常见问题

### 1. 数据库连接失败

- 检查MySQL服务是否启动
- 确认数据库用户名和密码正确
- 确认数据库端口号（默认3306，项目配置为3309）

### 2. 前端无法访问后端

- 检查后端是否正常启动
- 确认CORS配置正确
- 检查API地址配置

### 3. 定时任务不执行

- 确认主类添加了 `@EnableScheduling` 注解
- 检查 `simulator.enabled` 配置是否为 `true`
- 查看日志确认是否有错误


