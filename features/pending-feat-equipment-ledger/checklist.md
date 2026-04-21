# Checklist: feat-equipment-ledger 设备台账

## 完成检查清单

### 数据层
- [ ] EquipmentLedger 实体类定义完成（继承 SoftDeleteEntity）
- [ ] RunningStatus 枚举定义完成（RUNNING/FAULT/SHUTDOWN/MAINTENANCE）
- [ ] ManageStatus 枚举定义完成（IN_USE/IDLE/SCRAPPED）
- [ ] equipment_ledger 表 DDL 已创建（含 equipment_model_id FK, factory_id FK, 冗余字段, 多索引）
- [ ] EquipmentLedgerMapper 已定义
- [ ] EquipmentLedgerRepository 接口与实现完成

### 后端逻辑
- [ ] CRUD API 端点全部实现（POST/PUT/DELETE/GET）
- [ ] 编码唯一性校验（创建时）
- [ ] 编码不可修改约束（编辑时）
- [ ] 设备型号存在性校验（创建/编辑时）
- [ ] 工厂存在性校验（创建/编辑时，跨模块 FK）
- [ ] 型号→类型级联自动填充（冗余字段同步）
- [ ] 工厂信息自动填充（冗余字段同步）
- [ ] 引用检查（删除时检查是否被工单/维保等业务引用）
- [ ] 启用/禁用切换
- [ ] Excel 导入（含唯一性校验、型号/工厂存在性校验、错误报告）
- [ ] Excel 导出
- [ ] 分页查询 + 模糊搜索 + 多条件组合筛选（类型/型号/运行状态/管理状态/工厂）
- [ ] 变更履历记录（通过 BaseEntity 自动审计）

### DTO 校验
- [ ] EquipmentLedgerCreateDto（@NotBlank equipment_code, equipment_name; @NotNull equipment_model_id, running_status, manage_status, factory_id, commissioning_date）
- [ ] EquipmentLedgerUpdateDto（可编辑字段校验）
- [ ] EquipmentLedgerQueryDto（分页参数 + 多条件查询参数）

### 前端
- [ ] 设备台账管理页面（列表 + 多条件组合查询 + 操作）
- [ ] 创建/编辑弹窗
  - [ ] 编码（创建可填/编辑只读）
  - [ ] 设备型号下拉 + 级联带出设备类型（只读）
  - [ ] 运行状态/管理状态下拉组件
  - [ ] 工厂下拉 + 自动带出名称
  - [ ] 生产厂家 + 入场时间
- [ ] 删除确认（引用时按钮置灰）
- [ ] 启用/禁用操作
- [ ] 导入导出 UI

### 跨模块集成
- [ ] factory_id FK 指向 feat-factory 的 factory 表
- [ ] equipment_model_id FK 指向 feat-equipment-model 的 equipment_model 表
- [ ] 设备类型信息通过型号级联获取（equipment_type_id 冗余）

### 测试
- [ ] EquipmentLedgerService 单元测试（CRUD + 约束 + 级联 + 联动）
- [ ] EquipmentLedgerResource 集成测试（端到端 + 401 + 多条件查询）
- [ ] 跨模块 FK 约束测试
- [ ] Excel 导入导出测试
- [ ] 测试通过

### 代码质量
- [ ] 四层架构分离（web/application/domain/infrastructure）
- [ ] 代码风格符合 CLAUDE.md 规范
- [ ] 无业务逻辑泄漏到 Resource 层
- [ ] 必要的注释已添加

### 文档
- [ ] spec.md 数据模型与业务逻辑完整
- [ ] task.md 任务全部勾选
- [ ] 相关文档已更新
