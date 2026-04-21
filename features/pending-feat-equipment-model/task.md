# Tasks: feat-equipment-model 设备型号

## 任务清单

### 1. 数据层
- [ ] 定义 EquipmentModel 实体类（继承 SoftDeleteEntity，字段：model_code, model_name, equipment_type_id, type_code, type_name, status） — 对齐 feat-uom 的 Uom 实体模式
- [ ] 创建 equipment_model 表 DDL（唯一键 model_code，FK equipment_type_id，冗余 type_code/type_name，软删除，审计字段） — 场景 1
- [ ] 定义 EquipmentModelMapper（MyBatis-Plus BaseMapper） — 对齐 UomMapper 模式
- [ ] 定义 EquipmentModelRepository 接口（domain 层） — CRUD + 引用检查
- [ ] 实现 EquipmentModelRepositoryImpl（infrastructure 层） — 引用检查：查询 equipment_ledger 表是否有引用

### 2. DTO 定义
- [ ] EquipmentModelDto（查询返回） — 包含 id, model_code, model_name, equipment_type_id, type_code, type_name, status, 审计字段
- [ ] EquipmentModelCreateDto（创建请求） — model_code, model_name, equipment_type_id 必填校验
- [ ] EquipmentModelUpdateDto（更新请求） — model_name, equipment_type_id 可编辑
- [ ] EquipmentModelQueryDto（查询参数） — model_code/model_name 模糊查询, equipment_type_id 精确筛选, status 精确筛选, 分页参数

### 3. 后端逻辑
- [ ] 实现 EquipmentModelService — 对齐 feat-uom 的 UomService 模式
  - [ ] create()：编码唯一性校验 + 设备类型存在性校验 + 冗余字段自动填充 + 保存 — 场景 1, 场景 4
  - [ ] update()：编码不可修改约束 + 类型更换时更新冗余字段 — 场景 2
  - [ ] delete()：引用检查（被设备台账引用时拒绝） — 场景 3
  - [ ] toggleStatus()：启用/禁用切换
  - [ ] list()：分页查询 + 模糊搜索 + 类型筛选 — 场景 1
  - [ ] getById()：按 ID 查询详情
- [ ] 实现 EquipmentModelResource（JAX-RS）
  - [ ] POST /api/equipment-models — 创建
  - [ ] PUT /api/equipment-models/{id} — 更新
  - [ ] DELETE /api/equipment-models/{id} — 删除（带引用检查）
  - [ ] GET /api/equipment-models — 分页列表查询
  - [ ] GET /api/equipment-models/{id} — 详情
  - [ ] PUT /api/equipment-models/{id}/status — 启用/禁用
  - [ ] POST /api/equipment-models/import — Excel 导入
  - [ ] GET /api/equipment-models/export — Excel 导出
- [ ] 设备类型下拉数据源接口（复用 feat-equipment-type 的 list 接口或提供专用下拉接口） — 场景 5

### 4. 前端
- [ ] 设备型号管理页面（列表 + 查询 + 操作按钮） — 对齐 Uom 管理页面
- [ ] 创建/编辑弹窗（编码创建时可填、编辑时只读；名称必填；设备类型下拉选择） — 场景 5
- [ ] 设备类型下拉组件（选择后自动带出类型名称） — 场景 5
- [ ] 删除确认弹窗（引用时按钮置灰）
- [ ] 启用/禁用操作（确认提示）
- [ ] 导入导出 UI（上传弹窗 + 导出按钮 + 结果反馈）

### 5. 测试
- [ ] EquipmentModelService 单元测试
  - [ ] 创建成功 + 编码唯一性校验 + 类型存在性校验 — 场景 1, 场景 4
  - [ ] 编辑成功 + 编码不可修改约束 + 冗余字段同步 — 场景 2
  - [ ] 删除约束（被设备台账引用拒绝） — 场景 3
  - [ ] 设备类型下拉联动 — 场景 5
- [ ] EquipmentModelResource 集成测试（QuarkusTest + REST Assured）
  - [ ] CRUD 端到端测试
  - [ ] 401 无 Token 测试
  - [ ] 分页查询 + 类型筛选测试
- [ ] Excel 导入导出测试 — 场景 6

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-equipment-master 拆分 |
| 2026-04-21 | 已补充 | 补充数据库模型、详细任务、Gherkin 场景映射 |
