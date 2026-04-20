# Feature: feat-enterprise-common 企业管理通用功能

## 基本信息
- **ID**: feat-enterprise-common
- **名称**: 企业管理通用功能
- **优先级**: 80
- **规模**: S
- **依赖**: feat-company, feat-factory, feat-department, feat-shift-schedule
- **创建时间**: 2026-04-20

## 需求来源
- `docx/企业管理_功能设计_V1.0/企业管理_功能设计_V1.0.md` — 通用说明

## 需求描述
企业管理模块的通用功能，包括变更日志、表格设置、导入导出（通用）等。

### 功能清单
| 功能 | 说明 |
|------|------|
| 变更日志 | 记录数据的增删改操作，供用户查看变更履历 |
| 表格设置 | 设置列的显示/隐藏、列宽、顺序、对齐方式 |

## 验收标准

### Gherkin 场景

```gherkin
Feature: 变更日志

Scenario: 查看变更日志
  Given 公司"COM001"刚刚被创建
  When 用户点击"查看变更履历"
  Then 显示变更记录：创建人、创建时间

Scenario: 查看编辑日志
  Given 公司"COM001"刚刚被编辑
  When 用户点击"查看变更履历"
  Then 显示变更记录：修改人、修改时间、变更内容
```

```gherkin
Feature: 表格设置

Scenario: 设置列显示
  Given 用户在列表页面
  When 点击"表格设置"
  Then 弹出设置面板，可选择列的显示/隐藏

Scenario: 调整列宽
  Given 用户在列表页面
  When 拖动列边框调整列宽
  Then 列宽已调整并记住用户偏好
```

## 技术说明
- 变更日志采用审计字段（created_by/updated_by 等）实现
- 表格设置使用前端本地存储保存用户偏好
