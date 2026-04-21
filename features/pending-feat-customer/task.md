# Tasks: feat-customer 客户管理

## 任务清单

### 1. 数据层
- [ ] 创建 `customer` 表 DDL（参照 spec.md）— customer_code UNIQUE, idx_status, idx_type
- [ ] 创建 `customer_material` 表 DDL — (customer_id, material_id) 联合唯一, FK → customer, FK → material_master
- [ ] 定义 `Customer` 实体类（extends SoftDeleteEntity）— customerCode, customerName, status, type, shortName, contactPerson, phone, address, email
- [ ] 定义 `CustomerMaterial` 实体类（extends SoftDeleteEntity）— customerId, materialId
- [ ] 定义 `CustomerMapper`（MyBatis-Plus Mapper 接口）
- [ ] 定义 `CustomerMaterialMapper`（MyBatis-Plus Mapper 接口）
- [ ] 定义 `CustomerRepository` 接口（domain 层）— 包含编码唯一性检查、分页查询
- [ ] 实现 `CustomerRepositoryImpl`（infrastructure 层）

### 2. 后端逻辑
- [ ] 定义 DTO：CustomerCreateDto, CustomerUpdateDto, CustomerDto, CustomerQueryDto
- [ ] 定义 DTO：CustomerMaterialDto（物料关联请求/响应）
- [ ] 实现 `CustomerService` — 创建（编码+名称必填，编码唯一性校验）— 场景 1, 2
- [ ] 实现 `CustomerService` — 编辑（编码不可修改）— 场景 3
- [ ] 实现 `CustomerService` — 删除（引用检查，被引用时拒绝）— 场景 4
- [ ] 实现 `CustomerService` — 分页查询（编码/名称模糊，类型/状态下拉筛选）— 场景 7
- [ ] 实现 `CustomerService` — 启用/禁用 — 场景 9
- [ ] 实现 `CustomerService` — 物料关联（多选，去重校验）— 场景 5, 6
- [ ] 实现 `CustomerService` — 物料取消关联
- [ ] 实现 `CustomerResource`（JAX-RS）— /api/customers CRUD + 分页 + 物料关联
- [ ] Excel 导入（含编码唯一性校验）— 场景 8
- [ ] Excel 导出
- [ ] 变更日志记录（AuditLog 集成）

### 3. 前端
- [ ] 客户列表页面（查询条件：编码/名称模糊 + 类型下拉 + 状态下拉 + 数据表格）
- [ ] 创建/编辑弹窗（编码创建时可填、编辑时只读）
- [ ] 选择物料弹窗（支持多选，加载物料信息表数据）
- [ ] 导入/导出按钮
- [ ] 变更履历查看

### 4. 测试
- [ ] CustomerService 单元测试 — 编码唯一性校验、创建/编辑/删除逻辑
- [ ] CustomerService 单元测试 — 物料关联去重校验
- [ ] CustomerResource 集成测试（QuarkusTest + REST Assured）
- [ ] Excel 导入导出测试

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-supply-chain 拆分 |
| 2026-04-21 | Spec enriched | 补充用户价值点、上下文分析、数据库模型设计、设计决策、业务逻辑、枚举定义；细化任务清单 |
