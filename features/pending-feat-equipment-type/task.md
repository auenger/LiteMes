# Tasks: feat-equipment-type 设备类型

## 任务清单

### 1. 数据层
- [ ] 定义 EquipmentType 实体类（继承 SoftDeleteEntity，字段：type_code, type_name, status） — 对齐 feat-uom 的 Uom 实体模式
- [ ] 创建 equipment_type 表 DDL（唯一键 type_code，软删除，审计字段） — 场景 1
- [ ] 定义 EquipmentTypeMapper（MyBatis-Plus BaseMapper） — 对齐 UomMapper 模式
- [ ] 定义 EquipmentTypeRepository 接口（domain 层） — CRUD + 引用检查
- [ ] 实现 EquipmentTypeRepositoryImpl（infrastructure 层） — 引用检查：查询 equipment_model 表是否有引用

### 2. DTO 定义
- [ ] EquipmentTypeDto（查询返回） — 包含 id, type_code, type_name, status, 审计字段
- [ ] EquipmentTypeCreateDto（创建请求） — type_code, type_name 必填校验（@NotBlank）
- [ ] EquipmentTypeUpdateDto（更新请求） — type_name 可编辑
- [ ] EquipmentTypeQueryDto（查询参数） — type_code/type_name 模糊查询, status 精确筛选, 分页参数

### 3. 后端逻辑
- [ ] 实现 EquipmentTypeService — 对齐 feat-uom 的 UomService 模式
  - [ ] create()：编码唯一性校验 + DTO→Entity 转换 + 保存 — 场景 1, 场景 4
  - [ ] update()：编码不可修改约束 + 名称更新 — 场景 2
  - [ ] delete()：引用检查（被设备型号引用时拒绝） — 场景 3
  - [ ] toggleStatus()：启用/禁用切换
  - [ ] list()：分页查询 + 模糊搜索 — 场景 1
  - [ ] getById()：按 ID 查询详情
- [ ] 实现 EquipmentTypeResource（JAX-RS） — 对齐 UomResource 模式
  - [ ] POST /api/equipment-types — 创建
  - [ ] PUT /api/equipment-types/{id} — 更新
  - [ ] DELETE /api/equipment-types/{id} — 删除（带引用检查）
  - [ ] GET /api/equipment-types — 分页列表查询
  - [ ] GET /api/equipment-types/{id} — 详情
  - [ ] PUT /api/equipment-types/{id}/status — 启用/禁用
  - [ ] POST /api/equipment-types/import — Excel 导入
  - [ ] GET /api/equipment-types/export — Excel 导出

### 4. 前端
- [ ] 设备类型管理页面（列表 + 查询 + 操作按钮） — 对齐 Uom 管理页面
- [ ] 创建/编辑弹窗（编码创建时可填、编辑时只读；名称必填）
- [ ] 删除确认弹窗（引用时按钮置灰）
- [ ] 启用/禁用操作（确认提示）
- [ ] 导入导出 UI（上传弹窗 + 导出按钮 + 结果反馈）

### 5. 测试
- [ ] EquipmentTypeService 单元测试
  - [ ] 创建成功 + 编码唯一性校验 — 场景 1, 场景 4
  - [ ] 编辑成功 + 编码不可修改约束 — 场景 2
  - [ ] 删除约束（被引用拒绝） — 场景 3
- [ ] EquipmentTypeResource 集成测试（QuarkusTest + REST Assured）
  - [ ] CRUD 端到端测试
  - [ ] 401 无 Token 测试
  - [ ] 分页查询测试
- [ ] Excel 导入导出测试 — 场景 5

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-equipment-master 拆分 |
| 2026-04-21 | 已补充 | 补充数据库模型、详细任务、Gherkin 场景映射 |
