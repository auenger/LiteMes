import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('计量单位管理 E2E 测试', () => {
  const TS = Date.now().toString(36);
  const UOM_CODE = `E2E_PROD_UOM_${TS}`;
  const UOM_NAME = `E2E测试单位_${TS}`;

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/uoms');
    await page.waitForLoadState('networkidle');
  });

  test('计量单位列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 5000 });
  });

  test('新建计量单位成功', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建单位")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入单位编码"]').fill(UOM_CODE);
    await dialog.locator('input[placeholder="请输入单位名称"]').fill(UOM_NAME);
    // Set precision to 2 via el-input-number
    const precisionInput = dialog.locator('.el-input-number .el-input__inner');
    await precisionInput.fill('2');
    await dialog.locator('button:has-text("确定")').click();

    await assertTableContains(page, UOM_CODE);
  });

  test('计量单位编码必填校验', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建单位")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('button:has-text("确定")').click();
    await expect(dialog.locator('.text-red-500')).toBeVisible({ timeout: 3000 });
  });

  test('编辑计量单位', async ({ authenticatedPage: page }) => {
    // Create
    await page.locator('button:has-text("新建单位")').click();
    const createDialog = page.locator('.el-dialog:visible');
    await createDialog.locator('input[placeholder="请输入单位编码"]').fill(UOM_CODE);
    await createDialog.locator('input[placeholder="请输入单位名称"]').fill(UOM_NAME);
    await createDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, UOM_CODE);

    // Edit
    const row = await findTableRowByText(page, UOM_CODE);
    await clickRowAction(row, '编辑');
    const editDialog = page.locator('.el-dialog:visible');
    await expect(editDialog).toBeVisible({ timeout: 3000 });

    const codeInput = editDialog.locator('input[placeholder="请输入单位编码"]');
    await expect(codeInput).toBeDisabled();

    const nameInput = editDialog.locator('input[placeholder="请输入单位名称"]');
    await nameInput.clear();
    await nameInput.fill(`${UOM_NAME}_编辑`);

    const precisionInput = editDialog.locator('.el-input-number .el-input__inner');
    await precisionInput.clear();
    await precisionInput.fill('3');

    await editDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, `${UOM_NAME}_编辑`);
  });

  test('删除计量单位', async ({ authenticatedPage: page }) => {
    // Create
    await page.locator('button:has-text("新建单位")').click();
    const createDialog = page.locator('.el-dialog:visible');
    await createDialog.locator('input[placeholder="请输入单位编码"]').fill(UOM_CODE);
    await createDialog.locator('input[placeholder="请输入单位名称"]').fill(UOM_NAME);
    await createDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, UOM_CODE);

    // Delete
    const row = await findTableRowByText(page, UOM_CODE);
    await deleteWithConfirm(page, row);
    await assertTableNotContains(page, UOM_CODE);
  });
});
