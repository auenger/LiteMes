# Tasks: feat-material-info 物料基本信息

## 任务清单

### 后端
- [ ] MaterialMaster 实体定义（编码、名称、分类、单位、PCB属性、扩展字段）
- [ ] MaterialVersion 实体定义（物料ID、版本号）
- [ ] MaterialMaster CRUD API
- [ ] MaterialMaster 编码唯一性校验
- [ ] MaterialMaster 名称唯一性校验
- [ ] MaterialMaster 引用检查（被引用时禁止删除）
- [ ] MaterialVersion CRUD API（创建/查询）
- [ ] 物料编码不可修改约束
- [ ] 基本分类枚举（成品、半成品、原材料、备件）
- [ ] 属性分类枚举（采购件、自制件、采购&自制件）
- [ ] Excel 导入导出
- [ ] 变更日志记录

### 前端
- [ ] 物料信息管理页面（查询条件 + 数据表格）
- [ ] 物料信息创建/编辑弹窗
- [ ] 物料版本管理页签及创建弹窗
- [ ] 物料分类下拉选择（关联物料分类表）
- [ ] 单位下拉选择（关联单位表）

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-material-master 拆分 |
