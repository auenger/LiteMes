# Tasks: feat-inspection-exemption 免检清单

## 任务清单

### 1. 数据层
- [ ] 创建 `inspection_exemption` 表 DDL（参照 spec.md）— material_id FK→material_master, supplier_id 无DB级FK(逻辑引用), idx_valid_date 索引
- [ ] 定义 `InspectionExemption` 实体类（extends SoftDeleteEntity）— material_id/code/name, supplier_id/code/name(可空), valid_from/to, status
- [ ] 定义 `InspectionExemptionMapper`（MyBatis-Plus Mapper 接口）
- [ ] 定义 `InspectionExemptionRepository` 接口（domain 层）
- [ ] 实现 `InspectionExemptionRepositoryImpl`（infrastructure 层）— 包含有效期范围查询方法

### 2. 后端逻辑
- [ ] 定义 DTO：InspectionExemptionCreateDto, InspectionExemptionUpdateDto, InspectionExemptionDto, InspectionExemptionQueryDto
- [ ] 实现 `InspectionExemptionService` — 创建（物料必填，供应商/有效期可空，物料名称自动带出）— 场景1, 2
- [ ] 实现 `InspectionExemptionService` — 编辑（可修改物料、供应商、有效期）
- [ ] 实现 `InspectionExemptionService` — 删除（引用检查）
- [ ] 实现 `InspectionExemptionService` — 分页查询（物料/供应商下拉筛选，状态筛选）
- [ ] 实现物料名称自动带出逻辑（根据 material_id 从 MaterialMaster 查询填充 material_code/name）
- [ ] 实现供应商名称自动带出逻辑（根据 supplier_id 调用 feat-supplier 的 API 查询填充 supplier_code/name）
- [ ] 实现免检规则业务矩阵（4种组合：空+空=全局永久, 有+空=供应商永久, 有+有=指定有效期, 空+有=全局有效期）
- [ ] 实现有效期校验逻辑 — 查询时动态判断 valid_to 是否过期，过期规则标记为已失效 — 场景3
- [ ] （可选）Quarkus Scheduler 定时扫描过期规则并更新状态
- [ ] 实现 `InspectionExemptionResource`（JAX-RS）— /api/inspection-exemptions CRUD + 分页
- [ ] Excel 导入导出 — 唯一性校验导入
- [ ] 变更日志记录（AuditLog 集成）

### 3. 前端
- [ ] 免检清单管理页面（查询条件：物料/供应商/状态 + 数据表格）
- [ ] 免检清单创建/编辑弹窗（物料下拉取 material_master，供应商下拉取 supplier）
- [ ] 物料下拉选择器（关联物料信息表，支持模糊搜索）
- [ ] 供应商下拉选择器（关联供应商表，支持模糊搜索，允许为空）
- [ ] 有效期日期范围选择器（valid_from ~ valid_to，允许为空表示永久）
- [ ] 过期规则视觉标识（已过期的免检规则用灰色/删除线显示）

### 4. 测试
- [ ] InspectionExemptionService 单元测试 — 物料必填校验、自动带出逻辑、免检规则矩阵（4种组合）
- [ ] InspectionExemptionService 单元测试 — 有效期过期判断
- [ ] InspectionExemptionResource 集成测试（QuarkusTest + REST Assured）
- [ ] Excel 导入导出测试

### 5. 跨模块集成（feat-supplier 完成后）
- [ ] 确认 supplier 表已创建，执行 `ALTER TABLE inspection_exemption ADD CONSTRAINT fk_exemption_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id)` 添加 DB 级 FK 约束
- [ ] 集成测试：供应商下拉选择器与 feat-supplier API 联通
- [ ] 验证免检清单创建时供应商名称自动带出逻辑与 supplier 表数据一致

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-material-master 拆分 |
| 2026-04-21 | Spec enriched | 补充数据库模型设计、用户价值点、上下文分析 |
