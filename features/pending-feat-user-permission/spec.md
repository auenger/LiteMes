# Feature: feat-user-permission 用户数据权限

## 基本信息
- **ID**: feat-user-permission
- **名称**: 用户数据权限
- **优先级**: 51
- **规模**: M
- **依赖**: feat-permission-group
- **父模块**: feat-data-permission
- **创建时间**: 2026-04-21

## 需求来源
- 数据权限_功能设计_V1.0.docx 第4章「数据权限」
- 父 feature: feat-data-permission

## 用户价值点
1. **批量授权提效**：选择多个用户 + 权限组一键赋权，免去逐用户逐维度配置，大幅减少管理开销
2. **数据安全隔离**：通过 MyBatis-Plus 全局拦截器在仓储层自动过滤数据，业务代码零侵入，确保用户只能看到授权范围内的数据
3. **灵活权限叠加**：用户权限 = 权限组继承 + 直接授权，支持按需为用户额外开放特定工厂/工作中心/工序

## 需求描述
为用户分配数据权限，支持按权限组批量授权，也支持直接为用户指定工厂/工作中心/工序级别的权限。通过 MyBatis-Plus 全局拦截器，在仓储层自动过滤数据，业务代码无需手动处理权限。

### 核心实体
| 实体 | 说明 |
|------|------|
| `UserDataPermission` | 用户数据权限主表（用户维度，一条记录代表一个用户的权限配置） |
| `UserDataPermissionFactory` | 用户权限-工厂（直接授权或从权限组继承） |
| `UserDataPermissionWorkCenter` | 用户权限-工作中心 |
| `UserDataPermissionProcess` | 用户权限-工序 |

### 功能清单
1. **用户权限列表** — 按用户名/姓名查询，展示用户及其权限概览
2. **批量添加权限** — 选择多个用户，选择权限组，批量赋值该权限组已有的数据权限
3. **用户级工厂/工作中心/工序管理** — 为单个用户直接选择工厂、工作中心、工序
4. **导入/导出** — Excel 导入导出权限数据
5. **全局查询过滤器** — MyBatis-Plus 全局拦截器，根据当前用户权限自动过滤查询结果

### 页面设计
- 主页面：用户数据权限列表（查询条件：用户名/姓名-模糊查询）
- 批量操作：勾选多个用户 → 选择权限组 → 确认批量授权
- Tab 页：工厂 / 工作中心 / 工序（为单个用户直接配置）
- 支持 Excel 导入/导出

### 业务规则
- 批量添加权限时，选择权限组后为所有勾选用户赋值该权限组的数据权限
- 用户权限 = 权限组继承的权限 + 直接授权的权限
- 如果用户未分配任何权限组，仍可通过直接授权添加工厂/工作中心/工序权限
- 全局拦截器在仓储层自动生效，拦截所有涉及数据权限实体的查询
- 拦截器根据当前登录用户的权限上下文自动注入过滤条件
- 权限缓存：用户登录时加载权限到 Redis，避免每次查询都读库

### 技术实现要点
- **全局拦截器**：通过 MyBatis-Plus `Interceptor` 实现，在 SQL 执行前自动追加权限过滤条件
- **权限上下文**：通过 JWT Token 解析用户 ID，从 Redis 缓存中获取该用户的权限范围
- **缓存策略**：用户登录时计算权限并写入 Redis（TTL 与 JWT 一致），权限变更时主动刷新缓存
- **批量授权**：事务内完成，先创建 UserDataPermission 主记录，再批量插入关联记录

## 上下文分析

### 参考代码（关联模块实现后）
- `domain/entity/Factory.java` — 软删除实体模式参考
- `infrastructure/persistence/mapper/CompanyMapper.java` — MyBatis-Plus Mapper 接口
- `application/service/CompanyService.java` — CRUD + 事务管理参考
- `feat-permission-group` 的 DataPermissionGroupService — 关联保存全量替换策略
- Quarkus Redis Client — 权限缓存实现参考

### 关联文档
- 数据权限_功能设计_V1.0.docx — 原始需求文档
- CLAUDE.md — 四层架构、MyBatis-Plus 拦截器规范

### 关联 Feature
- **feat-permission-group** — 本 feature 的直接上游，提供权限组数据和删除保护依赖
- **feat-department-user** — 提供用户列表数据（用户名、姓名）供权限列表查询
- **feat-enterprise-common** — 通用下拉接口、级联选择器
- **feat-factory** — 工厂数据来源

## 数据模型

### UserDataPermission（用户数据权限，继承 BaseEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| user_id | BIGINT | UNIQUE, NOT NULL | 关联用户（一个用户一条记录） |
| group_id | BIGINT | FK, NULL | 关联权限组（可为空，表示无权限组，仅直接授权） |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

> **设计说明**：`group_id` 可为空。当用户通过直接授权获得权限时，不一定要绑定权限组。批量添加权限时会同时设置 `group_id` 和关联记录。

### UserDataPermissionFactory（用户权限-工厂，继承 BaseEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| user_permission_id | BIGINT | FK, NOT NULL | 关联用户权限主表 |
| factory_id | BIGINT | FK, NOT NULL | 关联工厂 |
| source | VARCHAR(10) | NOT NULL | 来源：`GROUP`(权限组继承) / `DIRECT`(直接授权) |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### UserDataPermissionWorkCenter（用户权限-工作中心，继承 BaseEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| user_permission_id | BIGINT | FK, NOT NULL | 关联用户权限主表 |
| work_center_id | BIGINT | FK, NOT NULL | 关联工作中心 |
| source | VARCHAR(10) | NOT NULL | 来源：`GROUP` / `DIRECT` |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### UserDataPermissionProcess（用户权限-工序，继承 BaseEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| user_permission_id | BIGINT | FK, NOT NULL | 关联用户权限主表 |
| process_id | BIGINT | FK, NOT NULL | 关联工序 |
| source | VARCHAR(10) | NOT NULL | 来源：`GROUP` / `DIRECT` |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### 数据库索引
| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| uk_user_perm_user | UNIQUE | user_id | 每个用户一条权限主记录 |
| idx_user_perm_group | INDEX | group_id | 按权限组反查用户（删除保护） |
| uk_up_factory | UNIQUE | (user_permission_id, factory_id) | 防止重复关联 |
| uk_up_work_center | UNIQUE | (user_permission_id, work_center_id) | 防止重复关联 |
| uk_up_process | UNIQUE | (user_permission_id, process_id) | 防止重复关联 |
| idx_upf_factory | INDEX | factory_id | 全局拦截器按工厂过滤 |
| idx_upw_wc | INDEX | work_center_id | 全局拦截器按工作中心过滤 |
| idx_upp_process | INDEX | process_id | 全局拦截器按工序过滤 |
| idx_up_source | INDEX | (user_permission_id, source) | 按 source 类型查询 |

### 数据库表 DDL（参考）

```sql
CREATE TABLE user_data_permission (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL,
    group_id      BIGINT       NULL COMMENT '关联权限组，NULL表示仅直接授权',
    created_by    VARCHAR(64)  NULL,
    created_at    DATETIME     NULL,
    updated_by    VARCHAR(64)  NULL,
    updated_at    DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_perm_user (user_id),
    INDEX idx_user_perm_group (group_id),
    CONSTRAINT fk_udp_group FOREIGN KEY (group_id) REFERENCES data_permission_group(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户数据权限';

CREATE TABLE user_data_permission_factory (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    user_permission_id   BIGINT       NOT NULL,
    factory_id           BIGINT       NOT NULL,
    source               VARCHAR(10)  NOT NULL COMMENT 'GROUP=权限组继承, DIRECT=直接授权',
    created_by           VARCHAR(64)  NULL,
    created_at           DATETIME     NULL,
    updated_by           VARCHAR(64)  NULL,
    updated_at           DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_up_factory (user_permission_id, factory_id),
    INDEX idx_upf_factory (factory_id),
    INDEX idx_up_source (user_permission_id, source),
    CONSTRAINT fk_upf_perm FOREIGN KEY (user_permission_id) REFERENCES user_data_permission(id),
    CONSTRAINT fk_upf_factory FOREIGN KEY (factory_id) REFERENCES factory(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户数据权限-工厂';

CREATE TABLE user_data_permission_work_center (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    user_permission_id   BIGINT       NOT NULL,
    work_center_id       BIGINT       NOT NULL,
    source               VARCHAR(10)  NOT NULL COMMENT 'GROUP/DIRECT',
    created_by           VARCHAR(64)  NULL,
    created_at           DATETIME     NULL,
    updated_by           VARCHAR(64)  NULL,
    updated_at           DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_up_work_center (user_permission_id, work_center_id),
    INDEX idx_upw_wc (work_center_id),
    INDEX idx_upw_source (user_permission_id, source),
    CONSTRAINT fk_upw_perm FOREIGN KEY (user_permission_id) REFERENCES user_data_permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户数据权限-工作中心';

CREATE TABLE user_data_permission_process (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    user_permission_id   BIGINT       NOT NULL,
    process_id           BIGINT       NOT NULL,
    source               VARCHAR(10)  NOT NULL COMMENT 'GROUP/DIRECT',
    created_by           VARCHAR(64)  NULL,
    created_at           DATETIME     NULL,
    updated_by           VARCHAR(64)  NULL,
    updated_at           DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_up_process (user_permission_id, process_id),
    INDEX idx_upp_process (process_id),
    INDEX idx_upp_source (user_permission_id, source),
    CONSTRAINT fk_upp_perm FOREIGN KEY (user_permission_id) REFERENCES user_data_permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户数据权限-工序';
```

## 验收标准 (Gherkin)

### 场景 1: 批量添加权限（通过权限组）
```gherkin
Given 用户 U001、U002 未分配数据权限
And 权限组"冲压车间"包含工厂 F001 + 工序 PS001、PS002
When 勾选 U001、U002，选择权限组"冲压车间"，点击确认
Then U001 和 U002 各创建一条 UserDataPermission 记录（group_id 指向"冲压车间"）
And U001 和 U002 的工厂/工序关联记录的 source 均为 GROUP
And U001 和 U002 均获得工厂 F001 + 工序 PS001、PS002 的数据权限
```

### 场景 2: 直接为用户添加工厂权限
```gherkin
Given 用户 U001 已有权限组"冲压车间"的权限（含工厂 F001）
When 为 U001 直接添加工厂 F002 的权限
Then U001 的权限范围扩大为 F001 + F002
And F001 的 source = GROUP，F002 的 source = DIRECT
```

### 场景 3: 直接为用户添加工序权限
```gherkin
Given 用户 U001 已有权限组"冲压车间"的权限（含工序 PS001、PS002）
When 为 U001 直接添加工序 PS003 的权限
Then U001 的工序权限范围扩大为 PS001 + PS002 + PS003
And PS003 的 source = DIRECT
```

### 场景 4: 无权限组直接授权
```gherkin
Given 用户 U003 未分配任何权限组
When 为 U003 直接添加工厂 F001 的权限
Then UserDataPermission 记录的 group_id 为 NULL
And 工厂关联记录 source = DIRECT
And U003 获得工厂 F001 的数据权限
```

### 场景 5: 全局拦截器自动生效
```gherkin
Given 用户 U001 的数据权限仅含工序 PS001
When 用户 U001 查询工单列表
Then MyBatis-Plus 全局拦截器自动追加 WHERE process_id IN (PS001)
And 用户只能看到工序 PS001 的数据
```

### 场景 6: 权限缓存
```gherkin
Given 用户 U001 刚登录系统
When 系统加载 U001 的权限数据
Then 权限范围写入 Redis 缓存（key: user:permission:{userId}）
And 后续查询直接从缓存读取，无需查库
```

### 场景 7: 权限变更后缓存刷新
```gherkin
Given 用户 U001 的权限已缓存
When 管理员为 U001 新增工厂 F002 的权限
Then Redis 缓存立即刷新
And U001 的下一次查询自动使用新的权限范围
```

### 场景 8: 用户权限列表查询
```gherkin
Given 系统中存在多个用户的权限数据
When 按用户名"张"模糊查询
Then 返回用户名含"张"的权限记录列表
And 列表显示用户名、姓名、关联权限组、创建人、创建时间等
```

### 场景 9: Excel 导出
```gherkin
Given 用户数据权限页面有权限数据
When 点击导出 Excel
Then 系统下载包含权限数据的 Excel 文件
And Excel 包含用户名、姓名、权限组、关联的工厂/工作中心/工序信息
```

### 场景 10: Excel 导入
```gherkin
Given 准备好格式正确的权限 Excel 文件
When 上传该文件
Then 系统解析并导入权限数据
And 导入失败的行返回错误提示
```

### 场景 11: 批量添加时用户已有权限
```gherkin
Given 用户 U001 已有权限组"冲压车间"的权限
When 再次为 U001 批量添加权限组"组装车间"的权限
Then U001 的 UserDataPermission 的 group_id 更新为"组装车间"
And 关联记录根据新权限组全量替换（source = GROUP）
And 之前直接授权的记录（source = DIRECT）保留不变
```

### 场景 12: 删除用户权限
```gherkin
Given 用户 U001 有数据权限记录及关联数据
When 删除 U001 的数据权限
Then UserDataPermission 主记录被删除
And 所有关联记录（工厂/工作中心/工序）一并删除
And Redis 缓存刷新
```
