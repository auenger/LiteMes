# Tasks: feat-uom 计量单位与换算

## 任务清单

### 1. 数据层
- [ ] 创建 `uom` 表 DDL（参照 spec.md 数据库模型设计）— 包含 uk_uom_code, uk_uom_name 唯一索引
- [ ] 创建 `uom_conversion` 表 DDL — 包含 uk_from_to 联合唯一索引, FK→uom
- [ ] 定义 `Uom` 实体类（extends SoftDeleteEntity）— uom_code, uom_name, status, precision
- [ ] 定义 `UomConversion` 实体类（extends SoftDeleteEntity）— from_uom_id/code/name, to_uom_id/code/name, conversion_rate
- [ ] 定义 `UomMapper`（MyBatis-Plus Mapper 接口）
- [ ] 定义 `UomConversionMapper`（MyBatis-Plus Mapper 接口）
- [ ] 定义 `UomRepository` 接口（domain 层）
- [ ] 实现 `UomRepositoryImpl`（infrastructure 层，extends UomMapper）
- [ ] 定义 `UomConversionRepository` 接口（domain 层）
- [ ] 实现 `UomConversionRepositoryImpl`（infrastructure 层）

### 2. 后端逻辑
- [ ] 定义 DTO：UomCreateDto, UomUpdateDto, UomDto, UomQueryDto
- [ ] 定义 DTO：UomConversionCreateDto, UomConversionUpdateDto, UomConversionDto
- [ ] 实现 `UomService` — 创建（编码+名称唯一性校验）— 场景1
- [ ] 实现 `UomService` — 编辑（编码不可修改，仅改名称和精度）— 场景2
- [ ] 实现 `UomService` — 删除（引用检查，被 UomConversion/MaterialMaster 引用时禁止）— 场景4
- [ ] 实现 `UomService` — 分页查询（编码/名称模糊查询，状态筛选）
- [ ] 实现 `UomService` — 启用/禁用
- [ ] 实现 `UomConversionService` — 创建（原单位+目标单位唯一性校验）— 场景3
- [ ] 实现 `UomConversionService` — 编辑（修改原单位、目标单位、换算率）
- [ ] 实现 `UomConversionService` — 删除（引用检查）
- [ ] 实现 `UomConversionService` — 分页查询（原单位/目标单位模糊查询）
- [ ] 实现 `UomResource`（JAX-RS）— /api/uoms CRUD + 分页
- [ ] 实现 `UomConversionResource`（JAX-RS）— /api/uom-conversions CRUD + 分页
- [ ] Excel 导入导出（Uom + UomConversion）— 唯一性校验，已存在编码跳过
- [ ] 变更日志记录（AuditLog 集成）

### 3. 前端
- [ ] 单位管理页面（查询条件：编码/名称/状态 + 数据表格）
- [ ] 单位创建/编辑弹窗（编码创建时可编辑，编辑时只读）
- [ ] 单位换算比例管理页面（查询条件：原单位/目标单位/状态 + 数据表格）
- [ ] 单位换算比例创建/编辑弹窗（原单位/目标单位下拉取 uom 表数据）
- [ ] 单位下拉选择器组件（供 feat-material-info 复用）

### 4. 测试
- [ ] UomService 单元测试 — 编码唯一性、名称唯一性、引用检查
- [ ] UomConversionService 单元测试 — 联合唯一性、引用检查
- [ ] UomResource 集成测试（QuarkusTest + REST Assured）
- [ ] UomConversionResource 集成测试
- [ ] Excel 导入导出测试

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-material-master 拆分 |
| 2026-04-21 | Spec enriched | 补充数据库模型设计、用户价值点、上下文分析 |
