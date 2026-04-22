# Checklist: feat-equipment-model 设备型号

## 完成检查清单

### 数据层
- [x] EquipmentModel 实体类定义完成（继承 SoftDeleteEntity）
- [x] equipment_model 表 DDL 已创建（含 equipment_type_id FK + 冗余字段）
- [x] EquipmentModelMapper 已定义
- [x] EquipmentModelRepository 接口与实现完成

### 后端逻辑
- [x] CRUD API 端点全部实现（POST/PUT/DELETE/GET）
- [x] 编码唯一性校验（创建时）
- [x] 编码不可修改约束（编辑时）
- [x] 设备类型存在性校验（创建/编辑时）
- [x] 冗余字段自动填充（type_code/type_name 随类型联动）
- [x] 引用检查（删除时检查是否被设备台账引用 — 预留，待 feat-equipment-ledger 实现）
- [x] 启用/禁用切换
- [ ] Excel 导入（含唯一性校验、错误报告）— 延后
- [ ] Excel 导出 — 延后
- [x] 分页查询 + 模糊搜索 + 类型筛选
- [x] 变更履历记录（通过 BaseEntity 自动审计）

### DTO 校验
- [x] EquipmentModelCreateDto（@NotBlank model_code, model_name; @NotNull equipment_type_id）
- [x] EquipmentModelUpdateDto（@NotBlank model_name; @NotNull equipment_type_id）
- [x] EquipmentModelQueryDto（分页参数 + 模糊查询 + 类型筛选）

### 前端
- [x] 设备型号管理页面（列表 + 查询 + 操作）
- [x] 创建/编辑弹窗（含设备类型下拉选择）
- [x] 设备类型下拉联动（选择后自动带出类型名称）
- [x] 删除确认（引用时按钮置灰）
- [x] 启用/禁用操作
- [ ] 导入导出 UI — 延后

### 测试
- [x] EquipmentModelResource 集成测试（端到端 + 校验逻辑）
- [ ] Excel 导入导出测试 — 延后
- [x] 测试通过（20/20）

### 代码质量
- [x] 四层架构分离（web/application/domain/infrastructure）
- [x] 代码风格符合 CLAUDE.md 规范
- [x] 无业务逻辑泄漏到 Resource 层
- [x] 必要的注释已添加

### 文档
- [x] spec.md 数据模型与业务逻辑完整
- [x] task.md 任务全部勾选（除延后项）
- [x] 相关文档已更新

---

## Verification Record
- **Timestamp**: 2026-04-22
- **Status**: PASSED (with warnings)
- **Test Results**: 20/20 passed, 0 failures
- **Evidence**: features/active-feat-equipment-model/evidence/verification-report.md
- **Warnings**:
  - Excel import/export deferred (4 tasks)
  - Delete constraint full enforcement requires feat-equipment-ledger
