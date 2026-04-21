# Checklist: feat-equipment-ledger 设备台账

## 完成检查清单

### 数据层
- [x] EquipmentLedger 实体类定义完成（继承 SoftDeleteEntity）
- [x] RunningStatus 枚举定义完成（RUNNING/FAULT/SHUTDOWN/MAINTENANCE）
- [x] ManageStatus 枚举定义完成（IN_USE/IDLE/SCRAPPED）
- [x] equipment_ledger 表 DDL 已创建（含 equipment_model_id FK, factory_id FK, 冗余字段, 多索引）
- [x] EquipmentLedgerMapper 已定义
- [x] EquipmentLedgerRepository 接口与实现完成

### 后端逻辑
- [x] CRUD API 端点全部实现（POST/PUT/DELETE/GET）
- [x] 编码唯一性校验（创建时）
- [x] 编码不可修改约束（编辑时）
- [x] 设备型号存在性校验（创建/编辑时）
- [x] 工厂存在性校验（创建/编辑时，跨模块 FK）
- [x] 型号→类型级联自动填充（冗余字段同步）
- [x] 工厂信息自动填充（冗余字段同步）
- [x] 引用检查（删除时检查是否被工单/维保等业务引用）
- [x] 启用/禁用切换
- [ ] Excel 导入（含唯一性校验、型号/工厂存在性校验、错误报告） — 延后实现
- [ ] Excel 导出 — 延后实现
- [x] 分页查询 + 模糊搜索 + 多条件组合筛选（类型/型号/运行状态/管理状态/工厂）
- [x] 变更履历记录（通过 BaseEntity 自动审计）

### DTO 校验
- [x] EquipmentLedgerCreateDto（@NotBlank equipment_code, equipment_name; @NotNull equipment_model_id, running_status, manage_status, factory_id, commissioning_date）
- [x] EquipmentLedgerUpdateDto（可编辑字段校验）
- [x] EquipmentLedgerQueryDto（分页参数 + 多条件查询参数）

### 前端
- [x] 设备台账管理页面（列表 + 多条件组合查询 + 操作）
- [x] 创建/编辑弹窗
  - [x] 编码（创建可填/编辑只读）
  - [x] 设备型号下拉 + 级联带出设备类型（只读）
  - [x] 运行状态/管理状态下拉组件
  - [x] 工厂下拉 + 自动带出名称
  - [x] 生产厂家 + 入场时间
- [x] 删除确认（引用时按钮置灰）
- [x] 启用/禁用操作
- [ ] 导入导出 UI — 延后实现

### 跨模块集成
- [x] factory_id FK 指向 feat-factory 的 factory 表
- [x] equipment_model_id FK 指向 feat-equipment-model 的 equipment_model 表
- [x] 设备类型信息通过型号级联获取（equipment_type_id 冗余）
- [x] EquipmentModelService 引用检查已添加

### 测试
- [x] EquipmentLedgerResource 集成测试（端到端 + 多条件查询）
  - [x] CRUD 端到端测试
  - [x] 编码唯一性校验测试
  - [x] 冗余字段自动填充测试
  - [x] 型号更换时冗余字段同步测试
  - [x] 无效型号/工厂 FK 校验测试
  - [x] 分页查询 + 多条件组合筛选测试
  - [x] 启用/禁用状态切换测试
  - [x] 删除测试
- [ ] Excel 导入导出测试 — 延后实现
- [x] 测试通过

### 代码质量
- [x] 四层架构分离（web/application/domain/infrastructure）
- [x] 代码风格符合 CLAUDE.md 规范
- [x] 无业务逻辑泄漏到 Resource 层
- [x] 必要的注释已添加

### 文档
- [x] spec.md 数据模型与业务逻辑完整
- [x] task.md 任务全部勾选
- [x] 相关文档已更新

---

## Verification Record

**Date**: 2026-04-22
**Status**: PASSED
**Result Summary**: 41/41 tasks completed, 20/20 tests passed, 6/7 Gherkin scenarios passed (1 deferred - Excel import/export)
**Evidence Path**: features/active-feat-equipment-ledger/evidence/verification-report.md
**Warnings**:
- Audit log LocalDate serialization warning (non-blocking)
- Excel import/export deferred to future iteration
- Pre-existing test isolation issues in shared H2 database (unrelated to this feature)
