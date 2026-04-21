import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * WorkCenter API module.
 */

export interface WorkCenterDto {
  id: number;
  workCenterCode: string;
  name: string;
  factoryId: number;
  factoryName: string;
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

export interface WorkCenterQueryParams {
  workCenterCode?: string;
  name?: string;
  factoryId?: number;
  status?: number;
  page?: number;
  size?: number;
}

export function listWorkCenters(params?: WorkCenterQueryParams) {
  return apiGet<ApiResponse<PagedResult<WorkCenterDto>>>('/api/work-centers', { params });
}

export function getWorkCenter(id: number) {
  return apiGet<ApiResponse<WorkCenterDto>>(`/api/work-centers/${id}`);
}

export function createWorkCenter(data: { workCenterCode: string; name: string; factoryId: number }) {
  return apiPost<ApiResponse<number>>('/api/work-centers', data);
}

export function updateWorkCenter(id: number, data: { name?: string }) {
  return apiPut<ApiResponse<void>>(`/api/work-centers/${id}`, data);
}

export function deleteWorkCenter(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/work-centers/${id}`);
}

export function updateWorkCenterStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/work-centers/${id}/status?status=${status}`);
}
