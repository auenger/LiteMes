# Feature: feat-material-master 物料主数据

## 基本信息
- **ID**: feat-material-master
- **名称**: 物料主数据
- **优先级**: 80
- **规模**: L（模块级）
- **依赖**: feat-enterprise-org
- **创建时间**: 2026-04-20

## 需求来源
- [物料主数据功能设计 V1.0](../../docx/物料主数据_功能设计_V1.0/物料主数据_功能设计_V1.0.md)

## 模块概述
统一维护 PCB 制造所需的物料基础数据，包括计量单位、单位换算、物料分类、物料信息及免检清单。通过标准化物料属性与计量规则配置，为生产计划、物料领用、库存管理及质量管控提供统一的数据基础。

## 子 Feature 列表

| ID | 名称 | 优先级 | 规模 | 依赖 |
|----|------|--------|------|------|
| [feat-uom](../pending-feat-uom/spec.md) | 计量单位与换算 | 84 | M | feat-enterprise-org |
| [feat-material-category](../pending-feat-material-category/spec.md) | 物料分类 | 83 | S | feat-enterprise-org |
| [feat-material-info](../pending-feat-material-info/spec.md) | 物料基本信息 | 82 | M | feat-uom, feat-material-category |
| [feat-inspection-exemption](../pending-feat-inspection-exemption/spec.md) | 免检清单 | 81 | S | feat-material-info |

## 依赖关系图
```
feat-enterprise-org
├── feat-uom (计量单位与换算)
├── feat-material-category (物料分类)
│
└── feat-material-info (物料基本信息) ← feat-uom + feat-material-category
    └── feat-inspection-exemption (免检清单)
```

## 整体数据模型关系

### ER 关系图

```
┌──────────────┐    ┌──────────────────────┐
│     Uom      │    │    UomConversion     │
│──────────────│    │──────────────────────│
│ id (PK)      │◄───│ from_uom_id (FK)     │
│ uom_code (UK)│◄───│ to_uom_id (FK)       │
│ uom_name (UK)│    │ conversion_rate      │
│ status       │    │ status               │
│ precision    │    │ UK(from_uom_id, to_uom_id) │
└──────┬───────┘    └──────────────────────┘
       │
       │ (uom_id FK)
       ▼
┌──────────────────────┐    ┌──────────────────────┐
│   MaterialMaster     │    │   MaterialCategory   │
│──────────────────────│    │──────────────────────│
│ id (PK)              │    │ id (PK)              │
│ material_code (UK)   │    │ category_code (UK)   │
│ material_name (UK)   │    │ category_name (UK)   │
│ basic_category       │    │ is_quality_category  │
│ category_id (FK) ────│───►│ parent_id (FK→self)  │
│ attribute_category   │    │ status               │
│ uom_id (FK)          │    └──────────────────────┘
│ PCB 属性字段(14个)    │
│ ext1~ext5            │    ┌──────────────────────┐
│ status               │    │   MaterialVersion    │
└──────┬───────┬───────┘    │──────────────────────│
       │       │            │ id (PK)              │
       │       └───────────►│ material_id (FK)     │
       │                    │ version_no           │
       │                    │ UK(material_id, version_no) │
       │                    └──────────────────────┘
       │
       │ (material_id FK)
       ▼
┌──────────────────────┐
│ InspectionExemption  │
│──────────────────────│
│ id (PK)              │
│ material_id (FK)     │
│ material_code (冗余) │
│ material_name (冗余) │
│ supplier_id (可空)*  │───► [supplier 表 - feat-supplier 模块]
│ supplier_code (冗余) │     * 无DB级FK，逻辑引用
│ supplier_name (冗余) │
│ valid_from / valid_to│
│ status               │
└──────────────────────┘
```

### 跨模块引用说明

| 本模块表 | 外部表 | 引用方式 | 外部模块 | 状态 |
|---------|--------|---------|---------|------|
| InspectionExemption.supplier_id | supplier | 逻辑引用，无DB级FK | feat-supplier | 未开发 |

**所有实体均继承 SoftDeleteEntity（审计字段 + 软删除），主键为 BIGINT AUTO_INCREMENT。**

### 建表顺序（按依赖关系）

```
1. uom                    — 无外部依赖
2. uom_conversion         — 依赖 uom
3. material_category      — 无外部依赖（parent_id 自引用）
4. material_master        — 依赖 uom, material_category
5. material_version       — 依赖 material_master
6. inspection_exemption   — 依赖 material_master
```

## 模块进度
| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| feat-uom | pending | 计量单位与换算 |
| feat-material-category | pending | 物料分类 |
| feat-material-info | pending | 物料基本信息 |
| feat-inspection-exemption | pending | 免检清单 |
