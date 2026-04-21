# Feature: feat-shift-schedule 班制班次管理

## 基本信息
- **ID**: feat-shift-schedule
- **名称**: 班制班次管理
- **优先级**: 90
- **规模**: M
- **依赖**: feat-project-scaffold
- **创建时间**: 2026-04-20

## 需求来源
- `docx/企业管理_功能设计_V1.0/企业管理_功能设计_V1.0.md` — 班制

## 需求描述
维护生产班制和班次数据，支持班制的增删改查、班次的增删改查，以及班制的导入导出功能。

### 功能清单
| 功能 | 说明 |
|------|------|
| 班制查询 | 按班制编码、班制名称模糊查询，按状态筛选 |
| 班制创建 | 填写班制编码、班制名称、是否默认 |
| 班制编辑 | 修改班制名称、是否默认（班制编码不可修改） |
| 班制删除 | 未被引用时可删除 |
| 班次查询 | 查看某班制下的班次列表 |
| 班次创建 | 填写班次编码、班次名称、开始时间、结束时间、是否跨天 |
| 班次编辑 | 修改班次名称、开始时间、结束时间、是否跨天（班次编码不可修改） |
| 班次删除 | 删除班次 |
| 班制导入 | Excel 导入班制数据 |
| 班制导出 | Excel 导出班制数据 |
| 状态管理 | 启用/禁用班制和班次 |

## 数据模型

### ShiftSchedule（班制，继承 SoftDeleteEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| shift_code | VARCHAR(50) | UNIQUE, NOT NULL | 班制编码，创建后不可修改 |
| name | VARCHAR(50) | NOT NULL | 班制名称 |
| is_default | TINYINT | NOT NULL, DEFAULT 0 | 是否默认：1=是，0=否 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：1=启用，0=禁用 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 软删除标记（@TableLogic） |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### Shift（班次，继承 SoftDeleteEntity）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| shift_schedule_id | BIGINT | FK -> ShiftSchedule, NOT NULL | 所属班制 |
| shift_code | VARCHAR(50) | UNIQUE, NOT NULL | 班次编码，创建后不可修改 |
| name | VARCHAR(50) | NOT NULL | 班次名称 |
| start_time | TIME | NOT NULL | 开始时间 |
| end_time | TIME | NOT NULL | 结束时间 |
| cross_day | TINYINT | NOT NULL, DEFAULT 0 | 是否跨天：1=是，0=否 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：1=启用，0=禁用 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 软删除标记（@TableLogic） |
| created_by | VARCHAR(64) | AUTO FILL | 创建人 |
| created_at | DATETIME | AUTO FILL | 创建时间 |
| updated_by | VARCHAR(64) | AUTO FILL | 修改人 |
| updated_at | DATETIME | AUTO FILL | 修改时间 |

### 数据库索引
| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| uk_shift_schedule_code | UNIQUE | shift_code | 班制编码唯一性（WHERE deleted = 0） |
| uk_shift_code | UNIQUE | shift_code | 班次编码唯一性（WHERE deleted = 0） |
| idx_shift_schedule | INDEX | shift_schedule_id | 按班制查班次 |

### 数据库表 DDL（参考）

```sql
CREATE TABLE shift_schedule (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    shift_code    VARCHAR(50)  NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    is_default    TINYINT      NOT NULL DEFAULT 0 COMMENT '1=默认,0=非默认',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted       TINYINT      NOT NULL DEFAULT 0,
    created_by    VARCHAR(64)  NULL,
    created_at    DATETIME     NULL,
    updated_by    VARCHAR(64)  NULL,
    updated_at    DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_shift_schedule_code (shift_code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班制';

CREATE TABLE shift (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    shift_schedule_id  BIGINT       NOT NULL,
    shift_code         VARCHAR(50)  NOT NULL,
    name               VARCHAR(50)  NOT NULL,
    start_time         TIME         NOT NULL,
    end_time           TIME         NOT NULL,
    cross_day          TINYINT      NOT NULL DEFAULT 0 COMMENT '1=跨天,0=不跨天',
    status             TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用,0=禁用',
    deleted            TINYINT      NOT NULL DEFAULT 0,
    created_by         VARCHAR(64)  NULL,
    created_at         DATETIME     NULL,
    updated_by         VARCHAR(64)  NULL,
    updated_at         DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_shift_code (shift_code, deleted),
    INDEX idx_shift_schedule (shift_schedule_id),
    CONSTRAINT fk_shift_schedule FOREIGN KEY (shift_schedule_id) REFERENCES shift_schedule(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班次';
```

### 引用检查规则
- 班制被删除前需检查 `Shift.shift_schedule_id` 是否有班次
- 班制禁用不影响已有班次状态

## 验收标准

### Gherkin 场景

```gherkin
Feature: 班制管理

Scenario: 创建班制
  Given 用户已登录并有管理员权限
  When 创建班制，填写班制编码"SFT001"、班制名称"正常班"、是否默认"是"
  Then 班制创建成功

Scenario: 设置默认班制
  Given 班制"SFT001"已存在且为默认
  When 创建新班制"SFT002"并设置为默认
  Then "SFT002"为默认班制
  And "SFT001"不再为默认

Scenario: 创建班次
  Given 班制"SFT001"已存在
  When 在班制下创建班次，填写班次编码"SHIFT001"、班次名称"白班"、开始时间"08:00"、结束时间"17:00"、是否跨天"否"
  Then 班次创建成功

Scenario: 创建跨天班次
  Given 班制"SFT001"已存在
  When 创建班次，填写班次编码"SHIFT002"、班次名称"夜班"、开始时间"22:00"、结束时间"06:00"、是否跨天"是"
  Then 班次创建成功
  And 系统正确识别跨天班次

Scenario: 跨天班次工时计算
  Given 跨天班次22:00-06:00已创建
  When 计算该班次工时
  Then 工时为8小时

Scenario: 导入班制
  Given Excel文件包含班制数据
  When 点击导入并选择文件
  Then 班制数据导入成功
  And 显示导入结果

Scenario: 导出班制
  Given 班制数据已存在
  When 点击导出Excel
  Then 下载包含班制数据的Excel文件
```

## 业务规则
1. 同一时刻只能有一个默认班制
2. 跨天班次的结束时间早于开始时间时，系统识别为跨天
3. 班制编码和班次编码创建后不可修改

## 合并记录
- **完成时间**: 2026-04-21
- **合并分支**: feature/feat-shift-schedule
- **合并提交**: 8a90cd4
- **归档标签**: feat-shift-schedule-20260421
- **冲突**: 无
- **验证**: 7/7 测试通过, 5/7 Gherkin 场景通过, 2 延期 (Excel 导入/导出)
- **变更统计**: 23 个文件, 2190 行新增
- **延期事项**: Excel 导入/导出服务和变更日志记录延期至 feat-enterprise-common
