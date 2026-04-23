# Checklist: feat-e2e-material

## Completion Checklist

### Development
- [ ] 3 个 spec 文件编写完成（material-category/material/inspection-exemption）
- [ ] 复用 auth fixture 和公共工具函数
- [ ] 代码自测通过

### Code Quality
- [ ] 测试命名规范统一
- [ ] 测试数据隔离（E2E_MAT_ 前缀）
- [ ] 无硬编码等待

### Testing — 物料分类
- [ ] 列表加载（树形结构）
- [ ] 新建分类
- [ ] 新建子分类（层级验证）
- [ ] 编辑分类
- [ ] 删除子分类

### Testing — 物料信息
- [ ] 列表加载
- [ ] 新建物料（基本字段：编码/名称 + 分类/属性分类/单位选择器）
- [ ] 新建物料含 PCB 属性（尺寸/长度/宽度/型号/规格/厚度等）
- [ ] 必填校验
- [ ] 编辑（编码只读）
- [ ] 删除

### Testing — 免检清单
- [ ] 列表加载
- [ ] 新建免检记录（物料 + 供应商选择器）
- [ ] 删除免检记录

### Cleanup
- [ ] afterEach 清理测试数据（反向依赖：免检 → 物料 → 分类）
- [ ] 测试可重复执行
