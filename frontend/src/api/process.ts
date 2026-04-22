import { apiGet, apiPost, apiPut, apiDelete } from './http';

export interface ProcessDto {
  id: number;
  processCode: string;
  name: string;
  workCenterId: number;
  workCenterName: string;
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

export interface ProcessQueryParams {
  processCode?: string;
  name?: string;
  workCenterId?: number;
  factoryId?: number;
  status?: number;
  page?: number;
  size?: number;
}

export function listProcesses(params?: ProcessQueryParams) {
  return apiGet<ApiResponse<PagedResult<ProcessDto>>>('/api/processes', { params });
}

export function getProcess(id: number) {
  return apiGet<ApiResponse<ProcessDto>>(`/api/processes/${id}`);
}

export function createProcess(data: { processCode: string; name: string; workCenterId: number }) {
  return apiPost<ApiResponse<number>>('/api/processes', data);
}

export function updateProcess(id: number, data: { name?: string }) {
  return apiPut<ApiResponse<void>>(`/api/processes/${id}`, data);
}

export function deleteProcess(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/processes/${id}`);
}

export function updateProcessStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/processes/${id}/status?status=${status}`);
}
