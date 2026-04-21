# Feature: feat-equipment-master 设备主数据

## 基本信息
- **ID**: feat-equipment-master
- **名称**: 设备主数据
- **优先级**: 70
- **规模**: L（模块级）
- **依赖**: feat-enterprise-org
- **创建时间**: 2026-04-20

## 需求来源
- docx/设备主数据_功能设计_V1.0.docx

## 模块概述
设备主数据用于统一维护设备类型、设备型号及设备台账等基础设备信息，规范企业设备管理体系。通过标准化设备属性与分类配置，为设备运行管理、生产调度、维护保养及质量追溯提供统一的数据基础。

## 子 Feature 列表

| ID | 名称 | 优先级 | 规模 | 依赖 |
|----|------|--------|------|------|
| [feat-equipment-type](../pending-feat-equipment-type/spec.md) | 设备类型 | 73 | S | feat-enterprise-org |
| [feat-equipment-model](../pending-feat-equipment-model/spec.md) | 设备型号 | 72 | S | feat-equipment-type |
| [feat-equipment-ledger](../pending-feat-equipment-ledger/spec.md) | 设备台账 | 71 | M | feat-equipment-model |

## 依赖关系图
```
feat-enterprise-org (企业管理模块)
  └── feat-equipment-type  (设备类型)
        └── feat-equipment-model  (设备型号)
              └── feat-equipment-ledger  (设备台账) ──→ Factory (feat-enterprise-org)
```

## 整体数据模型关系

### ER 关系图

```
┌──────────────────────┐    ┌──────────────────────┐
│   EquipmentType      │    │   EquipmentModel     │
│──────────────────────│    │──────────────────────│
│ id (PK)              │◄───│ equipment_type_id(FK) │
│ type_code (UK)       │    │ id (PK)              │◄───┐
│ type_name            │    │ model_code (UK)      │    │
│ status               │    │ model_name           │    │
└──────────────────────┘    │ status               │    │
                            └──────────────────────┘    │
                                                       │
┌──────────────────────────────────────────────────┐    │
│              EquipmentLedger                      │    │
│──────────────────────────────────────────────────│    │
│ id (PK)                                          │    │
│ equipment_code (UK)                              │    │
│ equipment_name                                   │    │
│ equipment_model_id (FK) ─────────────────────────│────┘
│ equipment_type_id (FK, 冗余) ──► EquipmentType   │
│ factory_id (FK) ──► Factory (feat-enterprise-org)│
│ running_status  (枚举: 运行/故障/停机/维修保养)   │
│ manage_status   (枚举: 使用中/闲置/报废)          │
│ manufacturer                                      │
│ commissioning_date                                │
│ status                                            │
└──────────────────────────────────────────────────┘
```

### 实体基类映射

| 基类 | 字段 | 适用实体 |
|------|------|---------|
| SoftDeleteEntity | created_by, created_at, updated_by, updated_at, deleted | EquipmentType, EquipmentModel, EquipmentLedger |

### 跨模块引用说明

| 本模块表 | 外部表 | 引用方式 | 外部模块 | 状态 |
|---------|--------|---------|---------|------|
| EquipmentLedger.factory_id | factory | DB级 FK | feat-factory (feat-enterprise-org) | pending |

**所有实体均继承 SoftDeleteEntity（审计字段 + 软删除），主键为 BIGINT AUTO_INCREMENT。**

### 建表顺序（按依赖关系）

```
1. equipment_type      — 无外部依赖
2. equipment_model     — 依赖 equipment_type
3. equipment_ledger    — 依赖 equipment_model, factory (feat-enterprise-org)
```

## 模块进度
| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| feat-equipment-type | pending | 设备类型 |
| feat-equipment-model | pending | 设备型号 |
| feat-equipment-ledger | pending | 设备台账 |
