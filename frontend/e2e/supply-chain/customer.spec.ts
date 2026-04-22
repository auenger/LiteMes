import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

/**
 * Helper: get the currently visible Element Plus select dropdown.
 */
function getVisibleDropdown(page: any) {
  return page.locator('.el-select-dropdown:visible').last();
}

/**
 * Helper: click an el-select, then select an option from the visible dropdown.
 */
async function selectOption(page: any, selectLocator: any, optionText: string) {
  await selectLocator.click({ force: true });
  await page.waitForTimeout(300);
  const visibleDropdown = getVisibleDropdown(page);
  await expect(visibleDropdown).toBeVisible({ timeout: 3000 });
  await visibleDropdown.locator('.el-select-dropdown__item').filter({ hasText: optionText }).first().click({ force: true });
}

test.describe('客户管理 E2E 测试', () => {
  const TS = Date.now().toString(36);
  const TEST_CODE = `E2E_SC_CUST_${TS}`;
  const TEST_NAME = `E2E测试客户_${TS}`;
  const TEST_CODE_EDIT = `E2E_SC_CUST_ED_${TS}`;
  const TEST_NAME_EDIT = `E2E编辑客户_${TS}`;
  const TEST_CODE_DEL = `E2E_SC_CUST_DL_${TS}`;
  const TEST_NAME_DEL = `E2E删除客户_${TS}`;

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/customers');
    await page.waitForLoadState('networkidle');
  });

  test('客户列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 5000 });
    await expect(page.locator('.el-pagination')).toBeVisible({ timeout: 5000 });
  });

  test('新建客户成功', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建客户")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入客户编码"]').fill(TEST_CODE);
    await dialog.locator('input[placeholder="请输入客户名称"]').fill(TEST_NAME);

    // Select customer type
    const typeSelect = dialog.locator('.el-form-item').filter({ hasText: '客户类型' }).locator('.el-select');
    await selectOption(page, typeSelect, '外贸客户');

    await dialog.locator('button:has-text("确定")').click();

    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, TEST_CODE);
  });

  test('客户编码必填校验', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建客户")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Click confirm without filling required fields
    await dialog.locator('button:has-text("确定")').click();

    // Expect validation error message
    await expect(dialog.locator('.text-red-500')).toBeVisible({ timeout: 3000 });
  });

  test('编辑客户', async ({ authenticatedPage: page }) => {
    // Create a customer first
    await page.locator('button:has-text("新建客户")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入客户编码"]').fill(TEST_CODE_EDIT);
    await dialog.locator('input[placeholder="请输入客户名称"]').fill(TEST_NAME_EDIT);
    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, TEST_CODE_EDIT);

    // Edit
    const row = await findTableRowByText(page, TEST_CODE_EDIT);
    await clickRowAction(row, '编辑');
    dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Code should be read-only
    const codeInput = dialog.locator('input[placeholder="请输入客户编码"]');
    await expect(codeInput).toBeDisabled();

    // Update name
    const nameInput = dialog.locator('input[placeholder="请输入客户名称"]');
    await nameInput.clear();
    await nameInput.fill(`${TEST_NAME_EDIT}_编辑`);

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify name update
    await assertTableContains(page, `${TEST_NAME_EDIT}_编辑`);
  });

  test('删除客户', async ({ authenticatedPage: page }) => {
    // Create a customer first
    await page.locator('button:has-text("新建客户")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入客户编码"]').fill(TEST_CODE_DEL);
    await dialog.locator('input[placeholder="请输入客户名称"]').fill(TEST_NAME_DEL);
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
