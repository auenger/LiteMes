# Checklist: feat-e2e-org

## Completion Checklist

### Development
- [ ] 公共 auth fixture (`e2e/fixtures/auth.ts`) 创建完成
- [ ] 公共工具函数 (`e2e/helpers/common.ts`) 创建完成
- [ ] 4 个 spec 文件编写完成（company/factory/department/shift-schedule）
- [ ] 代码自测通过

### Code Quality
- [ ] 测试命名规范统一（test.describe + test 描述清晰）
- [ ] 测试数据隔离（E2E_ORG_ 前缀）
- [ ] 无硬编码等待（使用 waitFor/toBeVisible 替代 waitForTimeout）
- [ ] 选择器稳定（优先 data-testid 或 placeholder，避免 CSS 类名依赖）

### Testing — 公司管理
- [ ] 列表加载 + 分页可见
- [ ] 新建公司（编码/名称/简码）
- [ ] 编码必填校验
- [ ] 编辑公司（名称可改，编码只读）
- [ ] 删除公司

### Testing — 工厂管理
- [ ] 列表加载
- [ ] 新建工厂（关联公司选择器）
- [ ] 必填校验
- [ ] 编辑工厂（编码只读）
- [ ] 删除工厂

### Testing — 部门管理
- [ ] 列表加载（树形结构）
- [ ] 新建顶级部门（关联工厂）
- [ ] 新建子部门（选择上级）
- [ ] 编辑部门
- [ ] 删除子部门

### Testing — 班制班次
- [ ] 列表加载
- [ ] 新建班制
- [ ] 管理班次（嵌套对话框：编码/名称/时间/跨天）
- [ ] 删除班制

### Cleanup
- [ ] afterEach 清理测试数据
- [ ] 测试可重复执行（幂等性）
