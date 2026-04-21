# Checklist: feat-inspection-exemption 免检清单

## 数据层
- [ ] `inspection_exemption` 表 DDL 已创建
- [ ] material_id FK→material_master 已配置
- [ ] supplier_id 无 DB 级 FK（逻辑引用，跨模块处理）
- [ ] 有效期索引 idx_valid_date (valid_from, valid_to) 已创建
- [ ] InspectionExemption 实体类继承 SoftDeleteEntity
- [ ] MyBatis Mapper + Repository 接口 + 实现已创建

## 后端逻辑
- [ ] InspectionExemption CRUD API 已实现（创建/编辑/删除/查询/启用禁用）
- [ ] 物料必填校验 + 物料名称自动带出 — 场景1, 2
- [ ] 供应商名称自动带出（逻辑引用 feat-supplier API）
- [ ] 免检规则业务矩阵（4种组合正确处理）
- [ ] 有效期过期判断逻辑（查询时动态判断或定时扫描）— 场景3
- [ ] 引用检查（被其他业务单据引用时禁止删除）
- [ ] Excel 导入导出
- [ ] 变更日志记录

## 前端
- [ ] 免检清单管理页面（查询条件：物料/供应商/状态 + 数据表格）
- [ ] 创建/编辑弹窗（物料下拉必填，供应商/有效期可空）
- [ ] 物料下拉选择器（关联 material_master，支持模糊搜索）
- [ ] 供应商下拉选择器（关联 supplier 表，允许为空）
- [ ] 有效期日期范围选择器（允许为空 = 永久免检）
- [ ] 过期规则视觉标识（灰色/删除线）

## 测试
- [ ] InspectionExemptionService 单元测试通过（物料必填、自动带出、免检矩阵4种组合）
- [ ] 有效期过期判断测试通过 — 场景3
- [ ] InspectionExemptionResource 集成测试通过
- [ ] Excel 导入导出测试通过

## 跨模块集成（feat-supplier 完成后验证）
- [ ] 供应商下拉选择器与 feat-supplier API 联通
- [ ] 供应商名称自动带出功能正常
- [ ] （可选）supplier_id 添加 DB 级 FK 约束

## 代码质量
- [ ] 四层架构分离
- [ ] DTO 与实体分离
- [ ] BusinessException 统一异常处理
- [ ] 审计字段自动填充
- [ ] supplier_id 跨模块引用有降级处理（supplier 模块不可用时不阻塞）
