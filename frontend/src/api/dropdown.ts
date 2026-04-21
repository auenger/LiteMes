import { apiGet } from './http';

/**
 * Common Dropdown API module.
 * Provides dropdown options for company, factory, department, shift-schedule.
 */

export interface DropdownItem {
  id: number;
  code: string;
  name: string;
  parentId?: number;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

export function getCompanyDropdown() {
  return apiGet<ApiResponse<DropdownItem[]>>('/api/dropdown/companies');
}

export function getFactoryDropdown(companyId?: number) {
  const params = companyId ? { companyId } : undefined;
  return apiGet<ApiResponse<DropdownItem[]>>('/api/dropdown/factories', { params });
}

export function getDepartmentDropdown(factoryId?: number) {
  const params = factoryId ? { factoryId } : undefined;
  return apiGet<ApiResponse<DropdownItem[]>>('/api/dropdown/departments', { params });
}

export function getShiftScheduleDropdown() {
  return apiGet<ApiResponse<DropdownItem[]>>('/api/dropdown/shift-schedules');
}

export function getMaterialCategoryDropdown() {
  return apiGet<ApiResponse<DropdownItem[]>>('/api/dropdown/material-categories');
}
