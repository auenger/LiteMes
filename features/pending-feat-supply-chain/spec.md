# Feature: feat-supply-chain 供应链主数据

## 基本信息
- **ID**: feat-supply-chain
- **名称**: 供应链主数据
- **优先级**: 60
- **规模**: M（模块级）
- **依赖**: feat-material-master
- **创建时间**: 2026-04-20

## 需求来源
- 文档: 供应链主数据_功能设计_V1.0

## 模块概述
供应链主数据用于统一维护客户和供应商等基础往来单位信息，规范企业内外部业务关系管理。通过标准化客户与供应商数据配置，为订单管理、物料采购、生产协同及质量追溯提供统一的数据基础，保障业务运行的准确性和一致性。

## 子 Feature 列表

| ID | 名称 | 优先级 | 规模 | 依赖 |
|----|------|--------|------|------|
| [feat-customer](../pending-feat-customer/spec.md) | 客户管理 | 62 | S | feat-material-master |
| [feat-supplier](../pending-feat-supplier/spec.md) | 供应商管理 | 61 | S | feat-material-master |

## 依赖关系图
```
feat-material-master (前置)
  ├── feat-material-info
  └── feat-inspection-exemption (下游引用 supplier)
          │
          ▼
feat-supply-chain (模块索引)
├── feat-customer   [S] ← 客户管理
└── feat-supplier   [S] ← 供应商管理
```

## 用户价值点

1. **统一往来单位管理** — 客户和供应商集中维护，一个模块管理所有外部业务伙伴信息
2. **物料关联精准定位** — 客户关联成品物料（出库确认）、供应商关联供货物料（采购/入库），业务协同更精准
3. **数据引用安全保障** — 已被订单/采购/免检清单引用的客户或供应商不可删除，保障下游业务完整性
4. **批量数据维护** — 客户和供应商均支持 Excel 导入导出，快速初始化和迁移数据
5. **供应链数据追溯** — 为质量追溯、采购管理、出库确认等下游业务提供统一的基础数据支撑

## 上下文分析

### 关联 Feature
- **feat-material-master**（前置）— 提供物料主数据，CustomerMaterial / SupplierMaterial 通过 material_id 引用 material_master 表
- **feat-inspection-exemption**（下游引用）— 免检清单通过 supplier_id 引用供应商表（当前为逻辑引用，无 DB FK）
- **feat-enterprise-org**（间接依赖）— 提供审计基类 BaseEntity/SoftDeleteEntity、全局异常处理、通用分页模式

### 参考代码模式
- `com.litemes.domain.entity.SoftDeleteEntity` — 软删除基类，Customer/Supplier 均继承
- `com.litemes.domain.entity.BaseEntity` — 审计字段基类（createdBy/createdAt/updatedBy/updatedAt）
- 企业管理模块 `Company` — 类似的编码唯一 + 软删除 + CRUD 模式
- 物料模块 `MaterialCategory` — 类似的编码/名称唯一约束模式
- 物料模块 `MaterialMaster` — 类似的关联表设计（MaterialVersion 的 FK 模式）

### 关联文档
- [供应链主数据_功能设计_V1.0](../../docx/供应链主数据_功能设计_V1.0/供应链主数据_功能设计_V1.0.md)

## 整体数据模型关系

```
Customer ──1:N──> CustomerMaterial <──N:1── MaterialMaster
Supplier ──1:N──> SupplierMaterial  <──N:1── MaterialMaster
                                                    ▲
InspectionExemption ──N:1──────────────────────────┘ (通过 supplier_id 逻辑引用)
```

### 数据库模型设计（总览）

| 实体 | 表名 | 基类 | 主要约束 |
|------|------|------|---------|
| Customer | `customer` | SoftDeleteEntity | customer_code UNIQUE |
| CustomerMaterial | `customer_material` | SoftDeleteEntity | (customer_id, material_id) UNIQUE |
| Supplier | `supplier` | SoftDeleteEntity | supplier_code UNIQUE |
| SupplierMaterial | `supplier_material` | SoftDeleteEntity | (supplier_id, material_id) UNIQUE |

### 设计决策

| 决策 | 原因 |
|------|------|
| 客户/供应商各独立表 | 业务含义不同（客户有 type 字段，供应商有 description），字段集不完全对称，不合并为统一往来单位表 |
| 关联表独立（非 JSON/逗号分隔） | 支持按物料查客户/供应商的反向查询，支持引用完整性校验 |
| 关联表继承 SoftDeleteEntity | 关联关系取消时软删除，保留审计记录 |
| customer_code / supplier_code UNIQUE | 业务主键，创建后不可修改，需 DB 级唯一约束 |
| status TINYINT 而非 BOOLEAN | 与项目其他表保持一致（1=启用, 0=禁用） |
| material_id FK → material_master | 确保物料引用完整性，关联表与物料主表强绑定 |
| InspectionExemption 的 supplier_id 不设 DB FK | 跨模块依赖（feat-supplier 尚未开发），避免物理依赖；feat-supplier 完成后可选择性添加 |

## 模块进度
| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| feat-customer | pending | |
| feat-supplier | pending | |
