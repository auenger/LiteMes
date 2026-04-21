# Checklist: feat-material-info 物料基本信息

## 数据层
- [ ] `material_master` 表 DDL 已创建，唯一索引 (material_code, material_name) 就位
- [ ] `material_version` 表 DDL 已创建，联合唯一索引 (material_id, version_no) 就位
- [ ] FK 约束已配置：category_id→material_category, uom_id→uom, material_version.material_id→material_master
- [ ] MaterialMaster / MaterialVersion 实体类继承 SoftDeleteEntity
- [ ] BasicCategory 枚举已定义（成品/半成品/原材料/备件）
- [ ] AttributeCategory 枚举已定义（采购件/自制件/采购&自制件）
- [ ] MyBatis Mapper + Repository 接口 + 实现已创建（两个实体各一套）

## 后端逻辑
- [ ] MaterialMaster CRUD API 已实现（创建/编辑/删除/查询/启用禁用）
- [ ] MaterialVersion 创建 + 查询 API 已实现
- [ ] 物料编码唯一性校验 — 场景1
- [ ] 物料编码不可修改约束 — 场景2
- [ ] 版本管理（material_id + version_no 唯一性）— 场景2
- [ ] 引用检查（被 InspectionExemption 引用时禁止删除）— 场景3
- [ ] 基本分类/属性分类枚举校验
- [ ] Excel 导入导出
- [ ] 变更日志记录

## 前端
- [ ] 物料信息管理页面（查询条件：编码/名称/物料分类/基本分类/状态 + 数据表格）
- [ ] 物料创建/编辑弹窗（编码编辑时只读；基本分类/属性分类枚举下拉）
- [ ] 物料分类下拉选择（调用 feat-material-category 的 API）
- [ ] 单位下拉选择（调用 feat-uom 的 API）
- [ ] 物料版本管理页签（嵌入物料详情，版本列表 + 创建弹窗）
- [ ] PCB 属性字段区域（按基本分类动态显示）

## 测试
- [ ] MaterialService 单元测试通过（唯一性、引用检查、枚举校验）
- [ ] MaterialVersionService 单元测试通过（版本号唯一性）
- [ ] MaterialResource 集成测试通过
- [ ] MaterialVersionResource 集成测试通过
- [ ] Excel 导入导出测试通过

## 代码质量
- [ ] 四层架构分离
- [ ] DTO 与实体分离（MaterialMaster 33 列的宽表不直接暴露给 API）
- [ ] BusinessException 统一异常处理
- [ ] 审计字段自动填充
- [ ] 枚举值通过 Hibernate Validator 校验
