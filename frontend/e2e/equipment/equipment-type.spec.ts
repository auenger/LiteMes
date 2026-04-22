import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('设备类型 E2E 测试', () => {
  const TS = Date.now().toString(36);
  const TYPE_CODE = `E2E_EQ_TYPE_${TS}`;
  const TYPE_NAME = `E2E测试设备类型_${TS}`;
  const TYPE_CODE_EDIT = `E2E_EQ_TYPE_ED_${TS}`;
  const TYPE_NAME_EDIT = `E2E编辑设备类型_${TS}`;
  const TYPE_CODE_DEL = `E2E_EQ_TYPE_DL_${TS}`;
  const TYPE_NAME_DEL = `E2E删除设备类型_${TS}`;

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/equipment-types');
    await page.waitForLoadState('networkidle');
  });

  test('设备类型列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
  });

  test('新建设备类型', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建设备类型")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入设备类型编码"]').fill(TYPE_CODE);
    await dialog.locator('input[placeholder="请输入设备类型名称"]').fill(TYPE_NAME);
    await dialog.locator('button:has-text("确定")').click();

    // Wait for dialog to close
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify type appears in table
    await assertTableContains(page, TYPE_CODE);
  });

  test('设备类型编码必填校验', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建设备类型")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Click confirm without filling any fields
    await dialog.locator('button:has-text("确定")').click();

    // Expect validation error message
    await expect(dialog.locator('.text-red-500')).toBeVisible({ timeout: 3000 });
  });

  test('编辑设备类型', async ({ authenticatedPage: page }) => {
    // First create a type to edit
    await page.locator('button:has-text("新建设备类型")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入设备类型编码"]').fill(TYPE_CODE_EDIT);
    await dialog.locator('input[placeholder="请输入设备类型名称"]').fill(TYPE_NAME_EDIT);
    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, TYPE_CODE_EDIT);

    // Now edit
    const row = await findTableRowByText(page, TYPE_CODE_EDIT);
    await clickRowAction(row, '编辑');
    dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Code should be read-only
    const codeInput = dialog.locator('input[placeholder="请输入设备类型编码"]');
    await expect(codeInput).toBeDisabled();

    // Update name
    const nameInput = dialog.locator('input[placeholder="请输入设备类型名称"]');
    await nameInput.clear();
    await nameInput.fill(`${TYPE_NAME_EDIT}_编辑后`);

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify name update
    await assertTableContains(page, `${TYPE_NAME_EDIT}_编辑后`);
  });

  test('删除设备类型', async ({ authenticatedPage: page }) => {
    // First create a type to delete
    await page.locator('button:has-text("新建设备类型")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入设备类型编码"]').fill(TYPE_CODE_DEL);
    await dialog.locator('input[placeholder="请输入设备类型名称"]').fill(TYPE_NAME_DEL);
    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, TYPE_CODE_DEL);

    // Delete the type
    const row = await findTableRowByText(page, TYPE_CODE_DEL);
    await clickRowAction(row, '删除');

    // Confirm delete dialog
    const deleteDialog = page.locator('.el-dialog:visible').filter({ hasText: '确认删除' });
    await expect(deleteDialog).toBeVisible({ timeout: 3000 });
    await deleteDialog.locator('button:has-text("删除")').click();

    // Wait for dialog to close
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify deletion
    await assertTableNotContains(page, TYPE_CODE_DEL);
  });
});
