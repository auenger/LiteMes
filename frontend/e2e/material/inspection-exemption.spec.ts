import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

/**
 * Helper: click an el-select, then select an option from the visible dropdown.
 * Uses force click on the option because Element Plus teleported poppers can
 * become "not stable" during animation transitions.
 */
async function selectOption(page: any, selectLocator: any, optionText: string) {
  await selectLocator.click({ force: true });
  await page.waitForTimeout(300);
  const visibleDropdown = page.locator('.el-select-dropdown:visible').last();
  await expect(visibleDropdown).toBeVisible({ timeout: 3000 });
  await visibleDropdown.locator('.el-select-dropdown__item').filter({ hasText: optionText }).first().click({ force: true });
}

/**
 * Helper: click an el-select, then pick the first option from the visible dropdown.
 */
async function selectFirstOption(page: any, selectLocator: any) {
  await selectLocator.click({ force: true });
  await page.waitForTimeout(300);
  const visibleDropdown = page.locator('.el-select-dropdown:visible').last();
  await expect(visibleDropdown).toBeVisible({ timeout: 3000 });
  await visibleDropdown.locator('.el-select-dropdown__item').first().click({ force: true });
}

/**
 * Helper: create a material via UI (used by exemption tests to set up prerequisite data).
 */
async function createMaterialViaUI(page: any, code: string, name: string) {
  await page.goto('/materials');
  await page.waitForLoadState('networkidle');
  await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

  await page.locator('button:has-text("新建物料")').click();
  const dialog = page.locator('.el-dialog:visible');
  await expect(dialog).toBeVisible({ timeout: 3000 });

  await dialog.locator('input[placeholder="请输入物料编码"]').fill(code);
  await dialog.locator('input[placeholder="请输入物料名称"]').fill(name);

  // Select basic category
  const basicCatSelect = dialog.locator('.el-form-item').filter({ hasText: '基本分类' }).locator('.el-select');
  await selectOption(page, basicCatSelect, '原材料');

  // Select material category
  const matCatSelect = dialog.locator('.el-form-item').filter({ hasText: '物料分类' }).locator('.el-select');
  await selectFirstOption(page, matCatSelect);

  // Select attribute category
  const attrCatSelect = dialog.locator('.el-form-item').filter({ hasText: '属性分类' }).locator('.el-select');
  await selectOption(page, attrCatSelect, '采购件');

  // Select UOM
  const uomSelect = dialog.locator('.el-form-item').filter({ hasText: '单位' }).locator('.el-select');
  await selectFirstOption(page, uomSelect);

  await dialog.locator('button:has-text("确定")').click();
  await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
  await assertTableContains(page, code);
}

test.describe('免检清单 E2E 测试', () => {
  const TS = Date.now().toString(36);
  const MAT_CODE = `E2E_MAT_IE_${TS}`;
  const MAT_NAME = `E2E免检物料_${TS}`;

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/inspection-exemptions');
    await page.waitForLoadState('networkidle');
  });

  test('免检清单列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
  });

  test('新建免检记录', async ({ authenticatedPage: page }) => {
    // First create a material for the exemption
    await createMaterialViaUI(page, MAT_CODE, MAT_NAME);

    // Navigate back to inspection exemptions page
    await page.goto('/inspection-exemptions');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    // Create exemption
    await page.locator('button:has-text("新建免检规则")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Select material from dropdown
    const materialSelect = dialog.locator('.el-form-item').filter({ hasText: '物料' }).locator('.el-select');
    await selectOption(page, materialSelect, MAT_CODE);

    await dialog.locator('button:has-text("确定")').click();

    // Wait for dialog to close
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify exemption appears in table
    await assertTableContains(page, MAT_CODE);
  });

  test('删除免检记录', async ({ authenticatedPage: page }) => {
    const delTS = Date.now().toString(36);
    const delMatCode = `E2E_MAT_IE_DEL_${delTS}`;
    const delMatName = `E2E免检删除物料_${delTS}`;

    // Create a material first
    await createMaterialViaUI(page, delMatCode, delMatName);

    // Navigate to inspection exemptions and create exemption
    await page.goto('/inspection-exemptions');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    await page.locator('button:has-text("新建免检规则")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    const materialSelect = dialog.locator('.el-form-item').filter({ hasText: '物料' }).locator('.el-select');
    await selectOption(page, materialSelect, delMatCode);

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, delMatCode);

    // Now delete the exemption
    const row = await findTableRowByText(page, delMatCode);
    await deleteWithConfirm(page, row);

    // Verify deletion
    await assertTableNotContains(page, delMatCode);
  });
});
