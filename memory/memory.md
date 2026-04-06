# SpringBoot_Vue 项目概览

## 项目基本信息
- **技术栈**: Spring Boot 3.4.1 + Vue 3 + MyBatis-Plus + PageHelper
- **Java 版本**: 21/22 (启用了 preview 特性)
- **数据库**: SQL Server (主) + PostgreSQL (从) - 动态多数据源
- **构建工具**: Maven

## 核心依赖
- MyBatis-Plus 3.5.9 (持久层框架)
- Dynamic-Datasource 4.3.1 (动态多数据源)
- PageHelper 2.1.0 (分页)
- EasyExcel 3.3.2 + Apache POI 5.2.3 (Excel 处理)
- SpringDoc OpenAPI 2.2.0 (Swagger UI)
- Druid 1.2.23 (数据库连接池)

## 项目结构

### 后端模块 (Spring Boot)
```
src/main/java/com/example/demo/
├── controller/          # 控制器层
│   ├── UserController              # 用户登录/注册
│   ├── ProjectsController          # 项目管理
│   ├── HaoSenMainDataQueryController  # 好森主数据查询 (医院/药店/公司)
│   ├── HaoSenHomeDataController       # 好森首页数据
│   ├── HaoSenAppealDataController     # 好森申诉数据
│   ├── HaoSenInputCleanDataController # 好森清洗数据导入
│   ├── HaoSenInputUpdateDataController# 好森更新数据导入
│   ├── HaoSenDuplicateDataController  # 好森重复数据
│   └── HengRuiController             # 恒瑞项目模块
├── service/             # 服务层接口
├── service/impl/        # 服务层实现
├── mapper/              # MyBatis Mapper 接口
├── entity/              # 实体类
│   ├── Users            # 用户
│   ├── Projects         # 项目
│   ├── HaoSenHospital   # 好森医院
│   ├── HaoSenDrugStore  # 好森药店
│   ├── HaoSenCompany    # 好森公司
│   ├── HrBatch          # 恒瑞批次
│   ├── HrMonitoringData # 恒瑞监测数据
│   └── HrOrgRelation    # 恒瑞组织关系
├── dto/                 # 数据传输对象
├── vo/                  # 视图对象
├── config/              # 配置类
├── utils/               # 工具类
│   ├── MyBatisUtils     # MyBatis 工具
│   ├── HaoSenToExcel    # 好森数据导出 Excel
│   ├── HaoSenReaderExcel# 好森 Excel 读取
│   └── ReaderExcel      # 通用 Excel 读取
└── annotation/          # 自定义注解
└── interceptor/         # 拦截器
└── apidata/             # API 数据结构
```

### 前端模块 (Vue 3)
```
vue_demo/src/
├── components/
│   ├── Login.vue              # 登录页
│   ├── Register.vue           # 注册页
│   ├── Home.vue               # 项目首页 (项目管理仪表盘)
│   ├── haosenproject/         # 好森项目模块
│   │   ├── HaoSenHome.vue           # 好森项目首页
│   │   ├── homechartview/           # 图表组件
│   │   │   ├── HomeDashboardView.vue  # 仪表盘
│   │   │   ├── CleanBarChart.vue      # 清洗数据柱状图
│   │   │   ├── MainDataPieChart.vue   # 主数据饼图
│   │   │   ├── BranchBarChart.vue     # 分支柱状图
│   │   │   └── AppealUpdateChart.vue  # 申诉更新图表
│   │   ├── maindataview/            # 主数据视图
│   │   │   ├── HospitalDataView.vue   # 医院数据
│   │   │   ├── DrugStoreDataView.vue  # 药店数据
│   │   │   └── CompanyDataView.vue    # 公司数据
│   │   ├── appealdataview/          # 申诉数据
│   │   ├── cleandataview/           # 清洗数据
│   │   ├── updatedataView/          # 更新数据
│   │   └── repeatdataview/          # 重复数据
│   └── hengruiproject/        # 恒瑞项目模块
│       ├── HengRuiHome.vue          # 恒瑞项目首页
│       ├── batchdataview/           # 批次数据
│       ├── monitoringdataview/      # 监测数据
│       └── orgrelationview/         # 组织关系
├── router/
│   └── index.js             # 路由配置 (含权限守卫)
└── assets/
    ├── css/dark-mode.css    # 暗黑模式样式
    └── images/              # 图片资源
```

## 核心业务模块

### 1. 用户认证模块
- 登录/注册/登出
- JWT Token 认证
- 路由守卫 (requiresAuth, requiresGuest)

### 2. 项目管理系统
- 项目卡片展示 (支持搜索/筛选)
- 动态创建项目 (自定义图标/颜色)
- 项目路由跳转

### 3. 好森项目 (HaoSen)
- **主数据管理**: 医院/药店/商业公司数据的查询、分页、导出 Excel
- **申诉数据**: 数据申诉导入、查看
- **清洗数据**: 数据清洗导入、查询
- **更新数据**: 数据更新导入
- **重复数据**: 重复数据检测、导出
- **首页仪表盘**: 多维度数据图表展示

### 4. 恒瑞项目 (HengRui)
- **批次管理**: 批次数据导入、查看
- **监测数据**: 监测数据查询
- **组织关系**: 组织架构关系管理

## 技术特点

### 后端特性
- **多数据源动态切换**: 使用 `@DS` 注解或配置文件切换 SQL Server/PostgreSQL
- **分页处理**: PageHelper 自动分页，支持动态数据库方言识别
- **Excel 导入导出**: EasyExcel + Apache POI，支持大数据量导出
- **异步处理**: `@EnableAsync` 启用异步任务
- **事务管理**: `@EnableTransactionManagement`

### 前端特性
- **暗黑模式支持**: 完整的主题切换
- **响应式设计**: 适配移动端
- **路由权限控制**: 基于 Token 的认证守卫
- **Toast 消息提示**: 统一的用户反馈机制
- **动态对话框**: 项目创建等交互

## 数据库配置
```properties
# SQL Server (主库)
spring.datasource.dynamic.datasource.master_sqlserver.url=jdbc:sqlserver://localhost:1433;databaseName=test

# PostgreSQL (从库)
spring.datasource.dynamic.datasource.slave_pg.url=jdbc:postgresql://localhost:5432/hs_server
```

## API 设计模式
- 统一返回格式：`ApiResponseDTO<T>`
- 分页返回：`PageInfo<T>` (包含 pageNumber, pageSize, totalPages, totalElements 等)
- 条件查询：使用 `@ModelAttribute` 接收查询条件 DTO
- 文件下载：返回 `ResponseEntity<byte[]>` + 文件类型头

## 代码规范 (来自 demo.md)
- DTO: 主要用于数据传输
- VO: 主要用于视图展示/封装业务逻辑
