# Feature: feat-equipment-master 设备主数据

## 基本信息
- **ID**: feat-equipment-master
- **名称**: 设备主数据
- **优先级**: 70
- **规模**: L（模块级）
- **依赖**: feat-enterprise-org
- **创建时间**: 2026-04-20

## 需求来源
- docx/设备主数据_功能设计_V1.0.md

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
feat-enterprise-org
  └── feat-equipment-type  (设备类型)
        └── feat-equipment-model  (设备型号)
              └── feat-equipment-ledger  (设备台账)
```

## 整体数据模型关系
```
EquipmentType (1) ──→ (N) EquipmentModel (1) ──→ (N) EquipmentLedger
                                                EquipmentLedger ──→ Factory (feat-enterprise-org)
```

## 模块进度
| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| feat-equipment-type | pending | |
| feat-equipment-model | pending | |
| feat-equipment-ledger | pending | |
