import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('物料分类 E2E 测试', () => {
  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/material-categories');
    await page.waitForLoadState('domcontentloaded');
    // 等待表格加载
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
  });

  test('物料分类列表加载（树形结构）', async ({ authenticatedPage: page }) => {
    // 表格可见
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 5000 });
    // 左侧树面板可见
    await expect(page.locator('text=分类结构')).toBeVisible({ timeout: 5000 });
  });

  test('新建物料分类（顶级）', async ({ authenticatedPage: page }) => {
    const ts = Date.now().toString(36);
    const parentCode = `E2E_MAT_CAT_${ts}`;
    const parentName = `E2E测试分类_${ts}`;

    await page.locator('button:has-text("新建分类")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入分类编码"]').fill(parentCode);
    await dialog.locator('input[placeholder="请输入分类名称"]').fill(parentName);
    await dialog.locator('button:has-text("确定")').click();

    // 等待对话框关闭
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // 等待表格中出现新建的分类
    await assertTableContains(page, parentCode);

    // 树中也应出现
    await expect(page.locator('.tree-node').filter({ hasText: parentCode })).toBeVisible({ timeout: 5000 });
  });

  test('新建子分类（层级关系）', async ({ authenticatedPage: page }) => {
    const ts = Date.now().toString(36);
    const parentCode = `E2E_MC_P_${ts}`;
    const parentName = `E2E父分类_${ts}`;
    const childCode = `E2E_MC_C_${ts}`;
    const childName = `E2E子分类_${ts}`;

    // 先创建父分类
    await page.locator('button:has-text("新建分类")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入分类编码"]').fill(parentCode);
    await dialog.locator('input[placeholder="请输入分类名称"]').fill(parentName);
    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, parentCode);

    // 再创建子分类，选择父分类
    await page.locator('button:has-text("新建分类")').click();
    dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入分类编码"]').fill(childCode);
    await dialog.locator('input[placeholder="请输入分类名称"]').fill(childName);

    // 选择上级分类：使用 placeholder 定位 el-select 并通过 force click 打开
    const parentSelect = dialog.locator('.el-select').filter({ hasText: '无（顶级分类）' }).first();
    await parentSelect.click({ force: true });
    await page.waitForTimeout(500);

    // 从下拉列表中选择父分类
    const dropdownItems = page.locator('.el-select-dropdown__item');
    await dropdownItems.filter({ hasText: parentCode }).first().click();

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, childCode);

    // 验证树中父分类和子分类都可见
    await expect(page.locator('.tree-node').filter({ hasText: parentCode }).first()).toBeVisible({ timeout: 5000 });
    await expect(page.locator('.tree-node').filter({ hasText: childCode }).last()).toBeVisible({ timeout: 5000 });
  });

  test('编辑物料分类', async ({ authenticatedPage: page }) => {
    const ts = Date.now().toString(36);
    const code = `E2E_MC_EDIT_${ts}`;
    const name = `E2E编辑分类_${ts}`;

    // 创建分类
    await page.locator('button:has-text("新建分类")').click();
    const createDialog = page.locator('.el-dialog:visible');
    await expect(createDialog).toBeVisible({ timeout: 3000 });
    await createDialog.locator('input[placeholder="请输入分类编码"]').fill(code);
    await createDialog.locator('input[placeholder="请输入分类名称"]').fill(name);
    await createDialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, code);

    // 编辑
    const row = await findTableRowByText(page, code);
    await clickRowAction(row, '编辑');
    const editDialog = page.locator('.el-dialog:visible');
    await expect(editDialog).toBeVisible({ timeout: 3000 });

    // 编码不可编辑
    const codeInput = editDialog.locator('input[placeholder="请输入分类编码"]');
    await expect(codeInput).toBeDisabled();

    // 修改名称
    const nameInput = editDialog.locator('input[placeholder="请输入分类名称"]');
    await nameInput.clear();
    await nameInput.fill(`${name}_编辑`);
    await editDialog.locator('button:has-text("确定")').click();

    await assertTableContains(page, `${name}_编辑`);
  });

  test('删除子分类', async ({ authenticatedPage: page }) => {
    const ts = Date.now().toString(36);
    const code = `E2E_MC_DEL_${ts}`;
    const name = `E2E删除分类_${ts}`;

    // 创建分类
    await page.locator('button:has-text("新建分类")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入分类编码"]').fill(code);
    await dialog.locator('input[placeholder="请输入分类名称"]').fill(name);
    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, code);

    // 删除该分类
    const row = await findTableRowByText(page, code);
    await clickRowAction(row, '删除');

    // 确认删除弹窗
    const deleteDialog = page.locator('.el-dialog:visible').filter({ hasText: '确认删除' });
    await expect(deleteDialog).toBeVisible({ timeout: 3000 });
    await deleteDialog.locator('button:has-text("删除")').click();

    // 等待删除完成
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    // 从表格消失
    await assertTableNotContains(page, code);
  });
});
