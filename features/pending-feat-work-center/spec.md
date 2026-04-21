# Feature: feat-work-center 工作中心管理

## 基本信息
- **ID**: feat-work-center
- **名称**: 工作中心管理
- **优先级**: 89
- **规模**: M
- **依赖**: feat-factory
- **父模块**: feat-enterprise-org
- **创建时间**: 2026-04-21

## 需求来源
- 数据权限_功能设计_V1.0.docx — 工作中心作为数据权限的维度之一
- PCB 制造场景通用需求 — 工作中心是 MES 系统的基础制造组织单元

## 用户价值点
1. **制造组织建模**：将工厂下的生产资源按工作中心进行组织，为排产、报工、数据权限提供统一的组织维度
2. **数据权限维度**：工作中心作为数据权限过滤的核心维度之一，实现按工作中心粒度的数据隔离
3. **基础数据复用**：工作中心数据供设备绑定、工序管理、数据权限等多模块引用，避免重复维护

## 需求描述
维护工作中心基础数据。工作中心隶属于工厂，是 MES 系统中的基础制造组织单元，代表具有特定生产能力的一组资源（设备、人员等）。工作中心数据主要服务于数据权限过滤、设备绑定和工序管理等下游模块。

### 功能清单
| 功能 | 说明 |
|------|------|
| 工作中心查询 | 按工作中心编码、工作中心名称、所属工厂查询，按状态筛选 |
| 工作中心创建 | 填写工作中心编码、名称、所属工厂 |
| 工作中心编辑 | 修改工作中心名称（编码不可修改） |
| 工作中心删除 | 未被引用时可删除，被引用时不可删除 |
| 状态管理 | 启用/禁用工作中心 |

### 页面设计
- 主页面：工作中心列表（查询条件：编码/名称模糊查询 + 工厂下拉筛选 + 状态筛选）
- 创建/编辑弹窗：编码、名称、所属工厂（创建时可选，编辑时只读）

### 业务规则
- 工作中心编码必填且唯一，创建后不可修改
- 工作中心名称必填
- 必须选择所属工厂
- 被数据权限、设备台账、工序等引用时不可删除
- 禁用不影响已关联的历史数据，仅影响新建关联时的下拉列表

## 上下文分析

### 参考代码（关联模块实现后）
- `domain/entity/Factory.java` — SoftDeleteEntity 继承模式、编码唯一性、引用检查
- `domain/entity/Department.java` — 工厂下属实体的标准模式（factory_id 外键）
- `application/service/FactoryService.java` — CRUD + 状态管理 + 引用检查
- `web/resource/FactoryResource.java` — JAX-RS Resource 标准 DTO 交互

### 关联文档
- 数据权限_功能设计_V1.0.docx — 工作中心作为权限维度的引用
- CLAUDE.md — 四层架构规范

### 关联 Feature
- **feat-factory** — 直接上游，提供 Factory 实体和工厂下拉数据
- **feat-enterprise-common** — 通用下拉接口，工作中心下拉数据供其他模块使用
- **feat-process** — 下游依赖本 feature，工序归属于工作中心
- **feat-data-permission** — 工作中心作为数据权限的三维过滤之一
- **feat-equipment-ledger** — 设备台账可能绑定工作中心（待确认）

## 数据模型

### WorkCenter（工作中心，继承 SoftDeleteEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| work_center_code | VARCHAR(50) | UNIQUE, NOT NULL | 工作中心编码，创建后不可修改 |
| name | VARCHAR(50) | NOT NULL | 工作中心名称 |
| factory_id | BIGINT | FK -> Factory, NOT NULL | 所属工厂 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：1=启用，0=禁用 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 软删除标记（@TableLogic） |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### 数据库索引
| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| uk_work_center_code | UNIQUE | work_center_code | 编码唯一性（WHERE deleted = 0） |
| idx_wc_factory | INDEX | factory_id | 按工厂查询工作中心 |

### 数据库表 DDL（参考）

```sql
CREATE TABLE work_center (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    work_center_code  VARCHAR(50)  NOT NULL,
    name              VARCHAR(50)  NOT NULL,
    factory_id        BIGINT       NOT NULL,
    status            TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted           TINYINT      NOT NULL DEFAULT 0,
    created_by        VARCHAR(64)  NULL,
    created_at        DATETIME     NULL,
    updated_by        VARCHAR(64)  NULL,
    updated_at        DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_work_center_code (work_center_code, deleted),
    INDEX idx_wc_factory (factory_id),
    CONSTRAINT fk_wc_factory FOREIGN KEY (factory_id) REFERENCES factory(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作中心';
```

### 引用检查规则
- 被删除前需检查：
  - `Process.work_center_id` 是否有引用
  - `DataPermissionGroupWorkCenter.work_center_id` 是否有引用
  - `UserDataPermissionWorkCenter.work_center_id` 是否有引用
- 被禁用前无需强制检查（不影响已关联数据）

## 验收标准 (Gherkin)

### 场景 1: 创建工作中心
```gherkin
Given 工厂"FAC001"已存在
When 创建工作中心，填写编码"WC001"、名称"冲压工作中心"、所属工厂"FAC001"
Then 工作中心创建成功
And 编码"WC001"已存在
```

### 场景 2: 编码唯一性
```gherkin
Given 工作中心"WC001"已存在
When 创建工作中心，填写编码"WC001"
Then 创建失败，提示"工作中心编码已存在"
```

### 场景 3: 编辑工作中心
```gherkin
Given 工作中心"WC001"已存在
When 编辑工作中心，将名称改为"新冲压工作中心"
Then 工作中心名称已更新
And 编码保持为"WC001"不可修改
```

### 场景 4: 删除未引用工作中心
```gherkin
Given 工作中心"WC001"未被任何工序或数据权限引用
When 删除工作中心"WC001"
Then 删除成功（软删除）
```

### 场景 5: 删除被引用工作中心
```gherkin
Given 工作中心"WC001"已被工序引用
When 删除工作中心"WC001"
Then 删除失败，提示"该工作中心已被引用，无法删除"
```

### 场景 6: 按工厂筛选
```gherkin
Given 工厂"FAC001"下有工作中心"WC001"、"WC002"
And 工厂"FAC002"下有工作中心"WC003"
When 按工厂"FAC001"筛选
Then 只返回"WC001"和"WC002"
```

### 场景 7: 模糊查询
```gherkin
Given 工作中心"冲压工作中心"和"组装工作中心"已存在
When 按名称"冲压"模糊查询
Then 只返回"冲压工作中心"
```

### 场景 8: 状态筛选
```gherkin
Given 启用的工作中心"WC001"和禁用的"WC002"已存在
When 按状态"禁用"筛选
Then 只返回"WC002"
```

### 场景 9: 禁用工作中心
```gherkin
Given 工作中心"WC001"为启用状态
When 禁用工作中心"WC001"
Then 状态变为禁用
And 下拉列表中不再出现"WC001"
And 已关联的历史数据不受影响
```

### 场景 10: 必须选择工厂
```gherkin
Given 创建工作中心时未选择工厂
When 提交创建
Then 提示"请选择所属工厂"
```
