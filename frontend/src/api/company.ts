import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * Company API module.
 */

export interface CompanyDto {
  id: number;
  companyCode: string;
  name: string;
  shortCode: string;
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

export interface CompanyQueryParams {
  companyCode?: string;
  name?: string;
  status?: number;
  page?: number;
  size?: number;
}

export function listCompanies(params?: CompanyQueryParams) {
  return apiGet<ApiResponse<PagedResult<CompanyDto>>>('/api/companies', { params });
}

export function getCompany(id: number) {
  return apiGet<ApiResponse<CompanyDto>>(`/api/companies/${id}`);
}

export function createCompany(data: { companyCode: string; name: string; shortCode?: string }) {
  return apiPost<ApiResponse<number>>('/api/companies', data);
}

export function updateCompany(id: number, data: { name?: string; shortCode?: string }) {
  return apiPut<ApiResponse<void>>(`/api/companies/${id}`, data);
}

export function deleteCompany(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/companies/${id}`);
}

export function updateCompanyStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/companies/${id}/status?status=${status}`);
}
