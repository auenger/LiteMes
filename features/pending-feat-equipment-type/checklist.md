# Checklist: feat-equipment-type 设备类型

## 完成检查清单

### 数据层
- [ ] EquipmentType 实体类定义完成（继承 SoftDeleteEntity）
- [ ] equipment_type 表 DDL 已创建
- [ ] EquipmentTypeMapper 已定义
- [ ] EquipmentTypeRepository 接口与实现完成

### 后端逻辑
- [ ] CRUD API 端点全部实现（POST/PUT/DELETE/GET）
- [ ] 编码唯一性校验（创建时）
- [ ] 编码不可修改约束（编辑时）
- [ ] 引用检查（删除时检查是否被设备型号引用）
- [ ] 启用/禁用切换
- [ ] Excel 导入（含唯一性校验、错误报告）
- [ ] Excel 导出
- [ ] 分页查询 + 模糊搜索
- [ ] 变更履历记录（通过 BaseEntity 自动审计）

### DTO 校验
- [ ] EquipmentTypeCreateDto（@NotBlank type_code, type_name）
- [ ] EquipmentTypeUpdateDto（@NotBlank type_name）
- [ ] EquipmentTypeQueryDto（分页参数 + 模糊查询参数）

### 前端
- [ ] 设备类型管理页面（列表 + 查询 + 操作）
- [ ] 创建/编辑弹窗
- [ ] 删除确认（引用时按钮置灰）
- [ ] 启用/禁用操作
- [ ] 导入导出 UI

### 测试
- [ ] EquipmentTypeService 单元测试（CRUD + 约束）
- [ ] EquipmentTypeResource 集成测试（端到端 + 401）
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
