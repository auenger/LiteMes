import { apiGet, apiPost, apiDelete } from './http';

/**
 * DepartmentUser API module.
 */

export interface DepartmentUserDto {
  id: number;
  departmentId: number;
  userId: number;
  username: string;
  realName: string;
  createdBy: string;
  createdAt: string;
}

export interface UserDto {
  id: number;
  username: string;
  realName: string;
  status: number;
  createdBy: string;
  createdAt: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

export interface PagedResult<T> {
  records: T[];
  total: number;
  page: number;
  size: number;
  pages: number;
}

export function listDepartmentUsers(departmentId: number) {
  return apiGet<ApiResponse<DepartmentUserDto[]>>(`/api/departments/${departmentId}/users`);
}

export function assignUsers(departmentId: number, userIds: number[]) {
  return apiPost<ApiResponse<void>>(`/api/departments/${departmentId}/users`, { userIds });
}

export function removeUser(departmentId: number, userId: number) {
  return apiDelete<ApiResponse<void>>(`/api/departments/${departmentId}/users/${userId}`);
}

export function searchUsers(params?: { username?: string; realName?: string; page?: number; size?: number }) {
  return apiGet<ApiResponse<PagedResult<UserDto>>>('/api/departments/users/search', { params });
}
