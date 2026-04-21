# Tasks: feat-material-category 物料分类

## 任务清单

### 1. 数据层
- [x] 创建 `material_category` 表 DDL（参照 spec.md）— 包含 uk_category_code, uk_category_name, parent_id 自引用 FK
- [x] 定义 `MaterialCategory` 实体类（extends SoftDeleteEntity）— category_code, category_name, is_quality_category, parent_id, status
- [x] 定义 `MaterialCategoryMapper`（MyBatis-Plus Mapper 接口）
- [x] 定义 `MaterialCategoryRepository` 接口（domain 层）
- [x] 实现 `MaterialCategoryRepositoryImpl`（infrastructure 层）— 包含树形查询方法（findByParentId, findTree）

### 2. 后端逻辑
- [x] 定义 DTO：MaterialCategoryCreateDto, MaterialCategoryUpdateDto, MaterialCategoryDto, MaterialCategoryTreeDto, MaterialCategoryQueryDto
- [x] 实现 `MaterialCategoryService` — 创建（编码+名称唯一性校验）— 场景1
- [x] 实现 `MaterialCategoryService` — 编辑（编码不可修改，仅改名称和 is_quality_category）— 场景2
- [x] 实现 `MaterialCategoryService` — 删除（引用检查，被 MaterialMaster 引用时禁止）— 场景3
- [x] 实现 `MaterialCategoryService` — 分页查询（编码/名称模糊查询，状态筛选）
- [x] 实现 `MaterialCategoryService` — 树形结构查询（返回 TreeDto 列表，参照 feat-department 的 Department 树形模式）
- [x] 实现 `MaterialCategoryService` — 启用/禁用
- [x] 实现 `MaterialCategoryResource`（JAX-RS）— /api/material-categories CRUD + 分页 + 树形
- [ ] Excel 导入导出 — 唯一性校验，已存在编码跳过（延后到后续迭代）
- [x] 变更日志记录（AuditLog 集成）

### 3. 前端
- [x] 物料分类管理页面（左侧树形结构 + 右侧列表/详情）
- [x] 物料分类创建/编辑弹窗（编码创建时可编辑，编辑时只读）
- [x] 树形展示组件（可复用 Department 树形组件模式）
- [x] 物料分类下拉选择器组件（供 feat-material-info 复用，支持树形选择）

### 4. 测试
- [ ] MaterialCategoryService 单元测试 — 编码唯一性、名称唯一性、引用检查、树形查询
- [ ] MaterialCategoryResource 集成测试（QuarkusTest + REST Assured）
- [ ] 树形结构查询测试（多级分类、空树、循环引用防护）
- [ ] Excel 导入导出测试

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-material-master 拆分 |
| 2026-04-21 | Spec enriched | 补充数据库模型设计、用户价值点、上下文分析 |
| 2026-04-22 | 后端实现完成 | 数据层+服务层+REST API，编译通过 |
| 2026-04-22 | 前端实现完成 | 物料分类管理页面+树形组件+下拉选择器，类型检查通过 |
| 2026-04-22 | Excel导入导出延后 | 优先实现核心CRUD，Excel功能延后到后续迭代 |
