# Feature: feat-inspection-exemption 免检清单

## 基本信息
- **ID**: feat-inspection-exemption
- **名称**: 免检清单
- **优先级**: 81
- **规模**: S
- **依赖**: feat-material-info
- **父模块**: feat-material-master
- **创建时间**: 2026-04-21

## 需求来源
- [物料主数据功能设计 V1.0](../../docx/物料主数据_功能设计_V1.0/物料主数据_功能设计_V1.0.md) — 第7节 免检清单

## 需求描述
定义物料免检规则，通过供应商+物料+有效时间范围组合来确定哪些物料可以免于来料检验，减少不必要的检验流程。

## 用户价值点

1. **免检规则灵活配置** — 通过供应商+物料+有效期三个维度组合定义免检规则
2. **减少不必要检验** — 合格供应商的物料自动跳过来料检验环节
3. **有效期自动管控** — 超过有效结束日期的免检规则自动失效
4. **批量维护** — 支持 Excel 导入导出

## 免检规则业务矩阵

| 供应商 | 有效期 | 含义 |
|--------|--------|------|
| 空 | 空 | 物料全局永久免检 |
| 有值 | 空 | 指定供应商永久免检 |
| 有值 | 有值 | 指定供应商+有效期免检 |
| 空 | 有值 | 物料全局有效期免检 |

## 验收标准 (Gherkin)

### 场景 1: 免检规则生效
```gherkin
Given 供应商 S001 和物料 MAT-001 已存在
When 创建免检规则（供应商 S001 + 物料 MAT-001 + 有效期 2026-01-01 至 2026-12-31）
Then 免检规则生效
And 在有效期内，该供应商的此物料免检
```

### 场景 2: 永久免检
```gherkin
Given 物料 MAT-001 已存在
When 创建免检规则（物料 MAT-001，供应商和有效期均为空）
Then 物料 MAT-001 永久免检
```

### 场景 3: 过期自动失效
```gherkin
Given 免检规则的有效结束日期为 2026-03-01
When 当前日期超过 2026-03-01
Then 该免检规则自动失效
```

## Merge Record
- **Completed**: 2026-04-22
- **Merged Branch**: feature/inspection-exemption
- **Merge Commit**: c6444f5
- **Archive Tag**: feat-inspection-exemption-20260422
- **Conflicts**: 4 files — resolved by keeping both additions
- **Verification**: PASSED (16/16 tests, 3/3 Gherkin scenarios)
- **Files Changed**: 22
