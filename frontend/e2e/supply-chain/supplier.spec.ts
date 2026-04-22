import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('供应商管理 E2E 测试', () => {
  const TS = Date.now().toString(36);
  const TEST_CODE = `E2E_SC_SUP_${TS}`;
  const TEST_NAME = `E2E测试供应商_${TS}`;
  const TEST_CODE_EDIT = `E2E_SC_SUP_ED_${TS}`;
  const TEST_NAME_EDIT = `E2E编辑供应商_${TS}`;
  const TEST_CODE_DEL = `E2E_SC_SUP_DL_${TS}`;
  const TEST_NAME_DEL = `E2E删除供应商_${TS}`;

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/suppliers');
    await page.waitForLoadState('networkidle');
  });

  test('供应商列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 5000 });
    await expect(page.locator('.el-pagination')).toBeVisible({ timeout: 5000 });
  });

  test('新建供应商成功', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建供应商")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入供应商编码"]').fill(TEST_CODE);
    await dialog.locator('input[placeholder="请输入供应商名称"]').fill(TEST_NAME);

    await dialog.locator('button:has-text("确定")').click();

    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, TEST_CODE);
  });

  test('供应商编码必填校验', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建供应商")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Click confirm without filling required fields
    await dialog.locator('button:has-text("确定")').click();

    // Expect validation error message
    await expect(dialog.locator('.text-red-500')).toBeVisible({ timeout: 3000 });
  });

  test('编辑供应商', async ({ authenticatedPage: page }) => {
    // Create a supplier first
    await page.locator('button:has-text("新建供应商")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入供应商编码"]').fill(TEST_CODE_EDIT);
    await dialog.locator('input[placeholder="请输入供应商名称"]').fill(TEST_NAME_EDIT);
    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, TEST_CODE_EDIT);

    // Edit
    const row = await findTableRowByText(page, TEST_CODE_EDIT);
    await clickRowAction(row, '编辑');
    dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Code should be read-only
    const codeInput = dialog.locator('input[placeholder="请输入供应商编码"]');
    await expect(codeInput).toBeDisabled();

    // Update name
    const nameInput = dialog.locator('input[placeholder="请输入供应商名称"]');
    await nameInput.clear();
    await nameInput.fill(`${TEST_NAME_EDIT}_编辑`);

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify name update
    await assertTableContains(page, `${TEST_NAME_EDIT}_编辑`);
  });

  test('删除供应商', async ({ authenticatedPage: page }) => {
    // Create a supplier first
    await page.locator('button:has-text("新建供应商")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入供应商编码"]').fill(TEST_CODE_DEL);
    await dialog.locator('input[placeholder="请输入供应商名称"]').fill(TEST_NAME_DEL);
    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, TEST_CODE_DEL);

    // Delete
    const row = await findTableRowByText(page, TEST_CODE_DEL);
    await deleteWithConfirm(page, row);

    // Verify deletion
    await assertTableNotContains(page, TEST_CODE_DEL);
  });
});
