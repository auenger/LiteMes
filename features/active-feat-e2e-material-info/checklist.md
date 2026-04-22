# Checklist: feat-e2e-material-info

## Completion Checklist

### Development
- [x] 2 个 spec 文件编写完成（material/inspection-exemption）
- [x] 正确导入并复用 auth fixture
- [x] 代码自测通过

### Code Quality
- [x] 测试命名规范统一
- [x] 测试数据隔离（E2E_MAT_ 前缀）
- [x] 无硬编码等待

### Testing — 物料信息
- [x] 列表加载
- [x] 新建物料（基本字段：编码/名称 + 分类/属性分类/单位选择器）
- [x] 新建物料含 PCB 属性（尺寸/长度/宽度/型号/规格/厚度等）
- [x] 必填校验
- [x] 编辑（编码只读）
- [x] 删除

### Testing — 免检清单
- [x] 列表加载
- [x] 新建免检记录（物料选择器）
- [x] 删除免检记录

### Cleanup
- [x] afterEach 清理测试数据（反向依赖：免检 → 物料）
- [x] 测试可重复执行

## Verification Record
| Date | Status | Results | Evidence |
|------|--------|---------|----------|
| 2026-04-23 | PASS | 9/9 tests passed (51.1s) | evidence/playwright-report/ |

