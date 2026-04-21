# Checklist: feat-equipment-model 设备型号

## 完成检查清单

### 数据层
- [ ] EquipmentModel 实体类定义完成（继承 SoftDeleteEntity）
- [ ] equipment_model 表 DDL 已创建（含 equipment_type_id FK + 冗余字段）
- [ ] EquipmentModelMapper 已定义
- [ ] EquipmentModelRepository 接口与实现完成

### 后端逻辑
- [ ] CRUD API 端点全部实现（POST/PUT/DELETE/GET）
- [ ] 编码唯一性校验（创建时）
- [ ] 编码不可修改约束（编辑时）
- [ ] 设备类型存在性校验（创建/编辑时）
- [ ] 冗余字段自动填充（type_code/type_name 随类型联动）
- [ ] 引用检查（删除时检查是否被设备台账引用）
- [ ] 启用/禁用切换
- [ ] Excel 导入（含唯一性校验、错误报告）
- [ ] Excel 导出
- [ ] 分页查询 + 模糊搜索 + 类型筛选
- [ ] 变更履历记录（通过 BaseEntity 自动审计）

### DTO 校验
- [ ] EquipmentModelCreateDto（@NotBlank model_code, model_name; @NotNull equipment_type_id）
- [ ] EquipmentModelUpdateDto（@NotBlank model_name; @NotNull equipment_type_id）
- [ ] EquipmentModelQueryDto（分页参数 + 模糊查询 + 类型筛选）

### 前端
- [ ] 设备型号管理页面（列表 + 查询 + 操作）
- [ ] 创建/编辑弹窗（含设备类型下拉选择）
- [ ] 设备类型下拉联动（选择后自动带出类型名称）
- [ ] 删除确认（引用时按钮置灰）
- [ ] 启用/禁用操作
- [ ] 导入导出 UI

### 测试
- [ ] EquipmentModelService 单元测试（CRUD + 约束 + 联动）
- [ ] EquipmentModelResource 集成测试（端到端 + 401）
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
