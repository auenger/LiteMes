# Checklist: feat-inspection-exemption 免检清单

## 数据层
- [x] `inspection_exemption` 表 DDL 已创建
- [x] material_id FK→material_master 已配置
- [x] supplier_id 无 DB 级 FK（逻辑引用，跨模块处理）
- [x] 有效期索引 idx_valid_date (valid_from, valid_to) 已创建
- [x] InspectionExemption 实体类继承 SoftDeleteEntity
- [x] MyBatis Mapper + Repository 接口 + 实现已创建

## 后端逻辑
- [x] InspectionExemption CRUD API 已实现（创建/编辑/删除/查询/启用禁用）
- [x] 物料必填校验 + 物料名称自动带出 — 场景1, 2
- [ ] 供应商名称自动带出 — 等待 feat-supplier
- [x] 免检规则业务矩阵（4种组合正确处理）
- [x] 有效期过期判断逻辑 — 场景3
- [x] 变更日志记录
- [x] 物料下拉接口已添加

## 前端
- [x] 免检清单管理页面
- [x] 创建/编辑弹窗
- [x] 物料下拉选择器
- [ ] 供应商下拉选择器 — 等待 feat-supplier
- [x] 有效期日期范围选择器
- [x] 过期规则视觉标识

## 测试
- [x] InspectionExemptionResource 集成测试通过（16/16）

## 代码质量
- [x] 四层架构分离
- [x] DTO 与实体分离
- [x] BusinessException 统一异常处理
- [x] 审计字段自动填充

## Verification Record
| Date | Status | Summary |
|------|--------|---------|
| 2026-04-22 | PASSED | 21/30 tasks completed. 16/16 integration tests passed. All 3 Gherkin scenarios validated. |
