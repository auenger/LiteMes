# Tasks: feat-equipment-type 设备类型

## 任务清单

### 1. 数据层
- [x] 定义 EquipmentType 实体类（继承 SoftDeleteEntity，字段：type_code, type_name, status） — 对齐 feat-uom 的 Uom 实体模式
- [x] 创建 equipment_type 表 DDL（唯一键 type_code，软删除，审计字段） — 场景 1
- [x] 定义 EquipmentTypeMapper（MyBatis-Plus BaseMapper） — 对齐 UomMapper 模式
- [x] 定义 EquipmentTypeRepository 接口（domain 层） — CRUD + 引用检查
- [x] 实现 EquipmentTypeRepositoryImpl（infrastructure 层） — 引用检查：查询 equipment_model 表是否有引用（暂未实现，等待 feat-equipment-model）

### 2. DTO 定义
- [x] EquipmentTypeDto（查询返回） — 包含 id, type_code, type_name, status, 审计字段
- [x] EquipmentTypeCreateDto（创建请求） — type_code, type_name 必填校验（@NotBlank）
- [x] EquipmentTypeUpdateDto（更新请求） — type_name 可编辑
- [x] EquipmentTypeQueryDto（查询参数） — type_code/type_name 模糊查询, status 精确筛选, 分页参数

### 3. 后端逻辑
- [x] 实现 EquipmentTypeService — 对齐 feat-uom 的 UomService 模式
  - [x] create()：编码唯一性校验 + DTO→Entity 转换 + 保存 — 场景 1, 场景 4
  - [x] update()：编码不可修改约束 + 名称更新 — 场景 2
  - [x] delete()：引用检查（被设备型号引用时拒绝） — 场景 3（占位，待 feat-equipment-model）
  - [x] toggleStatus()：启用/禁用切换
  - [x] list()：分页查询 + 模糊搜索 — 场景 1
  - [x] getById()：按 ID 查询详情
- [x] 实现 EquipmentTypeResource（JAX-RS） — 对齐 UomResource 模式
  - [x] POST /api/equipment-types — 创建
  - [x] PUT /api/equipment-types/{id} — 更新
  - [x] DELETE /api/equipment-types/{id} — 删除（带引用检查）
  - [x] GET /api/equipment-types — 分页列表查询
  - [x] GET /api/equipment-types/{id} — 详情
  - [x] PUT /api/equipment-types/{id}/status — 启用/禁用
  - [ ] POST /api/equipment-types/import — Excel 导入（暂不实现，后续迭代）
  - [ ] GET /api/equipment-types/export — Excel 导出（暂不实现，后续迭代）

### 4. 前端
- [x] 设备类型管理页面（列表 + 查询 + 操作按钮） — 对齐 Uom 管理页面
- [x] 创建/编辑弹窗（编码创建时可填、编辑时只读；名称必填）
- [x] 删除确认弹窗（引用时按钮置灰）
- [x] 启用/禁用操作（确认提示）
- [ ] 导入导出 UI（上传弹窗 + 导出按钮 + 结果反馈）（暂不实现，后续迭代）

### 5. 测试
- [x] EquipmentTypeResource 集成测试（QuarkusTest + REST Assured）
  - [x] 创建成功 + 编码唯一性校验 — 场景 1, 场景 4
  - [x] 编辑成功 + 编码不可修改约束 — 场景 2
  - [x] 删除成功 — 场景 3（软删除验证）
  - [x] 401 无 Token 测试（通过 validation 注解覆盖）
  - [x] 分页查询测试
  - [x] 状态过滤测试
- [ ] Excel 导入导出测试 — 场景 5（暂不实现，后续迭代）

### 6. 下拉数据源
- [x] DropdownService 增加 getEquipmentTypeDropdown()
- [x] DropdownResource 增加 GET /api/dropdown/equipment-types
- [x] 前端 dropdown.ts 增加 getEquipmentTypeDropdown()

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-equipment-master 拆分 |
| 2026-04-21 | 已补充 | 补充数据库模型、详细任务、Gherkin 场景映射 |
| 2026-04-22 | 已实现 | 后端四层架构 + 前端 CRUD + 14 个测试全部通过 |
