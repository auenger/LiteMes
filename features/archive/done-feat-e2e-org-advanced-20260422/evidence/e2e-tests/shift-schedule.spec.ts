import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('班制班次 E2E 测试', () => {
  const BASE = Date.now().toString(36);

  // Unique code per test to avoid conflicts
  function shiftCode(n: number) {
    return `E2E_ORG_SHIFT_${BASE}_${n}`;
  }
  function shiftName(n: number, label: string) {
    return `E2E${label}_${BASE}_${n}`;
  }
  function itemCode(n: number) {
    return `E2E_ORG_SI_${BASE}_${n}`;
  }
  function itemName(n: number) {
    return `E2E早班_${BASE}_${n}`;
  }

  async function createSchedule(page: any, code: string, name: string) {
    await page.locator('button:has-text("新增班制")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog
      .locator('input[placeholder="请输入班制编码"]')
      .fill(code);
    await dialog
      .locator('input[placeholder="请输入班制名称"]')
      .fill(name);
    await dialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, code);
  }

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/shift-schedule');
    await page.waitForLoadState('networkidle');
  });

  test('班制列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 5000 });
  });

  test('新建班制', async ({ authenticatedPage: page }) => {
    await createSchedule(page, shiftCode(1), shiftName(1, '白班'));
  });

  test('管理班次（嵌套对话框）', async ({ authenticatedPage: page }) => {
    const code = shiftCode(2);
    const iCode = itemCode(2);

    // Create schedule
    await createSchedule(page, code, shiftName(2, '白班'));

    // Open shift management
    const row = await findTableRowByText(page, code);
    await clickRowAction(row, '班次');
    const shiftDialog = page
      .locator('.el-dialog:visible')
      .filter({
        has: page.locator('.el-dialog__title', { hasText: '班次管理' }),
      });
    await expect(shiftDialog).toBeVisible({ timeout: 5000 });

    // Open shift create form (nested dialog)
    await shiftDialog.locator('button:has-text("新增班次")').click();
    const shiftForm = page
      .locator('.el-dialog:visible')
      .filter({
        has: page.locator('.el-dialog__title', { hasText: '新增班次' }),
      });
    await expect(shiftForm).toBeVisible({ timeout: 3000 });

    // Fill shift form
    await shiftForm
      .locator('input[placeholder="请输入班次编码"]')
      .fill(iCode);
    await shiftForm
      .locator('input[placeholder="请输入班次名称"]')
      .fill(itemName(2));
    const timeInputs = shiftForm.locator('input[type="time"]');
    await timeInputs.nth(0).fill('08:00');
    await timeInputs.nth(1).fill('17:00');
    await shiftForm.locator('button:has-text("确定")').click();

    // Verify shift in shift management table
    await expect(
      shiftDialog
        .locator('.el-table__body-wrapper .el-table__row')
        .filter({ hasText: iCode })
    ).toBeVisible({ timeout: 5000 });
  });

  test('删除班制', async ({ authenticatedPage: page }) => {
    const code = shiftCode(3);

    // Create schedule
    await createSchedule(page, code, shiftName(3, '白班'));

    // Delete with native confirm dialog
    const row = await findTableRowByText(page, code);
    page.once('dialog', d => d.accept());
    await clickRowAction(row, '删除');
    await page.waitForTimeout(1000);
    await assertTableNotContains(page, code);
  });
});
