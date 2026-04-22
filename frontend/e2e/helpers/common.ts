import { Page, Locator, expect } from '@playwright/test';

export async function findTableRowByText(page: Page, text: string): Promise<Locator> {
  const row = page.locator('.el-table__body-wrapper .el-table__row').filter({ hasText: text }).first();
  await expect(row).toBeVisible({ timeout: 5000 });
  return row;
}

export async function clickRowAction(row: Locator, actionText: string): Promise<void> {
  await row.locator(`button:has-text("${actionText}")`).first().evaluate(el => (el as HTMLElement).click());
}

export async function deleteWithConfirm(page: Page, row: Locator): Promise<void> {
  await clickRowAction(row, '删除');
  const deleteDialog = page.locator('.el-dialog:visible').filter({ hasText: '确认删除' });
  await expect(deleteDialog).toBeVisible({ timeout: 3000 });
  await deleteDialog.locator('button:has-text("删除")').click();
  await page.waitForTimeout(500);
}

export async function assertTableContains(page: Page, text: string): Promise<void> {
  await expect(page.locator('.el-table').filter({ hasText: text })).toBeVisible({ timeout: 5000 });
}

export async function assertTableNotContains(page: Page, text: string): Promise<void> {
  const table = page.locator('.el-table');
  await expect(table.filter({ hasText: text })).not.toBeVisible({ timeout: 5000 });
}
