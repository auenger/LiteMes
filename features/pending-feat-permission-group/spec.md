# Feature: feat-permission-group 数据权限组管理

## 基本信息
- **ID**: feat-permission-group
- **名称**: 数据权限组管理
- **优先级**: 52
- **规模**: M
- **依赖**: feat-enterprise-org
- **父模块**: feat-data-permission
- **创建时间**: 2026-04-21

## 需求来源
- 数据权限_功能设计_V1.0.docx 第3章「数据权限组」
- 父 feature: feat-data-permission

## 用户价值点
1. **权限模板化**：将工厂、工作中心、工序打包为权限集合，避免逐用户重复配置，大幅提升权限管理效率
2. **标准化授权**：通过权限组统一数据访问范围，确保相同角色的用户获得一致的权限，降低配置遗漏风险
3. **灵活组合**：权限组支持独立关联工厂/工作中心/工序三个维度，满足不同粒度的权限控制需求

## 需求描述
维护数据权限组基础信息，将工厂、工作中心、工序打包为权限集合，作为用户数据权限分配的基础单元。

### 核心实体
| 实体 | 说明 |
|------|------|
| `DataPermissionGroup` | 数据权限组主表（名称、描述） |
| `DataPermissionGroupFactory` | 权限组-工厂关联 |
| `DataPermissionGroupWorkCenter` | 权限组-工作中心关联 |
| `DataPermissionGroupProcess` | 权限组-工序关联 |

### 功能清单
1. **权限组 CRUD** — 创建/编辑/删除数据权限组（名称唯一性校验）
2. **关联工厂** — 选择工厂（支持多选、分页多页勾选），关联到权限组
3. **关联工作中心** — 选择工作中心（支持多选、分页多页勾选），关联到权限组
4. **关联工序** — 选择工序（支持多选、分页多页勾选），关联到权限组
5. **删除保护** — 已被用户权限引用的权限组不可删除（按钮置灰）

### 页面设计
- 主页面：权限组列表（查询条件：权限组名称-模糊查询）
- Tab 页：工厂 / 工作中心 / 工序（每个 Tab 独立的选择关联管理）
- 创建/编辑弹窗：权限组名称

### 业务规则
- 权限组名称必填且唯一
- 工厂/工作中心/工序从企业管理模块已有数据中选择
- 关联操作允许多页勾选批量保存
- 已被用户权限引用的数据不可删除
- 保存关联时采用全量替换策略：提交时对比已有关联，删除取消勾选的、新增新勾选的

## 上下文分析

### 参考代码（关联模块实现后）
- `domain/entity/Company.java` — BaseEntity 继承模式，审计字段自动填充
- `domain/entity/Factory.java` — 软删除实体（SoftDeleteEntity）模式
- `infrastructure/persistence/mapper/CompanyMapper.java` — MyBatis-Plus Mapper 基础接口
- `application/service/CompanyService.java` — CRUD Service 编排模式
- `web/resource/CompanyResource.java` — JAX-RS Resource DTO 交互模式
- `domain/repository/CompanyRepository.java` — 仓储接口定义模式

### 关联文档
- 数据权限_功能设计_V1.0.docx — 原始需求文档
- 企业管理_功能设计_V1.0.docx — 工厂实体定义来源
- CLAUDE.md — 四层架构规范、编码规范

### 关联 Feature
- **feat-company** — 提供 Company 实体，本 feature 不直接依赖
- **feat-factory** — 提供 Factory 实体及下拉数据，权限组-工厂关联的核心数据来源
- **feat-enterprise-common** — 提供通用下拉接口，工厂/工作中心/工序选择弹窗的数据来源
- **feat-user-permission** — 本 feature 的下游消费者，权限组被用户权限引用，引用关系决定删除保护逻辑

## 数据模型

### DataPermissionGroup（数据权限组，继承 BaseEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| group_name | VARCHAR(50) | UNIQUE, NOT NULL | 权限组名称，唯一 |
| remark | VARCHAR(200) | NULL | 备注 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 软删除标记（@TableLogic） |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### DataPermissionGroupFactory（权限组-工厂关联，继承 BaseEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| group_id | BIGINT | FK, NOT NULL | 关联权限组 |
| factory_id | BIGINT | FK, NOT NULL | 关联工厂 |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### DataPermissionGroupWorkCenter（权限组-工作中心关联，继承 BaseEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| group_id | BIGINT | FK, NOT NULL | 关联权限组 |
| work_center_id | BIGINT | FK, NOT NULL | 关联工作中心 |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### DataPermissionGroupProcess（权限组-工序关联，继承 BaseEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| group_id | BIGINT | FK, NOT NULL | 关联权限组 |
| process_id | BIGINT | FK, NOT NULL | 关联工序 |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### 数据库索引
| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| uk_group_name | UNIQUE | group_name | 名称唯一性（WHERE deleted = 0） |
| uk_group_factory | UNIQUE | (group_id, factory_id) | 防止重复关联 |
| uk_group_work_center | UNIQUE | (group_id, work_center_id) | 防止重复关联 |
| uk_group_process | UNIQUE | (group_id, process_id) | 防止重复关联 |
| idx_pg_factory_factory | INDEX | factory_id | 按工厂反查权限组 |
| idx_pg_wc_wc | INDEX | work_center_id | 按工作中心反查权限组 |
| idx_pg_process_process | INDEX | process_id | 按工序反查权限组 |

### 数据库表 DDL（参考）

```sql
CREATE TABLE data_permission_group (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    group_name    VARCHAR(50)  NOT NULL,
    remark        VARCHAR(200) NULL,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    created_by    VARCHAR(64)  NULL,
    created_at    DATETIME     NULL,
    updated_by    VARCHAR(64)  NULL,
    updated_at    DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_group_name (group_name, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据权限组';

CREATE TABLE data_permission_group_factory (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    group_id      BIGINT       NOT NULL,
    factory_id    BIGINT       NOT NULL,
    created_by    VARCHAR(64)  NULL,
    created_at    DATETIME     NULL,
    updated_by    VARCHAR(64)  NULL,
    updated_at    DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_group_factory (group_id, factory_id),
    INDEX idx_pg_factory_factory (factory_id),
    CONSTRAINT fk_pgf_group FOREIGN KEY (group_id) REFERENCES data_permission_group(id),
    CONSTRAINT fk_pgf_factory FOREIGN KEY (factory_id) REFERENCES factory(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据权限组-工厂关联';

CREATE TABLE data_permission_group_work_center (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    group_id         BIGINT       NOT NULL,
    work_center_id   BIGINT       NOT NULL,
    created_by       VARCHAR(64)  NULL,
    created_at       DATETIME     NULL,
    updated_by       VARCHAR(64)  NULL,
    updated_at       DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_group_work_center (group_id, work_center_id),
    INDEX idx_pg_wc_wc (work_center_id),
    CONSTRAINT fk_pgw_group FOREIGN KEY (group_id) REFERENCES data_permission_group(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据权限组-工作中心关联';

CREATE TABLE data_permission_group_process (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    group_id      BIGINT       NOT NULL,
    process_id    BIGINT       NOT NULL,
    created_by    VARCHAR(64)  NULL,
    created_at    DATETIME     NULL,
    updated_by    VARCHAR(64)  NULL,
    updated_at    DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_group_process (group_id, process_id),
    INDEX idx_pg_process_process (process_id),
    CONSTRAINT fk_pgp_group FOREIGN KEY (group_id) REFERENCES data_permission_group(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据权限组-工序关联';
```

## 验收标准 (Gherkin)

### 场景 1: 创建数据权限组
```gherkin
Given 用户进入数据权限组管理页面
When 创建数据权限组，填写名称"冲压车间权限组"，备注"冲压车间相关权限"
Then 权限组创建成功，列表显示该记录
And 创建人和创建时间自动填充为当前登录人和当前时间
```

### 场景 2: 权限组名称唯一性
```gherkin
Given 已存在权限组"冲压车间权限组"
When 再次创建同名的数据权限组
Then 系统提示"权限组名称已存在"，创建失败
```

### 场景 3: 编辑权限组
```gherkin
Given 权限组"冲压车间权限组"已存在
When 编辑该权限组，将名称改为"冲压一车间权限组"
Then 权限组名称已更新
And 修改人和修改时间自动更新
```

### 场景 4: 关联工厂
```gherkin
Given 权限组"冲压车间权限组"已创建
And 工厂"FAC001"、"FAC002"已存在
When 在工厂 Tab 页选择工厂"FAC001"和"FAC002"，点击确认
Then 权限组正确关联 2 个工厂
And 工厂 Tab 页显示关联的工厂编码和名称
```

### 场景 5: 关联工作中心
```gherkin
Given 权限组"冲压车间权限组"已创建
And 工作中心"WC001"、"WC002"已存在
When 在工作中心 Tab 页选择工作中心"WC001"和"WC002"，点击确认
Then 权限组正确关联 2 个工作中心
```

### 场景 6: 关联工序
```gherkin
Given 权限组"冲压车间权限组"已创建
And 工序"PS001"、"PS002"已存在
When 在工序 Tab 页选择工序"PS001"和"PS002"，点击确认
Then 权限组正确关联 2 个工序
```

### 场景 7: 关联多页勾选
```gherkin
Given 权限组"冲压车间权限组"已创建
And 存在 30 个工厂（每页显示 10 条）
When 在第 1 页勾选工厂 1-3，翻到第 2 页勾选工厂 11-13，点击确认
Then 权限组正确关联 6 个工厂
And 翻页后之前页的勾选状态保持
```

### 场景 8: 取消关联
```gherkin
Given 权限组"冲压车间权限组"已关联工厂"FAC001"和"FAC002"
When 在工厂 Tab 页取消勾选"FAC002"，点击确认
Then 权限组仅关联"FAC001"
```

### 场景 9: 删除保护
```gherkin
Given 权限组"冲压车间权限组"已被用户权限引用
When 用户查看该权限组
Then 删除按钮置灰不可点击
```

### 场景 10: 删除未引用权限组
```gherkin
Given 权限组"测试权限组"未被任何用户权限引用
When 删除该权限组
Then 权限组被软删除（deleted = 1）
And 关联的工厂/工作中心/工序关联记录一并删除
```

### 场景 11: 重复关联防护
```gherkin
Given 权限组"冲压车间权限组"已关联工厂"FAC001"
When 再次尝试关联工厂"FAC001"
Then 关联成功，不产生重复记录（唯一索引保障）
```

### 场景 12: 模糊查询权限组
```gherkin
Given 存在权限组"冲压车间权限组"和"组装车间权限组"
When 按名称"车间"模糊查询
Then 返回"冲压车间权限组"和"组装车间权限组"
```
