# Checklist: feat-e2e-production-base

## Completion Checklist

### Development
- [ ] 3 个 spec 文件编写完成（work-center/uom/uom-conversion）
- [ ] 复用 auth fixture 和公共工具函数
- [ ] 代码自测通过

### Code Quality
- [ ] 测试命名规范统一
- [ ] 测试数据隔离（E2E_PROD_ 前缀）
- [ ] 无硬编码等待

### Testing — 工作中心
- [ ] 列表加载 + 分页
- [ ] 新建工作中心（关联工厂选择器）
- [ ] 必填校验
- [ ] 编辑（编码只读）
- [ ] 删除

### Testing — 计量单位
- [ ] 列表加载
- [ ] 新建计量单位（编码/名称/精度 number 输入）
- [ ] 必填校验
- [ ] 编辑（名称 + 精度可改，编码只读）
- [ ] 删除

### Testing — 单位换算
- [ ] 列表加载
- [ ] 新建换算关系（源单位/目标单位选择器 + 比例）
- [ ] 删除换算关系

### Cleanup
- [ ] afterEach 清理测试数据（反向依赖顺序）
- [ ] 测试可重复执行
