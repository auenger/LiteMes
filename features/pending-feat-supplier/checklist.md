# Checklist: feat-supplier 供应商管理

## 完成检查清单

### 数据层
- [ ] `supplier` 表 DDL 已创建并验证
- [ ] `supplier_material` 表 DDL 已创建并验证
- [ ] Supplier / SupplierMaterial 实体类继承 SoftDeleteEntity
- [ ] Mapper / Repository 接口与实现完整

### 后端逻辑
- [ ] CRUD API 完整（创建、编辑、删除、查询、启用/禁用）
- [ ] 编码唯一性校验（创建时）
- [ ] 编码不可修改约束（编辑时）
- [ ] 被引用数据删除保护（免检清单等引用检查）
- [ ] 物料关联/取消关联 API（支持多选、去重校验）
- [ ] Excel 导入导出
- [ ] 变更日志记录
- [ ] DTO 定义完整（Create/Update/Query/Response）

### API 验证
- [ ] POST /api/suppliers — 创建供应商（场景 1, 2）
- [ ] PUT /api/suppliers/{id} — 编辑供应商（场景 3）
- [ ] DELETE /api/suppliers/{id} — 删除供应商（场景 4）
- [ ] GET /api/suppliers — 分页查询（场景 8）
- [ ] PATCH /api/suppliers/{id}/status — 启用/禁用（场景 9）
- [ ] POST /api/suppliers/{id}/materials — 关联物料（场景 5, 6）
- [ ] DELETE /api/suppliers/{id}/materials/{materialId} — 取消关联
- [ ] POST /api/suppliers/import — 导入（场景 7）
- [ ] GET /api/suppliers/export — 导出

### 前端
- [ ] 供应商列表页面（查询 + 表格 + 分页）
- [ ] 创建/编辑弹窗
- [ ] 选择物料弹窗（多选）
- [ ] 导入/导出按钮
- [ ] 变更履历查看

### 跨模块集成
- [ ] inspection_exemption.supplier_id 可正确引用 supplier 表
- [ ] （可选）inspection_exemption 表添加 FK 约束
- [ ] 免检清单供应商下拉与 Supplier API 联通

### 测试
- [ ] SupplierService 单元测试通过
- [ ] SupplierResource 集成测试通过
- [ ] Excel 导入导出测试通过

### 代码质量
- [ ] 四层架构分离：Resource → Service → Repository → Mapper
- [ ] 关键编码不可变（supplierCode 创建后不可修改）
- [ ] 软删除（@TableLogic）
- [ ] 审计日志（BaseEntity 自动填充）
- [ ] 禁止跳过 DTO

### 提交准备
- [ ] 变更已暂存
- [ ] 准备好提交信息
