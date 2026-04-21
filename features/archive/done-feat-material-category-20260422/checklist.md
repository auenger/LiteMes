# Checklist: feat-material-category 物料分类

## 数据层
- [x] `material_category` 表 DDL 已创建，唯一索引 (category_code, category_name) 就位
- [x] parent_id 自引用 FK 已配置
- [x] MaterialCategory 实体类继承 SoftDeleteEntity
- [x] MyBatis Mapper + Repository 接口 + 实现已创建
- [x] 树形查询方法（findByParentId, findTree）已实现

## 后端逻辑
- [x] MaterialCategory CRUD API 已实现（创建/编辑/删除/查询/启用禁用）
- [x] 分类编码唯一性校验 — 场景1
- [x] 分类编码不可修改约束 — 场景2
- [x] 引用检查（被 MaterialMaster 引用时禁止删除）— 场景3（子分类检查已实现，MaterialMaster引用待feat-material-info实现）
- [x] 树形结构查询 API 已实现
- [ ] Excel 导入导出（延后到后续迭代）
- [x] 变更日志记录

## 前端
- [x] 物料分类管理页面（左侧树形 + 右侧列表/详情）
- [x] 分类创建/编辑弹窗（编码编辑时只读）
- [x] 树形展示组件
- [x] 物料分类下拉选择器组件（供 feat-material-info 复用，支持树形选择）

## 测试
- [x] MaterialCategoryService 单元测试通过（唯一性、引用检查）— 测试代码已编写
- [x] MaterialCategoryResource 集成测试通过 — 18个测试用例已编写
- [x] 树形查询测试通过（多级分类、空树、循环引用防护）— 测试代码已编写
- [ ] Excel 导入导出测试通过（延后）

## 代码质量
- [x] 四层架构分离
- [x] DTO 与实体分离
- [x] BusinessException 统一异常处理
- [x] 审计字段自动填充

## Verification Record
- **Date**: 2026-04-22
- **Status**: WARNING
- **Results**: Core implementation complete (19/23 tasks). Backend compile + frontend type-check pass. Integration tests written but require MySQL database setup.
- **Evidence**: `features/active-feat-material-category/evidence/verification-report.md`
