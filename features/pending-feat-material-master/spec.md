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
```
Uom ──────────────────┐
  ↑                    │
  │ (单位换算)          │ (单位引用)
UomConversion    MaterialMaster
                    ↑  │
       (分类引用)  │  │ (物料引用)
                    │  ↓
            MaterialCategory  InspectionExemption
                                    ↑
                              (供应商引用)
                            [供应商表 - 外部模块]
```

## 模块进度
| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| feat-uom | pending | 计量单位与换算 |
| feat-material-category | pending | 物料分类 |
| feat-material-info | pending | 物料基本信息 |
| feat-inspection-exemption | pending | 免检清单 |
