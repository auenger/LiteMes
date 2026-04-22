import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('公司管理 E2E 测试', () => {
  const TS = Date.now().toString(36);
  const TEST_CODE = `E2E_CO_${TS}`;
  const TEST_NAME = `E2E测试公司_${TS}`;
  const TEST_SHORT = 'E2E';

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/companies');
    await page.waitForLoadState('networkidle');
  });

  test('公司列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 5000 });
    await expect(page.locator('.el-pagination')).toBeVisible({ timeout: 5000 });
  });

  test('新建公司成功', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建公司")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入公司编码"]').fill(TEST_CODE);
    await dialog.locator('input[placeholder="请输入公司名称"]').fill(TEST_NAME);
    await dialog.locator('input[placeholder="请输入简码"]').fill(TEST_SHORT);
    await dialog.locator('button:has-text("确定")').click();

    await assertTableContains(page, TEST_CODE);
  });

  test('公司编码必填校验', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建公司")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入公司名称"]').fill('测试');
    await dialog.locator('button:has-text("确定")').click();

    await expect(dialog.locator('.text-red-500')).toBeVisible({ timeout: 3000 });
  });

  test('编辑公司', async ({ authenticatedPage: page }) => {
    // Create
    await page.locator('button:has-text("新建公司")').click();
    const createDialog = page.locator('.el-dialog:visible');
    await createDialog.locator('input[placeholder="请输入公司编码"]').fill(TEST_CODE);
    await createDialog.locator('input[placeholder="请输入公司名称"]').fill(TEST_NAME);
    await createDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, TEST_CODE);

    // Edit
    const row = await findTableRowByText(page, TEST_CODE);
    await clickRowAction(row, '编辑');
    const editDialog = page.locator('.el-dialog:visible');
    await expect(editDialog).toBeVisible({ timeout: 3000 });

    const codeInput = editDialog.locator('input[placeholder="请输入公司编码"]');
    await expect(codeInput).toBeDisabled();

    const nameInput = editDialog.locator('input[placeholder="请输入公司名称"]');
    await nameInput.clear();
    await nameInput.fill(`${TEST_NAME}_编辑`);
    await editDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, `${TEST_NAME}_编辑`);
  });

  test('删除公司', async ({ authenticatedPage: page }) => {
    // Create
    await page.locator('button:has-text("新建公司")').click();
    const createDialog = page.locator('.el-dialog:visible');
    await createDialog.locator('input[placeholder="请输入公司编码"]').fill(TEST_CODE);
    await createDialog.locator('input[placeholder="请输入公司名称"]').fill(TEST_NAME);
    await createDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, TEST_CODE);

    // Delete
    const row = await findTableRowByText(page, TEST_CODE);
    await deleteWithConfirm(page, row);
    await assertTableNotContains(page, TEST_CODE);
  });
});
