import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

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
    // First we need to create a material for the exemption
    // Navigate to materials page to create a test material
    await page.goto('/materials');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    await page.locator('button:has-text("新建物料")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入物料编码"]').fill(MAT_CODE);
    await dialog.locator('input[placeholder="请输入物料名称"]').fill(MAT_NAME);

    // Select basic category
    const basicCatSelect = dialog.locator('.el-form-item').filter({ hasText: '基本分类' }).locator('.el-select');
    await basicCatSelect.click({ force: true });
    await page.waitForTimeout(300);
    await page.locator('.el-select-dropdown__item').filter({ hasText: '原材料' }).first().click();

    // Select material category
    const matCatSelect = dialog.locator('.el-form-item').filter({ hasText: '物料分类' }).locator('.el-select');
    await matCatSelect.click({ force: true });
    await page.waitForTimeout(300);
    await page.locator('.el-select-dropdown__item').first().click();

    // Select attribute category
    const attrCatSelect = dialog.locator('.el-form-item').filter({ hasText: '属性分类' }).locator('.el-select');
    await attrCatSelect.click({ force: true });
    await page.waitForTimeout(300);
    await page.locator('.el-select-dropdown__item').filter({ hasText: '采购件' }).first().click();

    // Select UOM
    const uomSelect = dialog.locator('.el-form-item').filter({ hasText: '单位' }).locator('.el-select');
    await uomSelect.click({ force: true });
    await page.waitForTimeout(300);
    await page.locator('.el-select-dropdown__item').first().click();

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, MAT_CODE);

    // Navigate back to inspection exemptions page
    await page.goto('/inspection-exemptions');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    // Create exemption
    await page.locator('button:has-text("新建免检规则")').click();
    dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Select material from dropdown
    const materialSelect = dialog.locator('.el-select').filter({ hasText: '请选择物料' }).first();
    if (await materialSelect.isVisible()) {
      await materialSelect.click({ force: true });
    } else {
      // Fallback: click the first select in the dialog
      const firstSelect = dialog.locator('.el-form-item').filter({ hasText: '物料' }).locator('.el-select');
      await firstSelect.click({ force: true });
    }
    await page.waitForTimeout(300);

    // Select the test material from dropdown
    await page.locator('.el-select-dropdown__item').filter({ hasText: MAT_CODE }).first().click();

    await dialog.locator('button:has-text("确定")').click();

    // Wait for dialog to close
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify exemption appears in table
    await assertTableContains(page, MAT_CODE);
  });

  test('删除免检记录', async ({ authenticatedPage: page }) => {
    // First create a material and an exemption record
    await page.goto('/materials');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    const delTS = Date.now().toString(36);
    const delMatCode = `E2E_MAT_IE_DEL_${delTS}`;
    const delMatName = `E2E免检删除物料_${delTS}`;

    await page.locator('button:has-text("新建物料")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入物料编码"]').fill(delMatCode);
    await dialog.locator('input[placeholder="请输入物料名称"]').fill(delMatName);

    // Select basic category
    const basicCatSelect = dialog.locator('.el-form-item').filter({ hasText: '基本分类' }).locator('.el-select');
    await basicCatSelect.click({ force: true });
    await page.waitForTimeout(300);
    await page.locator('.el-select-dropdown__item').filter({ hasText: '原材料' }).first().click();

    // Select material category
    const matCatSelect = dialog.locator('.el-form-item').filter({ hasText: '物料分类' }).locator('.el-select');
    await matCatSelect.click({ force: true });
    await page.waitForTimeout(300);
    await page.locator('.el-select-dropdown__item').first().click();

    // Select attribute category
    const attrCatSelect = dialog.locator('.el-form-item').filter({ hasText: '属性分类' }).locator('.el-select');
    await attrCatSelect.click({ force: true });
    await page.waitForTimeout(300);
    await page.locator('.el-select-dropdown__item').filter({ hasText: '采购件' }).first().click();

    // Select UOM
    const uomSelect = dialog.locator('.el-form-item').filter({ hasText: '单位' }).locator('.el-select');
    await uomSelect.click({ force: true });
    await page.waitForTimeout(300);
    await page.locator('.el-select-dropdown__item').first().click();

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, delMatCode);

    // Navigate to inspection exemptions and create exemption
    await page.goto('/inspection-exemptions');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    await page.locator('button:has-text("新建免检规则")').click();
    dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    const materialSelect = dialog.locator('.el-form-item').filter({ hasText: '物料' }).locator('.el-select');
    await materialSelect.click({ force: true });
    await page.waitForTimeout(300);
    await page.locator('.el-select-dropdown__item').filter({ hasText: delMatCode }).first().click();

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
