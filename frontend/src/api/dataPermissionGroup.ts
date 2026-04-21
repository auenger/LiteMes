import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * DataPermissionGroup API module.
 */

export interface DataPermissionGroupDto {
  id: number;
  groupName: string;
  remark: string;
  factoryCount: number;
  workCenterCount: number;
  processCount: number;
  referenced: boolean;
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

export interface DataPermissionGroupQueryParams {
  groupName?: string;
  page?: number;
  size?: number;
}

export interface AssociatedEntityDto {
  id: number;
  code: string;
  name: string;
}

export function listDataPermissionGroups(params?: DataPermissionGroupQueryParams) {
  return apiGet<ApiResponse<PagedResult<DataPermissionGroupDto>>>('/api/data-permission-groups', { params });
}

export function getDataPermissionGroup(id: number) {
  return apiGet<ApiResponse<DataPermissionGroupDto>>(`/api/data-permission-groups/${id}`);
}

export function createDataPermissionGroup(data: { groupName: string; remark?: string }) {
  return apiPost<ApiResponse<number>>('/api/data-permission-groups', data);
}

export function updateDataPermissionGroup(id: number, data: { groupName?: string; remark?: string }) {
  return apiPut<ApiResponse<void>>(`/api/data-permission-groups/${id}`, data);
}

export function deleteDataPermissionGroup(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/data-permission-groups/${id}`);
}

// Factory association
export function getGroupFactories(groupId: number) {
  return apiGet<ApiResponse<AssociatedEntityDto[]>>(`/api/data-permission-groups/${groupId}/factories`);
}

export function saveGroupFactories(groupId: number, ids: number[]) {
  return apiPost<ApiResponse<void>>(`/api/data-permission-groups/${groupId}/factories`, { ids });
}

// Work Center association
export function getGroupWorkCenters(groupId: number) {
  return apiGet<ApiResponse<AssociatedEntityDto[]>>(`/api/data-permission-groups/${groupId}/work-centers`);
}

export function saveGroupWorkCenters(groupId: number, ids: number[]) {
  return apiPost<ApiResponse<void>>(`/api/data-permission-groups/${groupId}/work-centers`, { ids });
}

// Process association
export function getGroupProcesses(groupId: number) {
  return apiGet<ApiResponse<AssociatedEntityDto[]>>(`/api/data-permission-groups/${groupId}/processes`);
}

export function saveGroupProcesses(groupId: number, ids: number[]) {
  return apiPost<ApiResponse<void>>(`/api/data-permission-groups/${groupId}/processes`, { ids });
}
