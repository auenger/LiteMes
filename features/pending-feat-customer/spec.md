# Feature: feat-customer 客户管理

## 基本信息
- **ID**: feat-customer
- **名称**: 客户管理
- **优先级**: 62
- **规模**: S
- **依赖**: feat-material-master
- **父模块**: feat-supply-chain
- **创建时间**: 2026-04-21

## 需求来源
- 父模块: feat-supply-chain（供应链主数据）
- 文档: 供应链主数据_功能设计_V1.0 → 第3章 客户

## 需求描述
管理 PCB 制造的客户基础信息，包括客户编码、名称、类型（外贸/内贸）、简称、联系人、电话、地址、邮箱，并支持关联成品物料，用于出库确认。

### 功能清单
| 功能 | 说明 |
|------|------|
| 创建 | 填写客户信息，校验编码和名称必填，校验编码唯一性 |
| 编辑 | 修改客户信息（编码不可修改） |
| 删除 | 已被引用的客户不可删除（按钮置灰） |
| 查询 | 按编码、名称（模糊）、状态筛选 |
| 启用/禁用 | 切换客户状态 |
| 选择物料 | 关联成品物料，支持多选 |
| 导入 | Excel 导入客户数据 |
| 导出 | 导出查询结果到 Excel |
| 日志 | 查看变更履历 |

## 数据模型

### Customer（客户）
| 字段 | 类型 | 必填 | 可编辑 | 备注 |
|------|------|------|--------|------|
| customerCode | String(50) | Y | N | 唯一 |
| customerName | String(50) | Y | Y | |
| status | Boolean | N | N | 默认启用，禁用/启用 |
| type | String(50) | N | Y | 枚举：外贸客户、内贸客户 |
| shortName | String(50) | N | Y | 简称 |
| contactPerson | String(50) | N | Y | 联系人 |
| phone | String(50) | N | Y | 电话 |
| address | String(50) | N | Y | 地址 |
| email | String(50) | N | Y | 邮箱 |
| createdBy | String | N | N | 系统登录人 |
| createdTime | DateTime | N | N | yyyy/MM/dd HH:mm:ss |
| modifiedBy | String | N | N | 系统登录人 |
| modifiedTime | DateTime | N | N | yyyy/MM/dd HH:mm:ss |

### CustomerMaterial（客户物料关联）
| 字段 | 类型 | 备注 |
|------|------|------|
| customerId | Long | 客户 ID |
| materialId | Long | 物料 ID |

## 验收标准 (Gherkin)

```gherkin
Feature: 客户管理

  Scenario: 创建客户
    Given 用户在客户管理页面点击创建
    When 填写客户编码 "C001"、客户名称 "测试客户"
    And 点击确认
    Then 客户创建成功，状态为启用

  Scenario: 客户编码唯一性校验
    Given 已存在客户编码 "C001"
    When 创建客户时输入编码 "C001"
    Then 提示客户编码已存在

  Scenario: 编辑客户
    Given 已存在客户 "C001"
    When 修改客户名称为 "新名称"
    Then 客户名称更新成功，客户编码不可修改

  Scenario: 删除被引用的客户
    Given 客户 "C001" 已被其他业务引用
    Then 删除按钮置灰，不可点击

  Scenario: 客户关联物料
    Given 客户 "C001" 已创建
    When 选择物料 "MAT001"
    Then 客户与物料关联成功

  Scenario: 查询客户
    Given 存在多个客户记录
    When 按编码模糊查询 "C00"
    Then 返回编码包含 "C00" 的客户列表
```
