import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * Factory API module.
 */

export interface FactoryDto {
  id: number;
  factoryCode: string;
  name: string;
  shortName: string;
  companyId: number;
  companyName: string;
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

export interface FactoryQueryParams {
  factoryCode?: string;
  name?: string;
  companyId?: number;
  status?: number;
  page?: number;
  size?: number;
}

export function listFactories(params?: FactoryQueryParams) {
  return apiGet<ApiResponse<PagedResult<FactoryDto>>>('/api/factories', { params });
}

export function getFactory(id: number) {
  return apiGet<ApiResponse<FactoryDto>>(`/api/factories/${id}`);
}

export function createFactory(data: { factoryCode: string; name: string; shortName?: string; companyId: number }) {
  return apiPost<ApiResponse<number>>('/api/factories', data);
}

export function updateFactory(id: number, data: { name?: string; shortName?: string; companyId?: number }) {
  return apiPut<ApiResponse<void>>(`/api/factories/${id}`, data);
}

export function deleteFactory(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/factories/${id}`);
}

export function updateFactoryStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/factories/${id}/status?status=${status}`);
}
