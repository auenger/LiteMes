# Checklist: feat-e2e-supply-chain

## Completion Checklist

### Development
- [ ] 2 个 spec 文件编写完成（customer/supplier）
- [ ] 复用 auth fixture 和公共工具函数
- [ ] 代码自测通过

### Code Quality
- [ ] 测试命名规范统一
- [ ] 测试数据隔离（E2E_SC_ 前缀）
- [ ] 无硬编码等待

### Testing — 客户管理
- [ ] 列表加载
- [ ] 新建客户（编码/名称/类型选择）
- [ ] 必填校验
- [ ] 编辑（编码只读）
- [ ] 删除

### Testing — 供应商管理
- [ ] 列表加载
- [ ] 新建供应商（编码/名称）
- [ ] 必填校验
- [ ] 编辑（编码只读）
- [ ] 删除

### Cleanup
- [ ] afterEach 清理测试数据
- [ ] 测试可重复执行
