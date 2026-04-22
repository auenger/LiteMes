# Tasks: feat-material-info 物料基本信息

## 任务清单

### 1. 数据层
- [x] 创建 `material_master` 表 DDL（参照 spec.md）— 33 列，含 uk_material_code, uk_material_name, FK→material_category, FK→uom
- [x] 创建 `material_version` 表 DDL — 含 uk_material_version(material_id, version_no), FK→material_master
- [x] 定义 `MaterialMaster` 实体类（extends SoftDeleteEntity）— material_code, material_name, status, basic_category, category_id, attribute_category, uom_id, 14个PCB属性字段, ext1~ext5
- [x] 定义 `MaterialVersion` 实体类（extends SoftDeleteEntity）— material_id, version_no, status
- [x] 定义 BasicCategory 枚举 — 成品, 半成品, 原材料, 备件
- [x] 定义 AttributeCategory 枚举 — 采购件, 自制件, 采购&自制件
- [x] 定义 `MaterialMasterMapper`（MyBatis-Plus Mapper 接口）
- [x] 定义 `MaterialVersionMapper`（MyBatis-Plus Mapper 接口）
- [x] 定义 `MaterialMasterRepository` 接口（domain 层）
- [x] 实现 `MaterialMasterRepositoryImpl`（infrastructure 层）
- [x] 定义 `MaterialVersionRepository` 接口（domain 层）
- [x] 实现 `MaterialVersionRepositoryImpl`（infrastructure 层）

### 2. 后端逻辑
- [x] 定义 DTO：MaterialCreateDto, MaterialUpdateDto, MaterialDto, MaterialQueryDto
- [x] 定义 DTO：MaterialVersionCreateDto, MaterialVersionDto
- [x] 实现 `MaterialService` — 创建（编码+名称唯一性校验，基本分类/属性分类/单位必填）— 场景1
- [x] 实现 `MaterialService` — 编辑（编码不可修改，可改名称、分类、单位、PCB属性等）— 场景1
- [x] 实现 `MaterialService` — 删除（引用检查，被 InspectionExemption 引用时禁止）— 场景3
- [x] 实现 `MaterialService` — 分页查询（编码/名称模糊查询，物料分类/基本分类/状态筛选）
- [x] 实现 `MaterialService` — 启用/禁用
- [x] 实现 `MaterialVersionService` — 创建版本（版本号必填，material_id+version_no 唯一性校验）— 场景2
- [x] 实现 `MaterialVersionService` — 查询版本列表（按 material_id 查询）
- [x] 实现 `MaterialResource`（JAX-RS）— /api/materials CRUD + 分页
- [x] 实现 `MaterialVersionResource`（JAX-RS）— /api/materials/{id}/versions 创建+查询
- [ ] Excel 导入导出 — 唯一性校验，已存在编码跳过
- [ ] 变更日志记录（AuditLog 集成）

### 3. 前端
- [x] 物料信息管理页面（查询条件：编码/名称/物料分类/基本分类/状态 + 数据表格）
- [x] 物料信息创建/编辑弹窗（编码创建时可编辑，编辑时只读；基本分类/属性分类下拉枚举）
- [x] 物料分类下拉选择（调用 feat-material-category 的下拉 API，支持树形选择）
- [x] 单位下拉选择（调用 feat-uom 的下拉 API）
- [x] 物料版本管理页签（嵌入物料详情页，显示版本列表 + 创建弹窗）
- [x] PCB 属性字段区域（按基本分类动态显示相关属性）

### 4. 测试
- [ ] MaterialService 单元测试 — 编码唯一性、名称唯一性、引用检查、枚举校验
- [ ] MaterialVersionService 单元测试 — 版本号唯一性、关联物料校验
- [ ] MaterialResource 集成测试（QuarkusTest + REST Assured）
- [ ] MaterialVersionResource 集成测试
- [ ] Excel 导入导出测试

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-material-master 拆分 |
| 2026-04-21 | Spec enriched | 补充数据库模型设计、用户价值点、上下文分析 |
| 2026-04-22 | 开发完成 | 数据层+后端逻辑+前端页面完成，Excel导入导出和变更日志待后续迭代 |
