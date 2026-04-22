# Checklist: feat-e2e-equipment

## Completion Checklist

### Development
- [ ] 3 个 spec 文件编写完成（equipment-type/equipment-model/equipment-ledger）
- [ ] 复用 auth fixture 和公共工具函数
- [ ] 代码自测通过

### Code Quality
- [ ] 测试命名规范统一
- [ ] 测试数据隔离（E2E_EQ_ 前缀）
- [ ] 无硬编码等待

### Testing — 设备类型
- [ ] 列表加载
- [ ] 新建设备类型
- [ ] 必填校验
- [ ] 编辑（编码只读）
- [ ] 删除

### Testing — 设备型号
- [ ] 列表加载
- [ ] 新建型号（关联设备类型选择器）
- [ ] 编辑（编码只读）
- [ ] 删除

### Testing — 设备台账
- [ ] 列表加载
- [ ] 新建设备（级联选择器：类型→型号）
- [ ] 运行状态/管理状态枚举值选择
- [ ] 工厂选择 + 制造商输入 + 日期选择器
- [ ] 级联选择器联动验证（选类型后型号过滤）
- [ ] 编辑（编码只读）
- [ ] 删除

### Cleanup
- [ ] afterEach 清理测试数据（反向依赖：台账 → 型号 → 类型）
- [ ] 测试可重复执行
