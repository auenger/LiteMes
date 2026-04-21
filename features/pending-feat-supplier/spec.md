# Feature: feat-supplier 供应商管理

## 基本信息
- **ID**: feat-supplier
- **名称**: 供应商管理
- **优先级**: 61
- **规模**: S
- **依赖**: feat-material-master
- **父模块**: feat-supply-chain
- **创建时间**: 2026-04-21

## 需求来源
- 父模块: feat-supply-chain（供应链主数据）
- 文档: 供应链主数据_功能设计_V1.0 → 第4章 供应商

## 需求描述
管理 PCB 制造的供应商基础信息，包括供应商编码、名称、简称、联系人、电话、地址、邮箱、描述，并支持关联供货物料，用于采购、入库和质量追溯。

### 功能清单
| 功能 | 说明 |
|------|------|
| 创建 | 填写供应商信息，校验编码和名称必填，校验编码唯一性 |
| 编辑 | 修改供应商信息（编码不可修改） |
| 删除 | 已被引用的供应商不可删除（按钮置灰） |
| 查询 | 按编码、名称（模糊）、状态筛选 |
| 启用/禁用 | 切换供应商状态 |
| 选择物料 | 关联供货物料，支持多选 |
| 导入 | Excel 导入供应商数据（含编码唯一性校验） |
| 导出 | 导出查询结果到 Excel |
| 日志 | 查看变更履历 |

## 数据模型

### Supplier（供应商）
| 字段 | 类型 | 必填 | 可编辑 | 备注 |
|------|------|------|--------|------|
| supplierCode | String(50) | Y | N | 唯一 |
| supplierName | String(255) | Y | Y | 列宽显示20个中文长度 |
| status | Boolean | N | N | 默认启用，禁用/启用 |
| shortName | String(50) | N | Y | 简称 |
| contactPerson | String(50) | N | Y | 联系人 |
| phone | String(50) | N | Y | 电话 |
| address | String(50) | N | Y | 地址 |
| email | String(50) | N | Y | 邮箱 |
| description | String(50) | N | Y | 描述 |
| createdBy | String | N | N | 系统登录人 |
| createdTime | DateTime | N | N | yyyy/MM/dd HH:mm:ss |
| modifiedBy | String | N | N | 系统登录人 |
| modifiedTime | DateTime | N | N | yyyy/MM/dd HH:mm:ss |

### SupplierMaterial（供应商物料关联）
| 字段 | 类型 | 备注 |
|------|------|------|
| supplierId | Long | 供应商 ID |
| materialId | Long | 物料 ID |

## 验收标准 (Gherkin)

```gherkin
Feature: 供应商管理

  Scenario: 创建供应商
    Given 用户在供应商管理页面点击创建
    When 填写供应商编码 "S001"、供应商名称 "测试供应商"
    And 点击确认
    Then 供应商创建成功，状态为启用

  Scenario: 供应商编码唯一性校验
    Given 已存在供应商编码 "S001"
    When 创建供应商时输入编码 "S001"
    Then 提示供应商编码已存在

  Scenario: 编辑供应商
    Given 已存在供应商 "S001"
    When 修改供应商名称为 "新名称"
    Then 供应商名称更新成功，供应商编码不可修改

  Scenario: 删除被引用的供应商
    Given 供应商 "S001" 已被其他业务引用
    Then 删除按钮置灰，不可点击

  Scenario: 供应商关联物料
    Given 供应商 "S001" 已创建
    When 选择物料 "MAT001"
    Then 供应商可供货物料清单更新成功

  Scenario: 导入供应商（编码重复）
    Given 已存在供应商编码 "S001"
    When 导入包含编码 "S001" 的 Excel
    Then 提示编码已存在，拒绝导入

  Scenario: 查询供应商
    Given 存在多个供应商记录
    When 按名称模糊查询 "测试"
    Then 返回名称包含 "测试" 的供应商列表
```
