# Tasks: feat-equipment-ledger 设备台账

## 任务清单

### 1. 数据层
- [ ] 定义 EquipmentLedger 实体类（继承 SoftDeleteEntity，字段：equipment_code, equipment_name, equipment_model_id, model_code, model_name, equipment_type_id, type_code, type_name, running_status, manage_status, factory_id, factory_code, factory_name, manufacturer, commissioning_date, status） — 对齐 feat-uom 的 Uom 实体模式
- [ ] 定义 RunningStatus 枚举（RUNNING, FAULT, SHUTDOWN, MAINTENANCE） — 对应中文：运行/故障/停机/维修保养
- [ ] 定义 ManageStatus 枚举（IN_USE, IDLE, SCRAPPED） — 对应中文：使用中/闲置/报废
- [ ] 创建 equipment_ledger 表 DDL（唯一键 equipment_code，FK equipment_model_id，FK factory_id，多索引，冗余字段，软删除，审计字段） — 场景 1
- [ ] 定义 EquipmentLedgerMapper（MyBatis-Plus BaseMapper）
- [ ] 定义 EquipmentLedgerRepository 接口（domain 层） — CRUD + 引用检查
- [ ] 实现 EquipmentLedgerRepositoryImpl（infrastructure 层） — 引用检查：检查工单、维保记录等业务引用

### 2. DTO 定义
- [ ] EquipmentLedgerDto（查询返回） — 包含所有字段含冗余展示字段 + 审计字段
- [ ] EquipmentLedgerCreateDto（创建请求） — equipment_code, equipment_name, equipment_model_id, running_status, manage_status, factory_id, manufacturer, commissioning_date 必填校验
- [ ] EquipmentLedgerUpdateDto（更新请求） — equipment_name, equipment_model_id, running_status, manage_status, factory_id, manufacturer, commissioning_date 可编辑
- [ ] EquipmentLedgerQueryDto（查询参数） — equipment_code/equipment_name 模糊查询, equipment_type_id/equipment_model_id 精确筛选, running_status/manage_status 精确筛选, factory_id 精确筛选, 分页参数

### 3. 后端逻辑
- [ ] 实现 EquipmentLedgerService — 对齐 feat-uom 的 UomService 模式
  - [ ] create()：编码唯一性校验 + 型号存在性校验 + 工厂存在性校验 + 冗余字段自动填充（型号→类型级联 + 工厂信息） + 保存 — 场景 1, 场景 4
  - [ ] update()：编码不可修改约束 + 型号更换时同步更新类型冗余字段 + 工厂更换时同步更新工厂冗余字段 — 场景 2
  - [ ] delete()：引用检查（被工单/维保等业务引用时拒绝） — 场景 3
  - [ ] toggleStatus()：启用/禁用切换
  - [ ] list()：分页查询 + 模糊搜索 + 多条件组合筛选 — 场景 1
  - [ ] getById()：按 ID 查询详情
- [ ] 实现 EquipmentLedgerResource（JAX-RS）
  - [ ] POST /api/equipment-ledger — 创建
  - [ ] PUT /api/equipment-ledger/{id} — 更新
  - [ ] DELETE /api/equipment-ledger/{id} — 删除（带引用检查）
  - [ ] GET /api/equipment-ledger — 分页列表查询（支持多条件组合筛选）
  - [ ] GET /api/equipment-ledger/{id} — 详情
  - [ ] PUT /api/equipment-ledger/{id}/status — 启用/禁用
  - [ ] POST /api/equipment-ledger/import — Excel 导入
  - [ ] GET /api/equipment-ledger/export — Excel 导出
- [ ] 设备型号下拉数据源接口（复用 feat-equipment-model 的 list 接口或提供专用下拉接口） — 场景 5
- [ ] 工厂下拉数据源接口（复用 feat-factory 的 list 接口或提供专用下拉接口） — 场景 6

### 4. 前端
- [ ] 设备台账管理页面（列表 + 多条件组合查询 + 操作按钮）
- [ ] 创建/编辑弹窗
  - [ ] 编码（创建时可填、编辑时只读） + 名称必填
  - [ ] 设备型号下拉（选择后级联带出设备类型，类型只读） — 场景 5
  - [ ] 运行状态下拉组件（运行/故障/停机/维修保养）
  - [ ] 管理状态下拉组件（使用中/闲置/报废）
  - [ ] 工厂下拉（选择后自动带出工厂名称） — 场景 6
  - [ ] 生产厂家文本框 + 入场时间日期选择器
- [ ] 删除确认弹窗（引用时按钮置灰）
- [ ] 启用/禁用操作（确认提示）
- [ ] 导入导出 UI（上传弹窗 + 导出按钮 + 结果反馈）

### 5. 测试
- [ ] EquipmentLedgerService 单元测试
  - [ ] 创建成功 + 编码唯一性校验 — 场景 1, 场景 4
  - [ ] 编辑成功 + 编码/类型名称不可修改约束 + 冗余字段同步 — 场景 2
  - [ ] 删除约束（被业务引用拒绝） — 场景 3
  - [ ] 型号-类型级联联动验证 — 场景 5
  - [ ] 工厂下拉联动验证 — 场景 6
- [ ] EquipmentLedgerResource 集成测试（QuarkusTest + REST Assured）
  - [ ] CRUD 端到端测试
  - [ ] 401 无 Token 测试
  - [ ] 分页查询 + 多条件组合筛选测试
  - [ ] 跨模块 FK 约束测试（factory_id 引用 factory 表）
- [ ] Excel 导入导出测试 — 场景 7

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-equipment-master 拆分 |
| 2026-04-21 | 已补充 | 补充数据库模型（含枚举设计）、详细任务、Gherkin 场景映射 |
