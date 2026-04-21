# Checklist: feat-uom 计量单位与换算

## 数据层
- [ ] `uom` 表 DDL 已创建，唯一索引 (uom_code, uom_name) 就位
- [ ] `uom_conversion` 表 DDL 已创建，联合唯一索引 (from_uom_id, to_uom_id) 就位
- [ ] Uom / UomConversion 实体类继承 SoftDeleteEntity
- [ ] MyBatis Mapper + Repository 接口 + 实现已创建

## 后端逻辑
- [ ] Uom CRUD API 已实现（创建/编辑/删除/查询/启用禁用）
- [ ] UomConversion CRUD API 已实现
- [ ] 单位编码唯一性校验 — 场景1
- [ ] 单位编码不可修改约束 — 场景2
- [ ] 换算比例原单位+目标单位唯一性校验 — 场景3
- [ ] 引用检查（被 UomConversion/MaterialMaster 引用时禁止删除）— 场景4
- [ ] Excel 导入导出
- [ ] 变更日志记录

## 前端
- [ ] 单位管理页面（查询条件 + 数据表格）
- [ ] 单位创建/编辑弹窗（编码编辑时只读）
- [ ] 换算比例管理页面 + 创建/编辑弹窗
- [ ] 单位下拉选择器组件（供 feat-material-info 复用）

## 测试
- [ ] UomService 单元测试通过
- [ ] UomConversionService 单元测试通过
- [ ] UomResource 集成测试通过
- [ ] UomConversionResource 集成测试通过
- [ ] Excel 导入导出测试通过

## 代码质量
- [ ] 四层架构分离（web → application → domain → infrastructure）
- [ ] DTO 与实体分离，API 层不暴露实体
- [ ] BusinessException 统一异常处理
- [ ] 审计字段（created_by/at, updated_by/at）自动填充
