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


