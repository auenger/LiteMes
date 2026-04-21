# Feature: feat-process 工序管理

## 基本信息
- **ID**: feat-process
- **名称**: 工序管理
- **优先级**: 88
- **规模**: M
- **依赖**: feat-work-center
- **父模块**: feat-enterprise-org
- **创建时间**: 2026-04-21

## 需求来源
- 数据权限_功能设计_V1.0.docx — 工序作为数据权限的最细粒度维度
- PCB 制造场景通用需求 — 工序是 MES 系统制造过程的核心定义

## 用户价值点
1. **制造过程标准化**：统一定义工厂内的工序步骤（如开料、钻孔、曝光、蚀刻等），为报工、质检、排产提供标准化的过程定义
2. **数据权限细粒度控制**：工序是数据权限三维度中最细的粒度，实现按工序级别的数据隔离和访问控制
3. **全链路追溯基础**：工序数据是生产追溯、质量管控、效率分析的基础维度

## 需求描述
维护工序基础数据。工序隶属于工作中心，是 MES 系统中定义制造过程步骤的核心实体。工序数据主要服务于数据权限过滤、报工管理、质量检验等下游模块。

### 功能清单
| 功能 | 说明 |
|------|------|
| 工序查询 | 按工序编码、工序名称、所属工作中心查询，按状态筛选 |
| 工序创建 | 填写工序编码、名称、所属工作中心 |
| 工序编辑 | 修改工序名称（编码不可修改） |
| 工序删除 | 未被引用时可删除，被引用时不可删除 |
| 状态管理 | 启用/禁用工序 |

### 页面设计
- 主页面：工序列表（查询条件：编码/名称模糊查询 + 工作中心/工厂级联筛选 + 状态筛选）
- 创建/编辑弹窗：编码、名称、所属工厂（选工厂后联动工作中心下拉）

### 业务规则
- 工序编码必填且唯一，创建后不可修改
- 工序名称必填
- 必须选择所属工作中心
- 被数据权限、报工记录、质检规则等引用时不可删除
- 禁用不影响已关联的历史数据

## 上下文分析

### 参考代码（关联模块实现后）
- `domain/entity/WorkCenter.java` — 工厂下属实体的标准模式
- `domain/entity/Department.java` — 编码唯一 + 外键关联 + 引用检查模式
- `application/service/WorkCenterService.java` — CRUD + 状态管理模式参考
- `web/resource/FactoryResource.java` — JAX-RS Resource 标准 DTO 交互

### 关联文档
- 数据权限_功能设计_V1.0.docx — 工序作为权限维度的引用
- CLAUDE.md — 四层架构规范

### 关联 Feature
- **feat-work-center** — 直接上游，提供 WorkCenter 实体，工序归属于工作中心
- **feat-factory** — 间接上游，通过工作中心关联工厂，提供工厂下拉和级联选择
- **feat-enterprise-common** — 通用下拉接口和级联选择器（工厂→工作中心联动）
- **feat-data-permission** — 工序是数据权限最细粒度的过滤维度
- **feat-inspection-exemption** — 免检清单可能引用工序（物料+工序维度）

## 数据模型

### Process（工序，继承 SoftDeleteEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| process_code | VARCHAR(50) | UNIQUE, NOT NULL | 工序编码，创建后不可修改 |
| name | VARCHAR(50) | NOT NULL | 工序名称 |
| work_center_id | BIGINT | FK -> WorkCenter, NOT NULL | 所属工作中心 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：1=启用，0=禁用 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 软删除标记（@TableLogic） |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### 数据库索引
| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| uk_process_code | UNIQUE | process_code | 编码唯一性（WHERE deleted = 0） |
| idx_process_work_center | INDEX | work_center_id | 按工作中心查询工序 |

### 数据库表 DDL（参考）

```sql
CREATE TABLE process (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    process_code    VARCHAR(50)  NOT NULL,
    name            VARCHAR(50)  NOT NULL,
    work_center_id  BIGINT       NOT NULL,
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted         TINYINT      NOT NULL DEFAULT 0,
    created_by      VARCHAR(64)  NULL,
    created_at      DATETIME     NULL,
    updated_by      VARCHAR(64)  NULL,
    updated_at      DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_process_code (process_code, deleted),
    INDEX idx_process_work_center (work_center_id),
    CONSTRAINT fk_process_wc FOREIGN KEY (work_center_id) REFERENCES work_center(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序';
```

### 引用检查规则
- 被删除前需检查：
  - `DataPermissionGroupProcess.process_id` 是否有引用
  - `UserDataPermissionProcess.process_id` 是否有引用
  - 后续扩展：报工记录、质检规则等
- 被禁用前无需强制检查

## 验收标准 (Gherkin)

### 场景 1: 创建工序
```gherkin
Given 工作中心"WC001"已存在
When 创建工序，填写编码"PS001"、名称"开料"、所属工作中心"WC001"
Then 工序创建成功
And 编码"PS001"已存在
```

### 场景 2: 编码唯一性
```gherkin
Given 工序"PS001"已存在
When 创建工序，填写编码"PS001"
Then 创建失败，提示"工序编码已存在"
```

### 场景 3: 编辑工序
```gherkin
Given 工序"PS001"已存在
When 编辑工序，将名称改为"激光开料"
Then 工序名称已更新
And 编码保持为"PS001"不可修改
```

### 场景 4: 删除未引用工序
```gherkin
Given 工序"PS001"未被任何数据权限或其他数据引用
When 删除工序"PS001"
Then 删除成功（软删除）
```

### 场景 5: 删除被引用工序
```gherkin
Given 工序"PS001"已被数据权限引用
When 删除工序"PS001"
Then 删除失败，提示"该工序已被引用，无法删除"
```

### 场景 6: 按工作中心筛选
```gherkin
Given 工作中心"WC001"下有工序"PS001"、"PS002"
And 工作中心"WC002"下有工序"PS003"
When 按工作中心"WC001"筛选
Then 只返回"PS001"和"PS002"
```

### 场景 7: 按工厂级联筛选
```gherkin
Given 工厂"FAC001"下有工作中心"WC001"（含工序PS001、PS002）
And 工厂"FAC002"下有工作中心"WC002"（含工序PS003）
When 选择工厂"FAC001"后筛选工序列表
Then 只返回"PS001"和"PS002"
```

### 场景 8: 模糊查询
```gherkin
Given 工序"开料"和"钻孔"已存在
When 按名称"开"模糊查询
Then 只返回"开料"
```

### 场景 9: 状态筛选
```gherkin
Given 启用的工序"PS001"和禁用的"PS002"已存在
When 按状态"禁用"筛选
Then 只返回"PS002"
```

### 场景 10: 必须选择工作中心
```gherkin
Given 创建工序时未选择工作中心
When 提交创建
Then 提示"请选择所属工作中心"
```

### 场景 11: 创建弹窗工厂-工作中心联动
```gherkin
Given 工厂"FAC001"下有工作中心"WC001"、"WC002"
And 工厂"FAC002"下有工作中心"WC003"
When 创建工序时选择工厂"FAC001"
Then 工作中心下拉仅显示"WC001"和"WC002"
When 切换工厂为"FAC002"
Then 工作中心下拉更新为"WC003"
```

## 合并记录
- **完成时间**: 2026-04-22
- **合并分支**: feature/feat-process
- **合并提交**: 4f40d5e
- **归档标签**: feat-process-20260422
- **冲突记录**: 无冲突
- **验证状态**: warning (编译通过，集成测试需要 MySQL 运行环境)
- **开发统计**: 16 文件变更, 1209 行新增, 后端全部实现
