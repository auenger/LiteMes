# Checklist: feat-uom 计量单位与换算

## 数据层
- [x] `uom` 表 DDL 已创建，唯一索引 (uom_code, uom_name) 就位
- [x] `uom_conversion` 表 DDL 已创建，联合唯一索引 (from_uom_id, to_uom_id) 就位
- [x] Uom / UomConversion 实体类继承 SoftDeleteEntity
- [x] MyBatis Mapper + Repository 接口 + 实现已创建

## 后端逻辑
- [x] Uom CRUD API 已实现（创建/编辑/删除/查询/启用禁用）
- [x] UomConversion CRUD API 已实现
- [x] 单位编码唯一性校验 — 场景1
- [x] 单位编码不可修改约束 — 场景2
- [x] 换算比例原单位+目标单位唯一性校验 — 场景3
- [x] 引用检查（被 UomConversion/MaterialMaster 引用时禁止删除）— 场景4
- [ ] Excel 导入导出
- [ ] 变更日志记录

## 前端
- [x] 单位管理页面（查询条件 + 数据表格）
- [x] 单位创建/编辑弹窗（编码编辑时只读）
- [x] 换算比例管理页面 + 创建/编辑弹窗
- [x] 单位下拉选择器组件（供 feat-material-info 复用）

## 测试
- [x] UomService 单元测试通过（covered by integration tests）
- [x] UomConversionService 单元测试通过（covered by integration tests）
- [x] UomResource 集成测试通过（13/13）
- [x] UomConversionResource 集成测试通过（9/9）
- [ ] Excel 导入导出测试通过

## 代码质量
- [x] 四层架构分离（web → application → domain → infrastructure）
- [x] DTO 与实体分离，API 层不暴露实体
- [x] BusinessException 统一异常处理
- [x] 审计字段（created_by/at, updated_by/at）自动填充

---

## Verification Record
| Date | Status | Summary |
|------|--------|---------|
| 2026-04-22 | PASSED | 22/22 tests passing, all Gherkin scenarios satisfied, 2 items deferred (Excel import/export, AuditLog integration) |

### Evidence
- Verification report: `features/active-feat-uom/evidence/verification-report.md`
