# Checklist: feat-equipment-type 设备类型

## 完成检查清单

### 数据层
- [x] EquipmentType 实体类定义完成（继承 SoftDeleteEntity）
- [x] equipment_type 表 DDL 已创建
- [x] EquipmentTypeMapper 已定义
- [x] EquipmentTypeRepository 接口与实现完成

### 后端逻辑
- [x] CRUD API 端点全部实现（POST/PUT/DELETE/GET）
- [x] 编码唯一性校验（创建时）
- [x] 编码不可修改约束（编辑时）
- [x] 引用检查（删除时检查是否被设备型号引用）— 占位，待 feat-equipment-model
- [x] 启用/禁用切换
- [ ] Excel 导入（含唯一性校验、错误报告）— 后续迭代
- [ ] Excel 导出 — 后续迭代
- [x] 分页查询 + 模糊搜索
- [x] 变更履历记录（通过 BaseEntity 自动审计）

### DTO 校验
- [x] EquipmentTypeCreateDto（@NotBlank type_code, type_name）
- [x] EquipmentTypeUpdateDto（@NotBlank type_name）
- [x] EquipmentTypeQueryDto（分页参数 + 模糊查询参数）

### 前端
- [x] 设备类型管理页面（列表 + 查询 + 操作）
- [x] 创建/编辑弹窗
- [x] 删除确认（引用时按钮置灰）
- [x] 启用/禁用操作
- [ ] 导入导出 UI — 后续迭代

### 测试
- [x] EquipmentTypeResource 集成测试（端到端 + 校验）
- [ ] Excel 导入导出测试 — 后续迭代
- [x] 测试通过（14/14）

### 代码质量
- [x] 四层架构分离（web/application/domain/infrastructure）
- [x] 代码风格符合 CLAUDE.md 规范
- [x] 无业务逻辑泄漏到 Resource 层
- [x] 必要的注释已添加

### 文档
- [x] spec.md 数据模型与业务逻辑完整
- [x] task.md 任务全部勾选（核心任务）
- [x] 相关文档已更新

### 下拉数据源
- [x] DropdownService 增加 getEquipmentTypeDropdown()
- [x] DropdownResource 增加 GET /api/dropdown/equipment-types
- [x] 前端 dropdown.ts 增加 getEquipmentTypeDropdown()

---

## Verification Record

| 日期 | 状态 | 结果摘要 | 证据路径 |
|------|------|---------|---------|
| 2026-04-22 | PASSED (warnings) | 14/14 测试通过, 5 个 Gherkin 场景中 3 PASS + 1 PARTIAL + 1 DEFERRED | features/active-feat-equipment-type/evidence/verification-report.md |
