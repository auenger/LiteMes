import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * Department API module.
 */

export interface DepartmentDto {
  id: number;
  departmentCode: string;
  name: string;
  factoryId: number;
  factoryName: string;
  parentId: number | null;
  parentName: string | null;
  sortOrder: number;
  status: number;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface PagedResult<T> {
  records: T[];
  total: number;
  page: number;
  size: number;
  pages: number;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

export interface DepartmentQueryParams {
  departmentCode?: string;
  name?: string;
  factoryId?: number;
  status?: number;
  page?: number;
  size?: number;
}

export function listDepartments(params?: DepartmentQueryParams) {
  return apiGet<ApiResponse<PagedResult<DepartmentDto>>>('/api/departments', { params });
}

export function getDepartment(id: number) {
  return apiGet<ApiResponse<DepartmentDto>>(`/api/departments/${id}`);
}

export function createDepartment(data: {
  departmentCode: string;
  name: string;
  factoryId: number;
  parentId?: number | null;
  sortOrder?: number;
}) {
  return apiPost<ApiResponse<number>>('/api/departments', data);
}

export function updateDepartment(
  id: number,
  data: { name?: string; factoryId?: number; parentId?: number | null; sortOrder?: number }
) {
  return apiPut<ApiResponse<void>>(`/api/departments/${id}`, data);
}

export function deleteDepartment(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/departments/${id}`);
}

export function updateDepartmentStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/departments/${id}/status?status=${status}`);
}
