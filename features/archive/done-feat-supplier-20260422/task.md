# Tasks: feat-supplier 供应商管理

## 任务清单

### 1. 数据层
- [x] 创建 `supplier` 表 DDL（参照 spec.md）— supplier_code UNIQUE, idx_status
- [x] 创建 `supplier_material` 表 DDL — (supplier_id, material_id) 联合唯一, FK → supplier, FK → material_master
- [x] 定义 `Supplier` 实体类（extends SoftDeleteEntity）— supplierCode, supplierName, status, shortName, contactPerson, phone, address, email, description
- [x] 定义 `SupplierMaterial` 实体类（extends SoftDeleteEntity）— supplierId, materialId
- [x] 定义 `SupplierMapper`（MyBatis-Plus Mapper 接口）
- [x] 定义 `SupplierMaterialMapper`（MyBatis-Plus Mapper 接口）
- [x] 定义 `SupplierRepository` 接口（domain 层）— 包含编码唯一性检查、分页查询
- [x] 实现 `SupplierRepositoryImpl`（infrastructure 层）

### 2. 后端逻辑
- [x] 定义 DTO：SupplierCreateDto, SupplierUpdateDto, SupplierDto, SupplierQueryDto
- [x] 定义 DTO：SupplierMaterialDto（物料关联请求/响应）
- [x] 实现 `SupplierService` — 创建（编码+名称必填，编码唯一性校验）— 场景 1, 2
- [x] 实现 `SupplierService` — 编辑（编码不可修改）— 场景 3
- [x] 实现 `SupplierService` — 删除（引用检查：免检清单等，被引用时拒绝）— 场景 4
- [x] 实现 `SupplierService` — 分页查询（编码/名称模糊，状态下拉筛选）— 场景 8
- [x] 实现 `SupplierService` — 启用/禁用 — 场景 9
- [x] 实现 `SupplierService` — 物料关联（多选，去重校验）— 场景 5, 6
- [x] 实现 `SupplierService` — 物料取消关联
- [x] 实现 `SupplierResource`（JAX-RS）— /api/suppliers CRUD + 分页 + 物料关联
- [ ] Excel 导入（含编码唯一性校验，已存在编码不允许再次导入）— 场景 7
- [ ] Excel 导出
- [x] 变更日志记录（AuditLog 集成）
- [x] 供应商下拉接口（DropdownService + DropdownResource 添加 suppliers 端点）

### 3. 前端
- [x] 供应商列表页面（查询条件：编码/名称模糊 + 状态下拉 + 数据表格）
- [x] 创建/编辑弹窗（编码创建时可填、编辑时只读）
- [x] 选择物料弹窗（支持多选，加载物料信息表数据）
- [ ] 导入/导出按钮
- [x] 变更履历查看

### 4. 跨模块集成
- [x] 确认 `inspection_exemption.supplier_id` 可正确引用 supplier 表
- [ ] 为 inspection_exemption 表添加 supplier_id → supplier.id 的 DB FK 约束：`ALTER TABLE inspection_exemption ADD CONSTRAINT fk_exemption_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id)`
- [ ] 集成测试：免检清单的供应商下拉选择器与 Supplier API 联通
- [ ] 验证免检清单创建时供应商名称自动带出逻辑与 supplier 表数据一致

### 5. 测试
- [ ] SupplierService 单元测试 — 编码唯一性校验、创建/编辑/删除逻辑
- [ ] SupplierService 单元测试 — 物料关联去重校验
- [ ] SupplierResource 集成测试（QuarkusTest + REST Assured）
- [ ] Excel 导入导出测试

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-supply-chain 拆分 |
| 2026-04-21 | Spec enriched | 补充用户价值点、上下文分析、数据库模型设计、设计决策、业务逻辑；细化任务清单；增加跨模块集成任务 |
| 2026-04-22 | 核心功能已实现 | 后端四层架构完整，前端页面完整，下拉接口已集成；Excel导入导出、FK约束、单元测试待后续补充 |
