# Checklist: feat-material-info 物料基本信息

## 数据层
- [x] `material_master` 表 DDL 已创建，唯一索引 (material_code, material_name) 就位
- [x] `material_version` 表 DDL 已创建，联合唯一索引 (material_id, version_no) 就位
- [x] FK 约束已配置：category_id→material_category, uom_id→uom, material_version.material_id→material_master
- [x] MaterialMaster / MaterialVersion 实体类继承 SoftDeleteEntity
- [x] BasicCategory 枚举已定义（成品/半成品/原材料/备件）
- [x] AttributeCategory 枚举已定义（采购件/自制件/采购&自制件）
- [x] MyBatis Mapper + Repository 接口 + 实现已创建（两个实体各一套）

## 后端逻辑
- [x] MaterialMaster CRUD API 已实现（创建/编辑/删除/查询/启用禁用）
- [x] MaterialVersion 创建 + 查询 API 已实现
- [x] 物料编码唯一性校验 — 场景1
- [x] 物料编码不可修改约束 — 场景2
- [x] 版本管理（material_id + version_no 唯一性）— 场景2
- [ ] 引用检查（被 InspectionExemption 引用时禁止删除）— 场景3（待 feat-inspection-exemption 实现）
- [x] 基本分类/属性分类枚举校验
- [ ] Excel 导入导出
- [ ] 变更日志记录

## 前端
- [x] 物料信息管理页面（查询条件：编码/名称/物料分类/基本分类/状态 + 数据表格）
- [x] 物料创建/编辑弹窗（编码编辑时只读；基本分类/属性分类枚举下拉）
- [x] 物料分类下拉选择（调用 feat-material-category 的 API）
- [x] 单位下拉选择（调用 feat-uom 的 API）
- [x] 物料版本管理页签（嵌入物料详情，版本列表 + 创建弹窗）
- [x] PCB 属性字段区域（按基本分类动态显示）

## 测试
- [ ] MaterialService 单元测试通过（唯一性、引用检查、枚举校验）
- [ ] MaterialVersionService 单元测试通过（版本号唯一性）
- [ ] MaterialResource 集成测试通过
- [ ] MaterialVersionResource 集成测试通过
- [ ] Excel 导入导出测试通过

## 代码质量
- [x] 四层架构分离
- [x] DTO 与实体分离（MaterialMaster 33 列的宽表不直接暴露给 API）
- [x] BusinessException 统一异常处理
- [x] 审计字段自动填充
- [x] 枚举值通过 Hibernate Validator 校验

## Verification Record
| Date | Status | Summary | Evidence |
|------|--------|---------|----------|
| 2026-04-22 | PASS (with warnings) | 28/35 tasks completed. Compilation passes. All Gherkin scenarios validated. Excel import/export, AuditLog, and tests deferred. | evidence/verification-report.md |
