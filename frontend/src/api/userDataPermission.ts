import { apiGet, apiPost, apiDelete } from './http';

/**
 * UserDataPermission API module.
 */

export interface UserDataPermissionVo {
  id: number;
  userId: number;
  username: string;
  realName: string;
  groupId: number | null;
  groupName: string | null;
  factoryCount: number;
  workCenterCount: number;
  processCount: number;
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

export interface UserDataPermissionQueryParams {
  username?: string;
  realName?: string;
  page?: number;
  size?: number;
}

export interface UserPermissionAssociatedEntityDto {
  id: number;
  code: string;
  name: string;
  source: string;
}

export function listUserDataPermissions(params?: UserDataPermissionQueryParams) {
  return apiGet<ApiResponse<PagedResult<UserDataPermissionVo>>>('/api/user-data-permissions', { params });
}

export function batchAssignPermission(userIds: number[], groupId: number) {
  return apiPost<ApiResponse<void>>('/api/user-data-permissions/batch-assign', { userIds, groupId });
}

export function deleteUserDataPermission(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/user-data-permissions/${id}`);
}

// Factory association
export function getUserFactories(permissionId: number) {
  return apiGet<ApiResponse<UserPermissionAssociatedEntityDto[]>>(`/api/user-data-permissions/${permissionId}/factories`);
}

export function directAssignFactories(permissionId: number, ids: number[]) {
  return apiPost<ApiResponse<void>>(`/api/user-data-permissions/${permissionId}/factories`, { ids });
}

// Work Center association
export function getUserWorkCenters(permissionId: number) {
  return apiGet<ApiResponse<UserPermissionAssociatedEntityDto[]>>(`/api/user-data-permissions/${permissionId}/work-centers`);
}

export function directAssignWorkCenters(permissionId: number, ids: number[]) {
  return apiPost<ApiResponse<void>>(`/api/user-data-permissions/${permissionId}/work-centers`, { ids });
}

// Process association
export function getUserProcesses(permissionId: number) {
  return apiGet<ApiResponse<UserPermissionAssociatedEntityDto[]>>(`/api/user-data-permissions/${permissionId}/processes`);
}

export function directAssignProcesses(permissionId: number, ids: number[]) {
  return apiPost<ApiResponse<void>>(`/api/user-data-permissions/${permissionId}/processes`, { ids });
}
