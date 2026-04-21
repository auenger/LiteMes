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
feat-supply-chain (模块索引)
├── feat-customer   [S] ← 客户管理
└── feat-supplier   [S] ← 供应商管理
```

## 整体数据模型关系
```
Customer ──1:N──> CustomerMaterial <──N:1── Material
Supplier ──1:N──> SupplierMaterial  <──N:1── Material
```

## 模块进度
| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| feat-customer | pending | |
| feat-supplier | pending | |
