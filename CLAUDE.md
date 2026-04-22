# CLAUDE.md — LiteMes 项目开发指南

## 项目概述

LiteMes 是面向 PCB 制造场景的轻量级 MES 系统，单体服务四层架构，基于 **Quarkus 3.21 (Java 17) + Vue 3 + Element Plus** 构建。支持 JVM 模式与 GraalVM Native Image 双模式部署。

**当前进度：** 基础数据模块（5 大子模块、24 个 Feature）已完成。下一步将进入**生产执行模块**（工单、批次、过站记录）开发。

## 技术栈

| 层 | 技术 |
|----|------|
| 后端框架 | Quarkus 3.21.4, Java 17, Maven |
| REST API | JAX-RS |
| ORM | MyBatis-Plus 2.4.2 (Quarkiverse) |
| 认证 | SmallRye JWT |
| API 文档 | SmallRye OpenAPI (Swagger UI) |
| 参数校验 | Hibernate Validator |
| 缓存/锁 | Quarkus Redis (Lettuce) |
| 实时通信 | Quarkus WebSocket |
| 硬件通信 | Vert.x MQTT Client |
| 领域事件 | CDI Events |
| 定时任务 | Quarkus Scheduler |
| 日志 | JBoss Logging + SLF4J |
| 测试 | JUnit 5 + REST Assured (QuarkusTest) |
| Native | GraalVM Native Image |
| 前端框架 | Vue 3.5.32 + Pinia 3.0.4 + Vite 8.0.9 |
| UI 组件库 | Element Plus 2.13.7 |
| 前端语言 | TypeScript |
| HTTP 客户端 | Axios |

## 数据库

**环境：** MySQL 8 运行在 Docker 容器 `ruoyi-mysql`

**连接信息：** `localhost:3306/litemes`，用户 `root`，密码 `password`

**Schema 文件：** `src/main/resources/db/schema.sql`（31 张表，含外键约束）

### 字符集（utf8mb4）

MySQL 的 `utf8` 是 3 字节子集，中文会乱码。全链路必须 `utf8mb4`：

- **建库**：`CREATE DATABASE litemes CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
- **JDBC URL**：`characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci`（已配在 application.yml）
- **建表**：`DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci`（schema.sql 已统一）

### 数据库初始化

从零初始化数据库（所有表 + 默认管理员）：

```bash
# 1. 将 schema.sql 复制到容器
docker cp src/main/resources/db/schema.sql ruoyi-mysql:/tmp/schema.sql

# 2. 重建数据库 + 建表
docker exec ruoyi-mysql mysql -u root -ppassword --default-character-set=utf8mb4 \
  -e "DROP DATABASE IF EXISTS litemes;
      CREATE DATABASE litemes CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

docker exec ruoyi-mysql mysql -u root -ppassword --default-character-set=utf8mb4 litemes \
  -e "SOURCE /tmp/schema.sql;"

# 3. 创建默认管理员
docker exec ruoyi-mysql mysql -u root -ppassword --default-character-set=utf8mb4 litemes \
  -e "INSERT INTO user (username, password, realName, status, createdAt)
      VALUES ('admin', 'admin123', '系统管理员', 1, NOW());"
```

**默认管理员：** `admin / admin123`

## 构建与运行

```bash
# 后端开发模式
./mvnw quarkus:dev

# 运行测试
./mvnw test

# Native Image 编译
./mvnw package -Pnative
./target/litemes-1.0.0-SNAPSHOT-runner

# 前端开发
cd frontend && npm install && npm run dev

# E2E 测试（Playwright）
cd frontend && npm run test:e2e          # 运行测试
cd frontend && npm run test:report       # 查看 HTML 报告（含截图）
```

**E2E 测试说明：**
- 测试目录：`frontend/e2e/`
- 报告输出：`frontend/evidence/playwright-report/`（HTML）、`frontend/evidence/test-results.json`（JSON）
- 截图策略：每次测试都截图（`screenshot: 'on'`），Trace 完整记录（`trace: 'on'`）
- 测试前需确保后端（`./mvnw quarkus:dev`）和前端（`npm run dev`）已启动

## 四层架构

严格分层，不可跨层调用：

```
web/ (REST API 层 — JAX-RS Resource)
  ↓ 调用
application/ (应用服务层 — 业务编排、事务、DTO 转换)
  ↓ 调用
domain/ (领域层 — 实体、仓储接口、领域服务、领域事件)
  ↑ 实现
infrastructure/ (基础设施层 — MyBatis-Plus 仓储、MQTT、Redis、配置)
```

- `web` → 依赖 `application`
- `application` → 依赖 `domain`
- `infrastructure` → 依赖 `domain`（实现仓储接口）
- `web` 可直接依赖 `infrastructure`（CDI 注入）

## 已实现模块

### 基础数据模块（Master Data）— 全部完成

| 子模块 | 实体数 | REST API | 前端页面 |
|--------|--------|----------|---------|
| 企业架构（公司/工厂/部门/班制/工作中心/工序） | 8 | 8 Resource | 已完成 |
| 物料管理（计量单位/物料分类/物料/免检清单） | 5 | 5 Resource | 已完成 |
| 设备管理（设备类型/型号/台账） | 3 | 3 Resource | 已完成 |
| 供应链（客户/供应商） | 4 | 2 Resource | 已完成 |
| 数据权限（权限组/用户权限） | 5 | 2 Resource | 已完成 |

### 基础设施 — 已完成

- 项目骨架（四层架构 + 统一响应体 `R<T>` + 全局异常处理）
- MyBatis-Plus 配置（分页 + 数据权限拦截器 + 审计字段自动填充）
- JWT 认证后端 + 前端（登录页 + 路由守卫 + Token 管理）
- 前端布局（导航 + Tab 管理 + 主题切换 + Element Plus 集成）
- 通用组件（级联选择器、审计日志弹窗、表格设置面板）

## 编码规范

### 命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 实体类 | PascalCase | `MaterialMaster`, `EquipmentLedger` |
| REST Resource | `{Entity}Resource` | `MaterialResource` |
| 应用服务 | `{Entity}Service` | `MaterialService` |
| 仓储接口 | `{Entity}Repository` | `MaterialRepository` |
| DTO | `{Entity}Dto` / `Create/Update` | `MaterialCreateDto` |
| MyBatis Mapper | `{Entity}Mapper` | `MaterialMapper` |
| 数据库表名 | snake_case | `material_master` |

### 关键规则

- **四层分离**：Resource 仅做参数校验和调用 Service；Service 负责编排；Domain 封装规则；Infrastructure 实现仓储
- **关键编码不可变**：物料编码、工厂编码等业务主键创建后不可编辑
- **引用数据不可删除**：被引用的基础数据采用软删除（`@TableLogic`）
- **数据权限全局过滤**：通过 MyBatis-Plus 拦截器实现，禁止在业务代码中手动拼接权限条件
- **审计日志强制记录**：所有增删改必须通过 BaseEntity 基类记录操作人和时间
- **禁止跳过 DTO**：API 层必须通过 DTO 与前端交互
- **领域事件解耦硬件通信**：MQTT 数据接收后通过 CDI Events 发布，业务逻辑在 Observer 中处理

## Feature Workflow

项目使用 Feature Workflow 管理开发流程：
- 配置：`feature-workflow/config.yaml`
- 队列：`feature-workflow/queue.yaml`
- Feature 文档：`features/pending-{id}/` 或 `features/active-{id}/`
- 归档：`features/archive/`

常用命令：
- `/list-features` — 查看所有 Feature 状态
- `/new-feature` — 创建新 Feature
- `/start-feature <id>` — 开始开发
- `/complete-feature <id>` — 完成并归档

## 相关文件

- `project-context.md` — 项目上下文知识库（技术栈、编码模式、领域模型）
- `README.md` — 项目概述与架构说明
