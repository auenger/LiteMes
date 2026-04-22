# Checklist: feat-e2e-smoke-test

## Completion Checklist

### Development
- [ ] 登录 500 错误已修复，curl 验证通过
- [ ] CORS 配置已更新，跨域请求正常
- [ ] Playwright 环境搭建完成
- [ ] 验收脚本编写完成（auth + company-crud）

### Testing
- [ ] `npx playwright test` 全部通过
- [ ] HTML 报告生成正常
- [ ] 登录成功场景通过
- [ ] 登录失败场景通过
- [ ] 路由守卫场景通过
- [ ] 公司 CRUD 场景通过

### Code Quality
- [ ] 修复代码符合项目编码规范
- [ ] Playwright 脚本可维护（选择器稳定、步骤清晰）

### Documentation
- [ ] spec.md technical solution 已更新实际修复内容
- [ ] 发现的问题和解决方案已记录
