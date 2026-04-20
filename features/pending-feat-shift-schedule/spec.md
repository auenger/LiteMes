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

### ShiftSchedule (班制)
| 字段 | 类型 | 约束 |
|------|------|------|
| id | UUID | PK |
| shift_code | VARCHAR(50) | UNIQUE, NOT NULL |
| name | VARCHAR(50) | NOT NULL |
| is_default | BOOLEAN | DEFAULT false |
| status | BOOLEAN | DEFAULT true |
| created_by | VARCHAR(50) | |
| created_at | DATETIME | |
| updated_by | VARCHAR(50) | |
| updated_at | DATETIME | |

### Shift (班次)
| 字段 | 类型 | 约束 |
|------|------|------|
| id | UUID | PK |
| shift_schedule_id | UUID | FK -> ShiftSchedule, NOT NULL |
| shift_code | VARCHAR(50) | UNIQUE, NOT NULL |
| name | VARCHAR(50) | NOT NULL |
| start_time | TIME | NOT NULL |
| end_time | TIME | NOT NULL |
| cross_day | BOOLEAN | DEFAULT false |
| status | BOOLEAN | DEFAULT true |
| created_by | VARCHAR(50) | |
| created_at | DATETIME | |

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
