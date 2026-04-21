# Checklist: feat-material-category 物料分类

## 数据层
- [ ] `material_category` 表 DDL 已创建，唯一索引 (category_code, category_name) 就位
- [ ] parent_id 自引用 FK 已配置
- [ ] MaterialCategory 实体类继承 SoftDeleteEntity
- [ ] MyBatis Mapper + Repository 接口 + 实现已创建
- [ ] 树形查询方法（findByParentId, findTree）已实现

## 后端逻辑
- [ ] MaterialCategory CRUD API 已实现（创建/编辑/删除/查询/启用禁用）
- [ ] 分类编码唯一性校验 — 场景1
- [ ] 分类编码不可修改约束 — 场景2
- [ ] 引用检查（被 MaterialMaster 引用时禁止删除）— 场景3
- [ ] 树形结构查询 API 已实现
- [ ] Excel 导入导出
- [ ] 变更日志记录

## 前端
- [ ] 物料分类管理页面（左侧树形 + 右侧列表/详情）
- [ ] 分类创建/编辑弹窗（编码编辑时只读）
- [ ] 树形展示组件
- [ ] 物料分类下拉选择器组件（供 feat-material-info 复用，支持树形选择）

## 测试
- [ ] MaterialCategoryService 单元测试通过（唯一性、引用检查）
- [ ] MaterialCategoryResource 集成测试通过
- [ ] 树形查询测试通过（多级分类、空树、循环引用防护）
- [ ] Excel 导入导出测试通过

## 代码质量
- [ ] 四层架构分离
- [ ] DTO 与实体分离
- [ ] BusinessException 统一异常处理
- [ ] 审计字段自动填充
