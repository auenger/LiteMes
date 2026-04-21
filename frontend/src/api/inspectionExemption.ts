import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * InspectionExemption API module.
 */

export interface InspectionExemptionDto {
  id: number;
  materialId: number;
  materialCode: string;
  materialName: string;
  supplierId: number | null;
  supplierCode: string | null;
  supplierName: string | null;
  status: number;
  validFrom: string | null;
  validTo: string | null;
  expired: boolean;
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

export interface InspectionExemptionQueryParams {
  materialId?: number;
  supplierId?: number;
  status?: number;
  page?: number;
  size?: number;
}

export function listInspectionExemptions(params?: InspectionExemptionQueryParams) {
  return apiGet<ApiResponse<PagedResult<InspectionExemptionDto>>>('/api/inspection-exemptions', { params });
}

export function getInspectionExemption(id: number) {
  return apiGet<ApiResponse<InspectionExemptionDto>>(`/api/inspection-exemptions/${id}`);
}

export function createInspectionExemption(data: {
  materialId: number;
  supplierId?: number | null;
  validFrom?: string | null;
  validTo?: string | null;
}) {
  return apiPost<ApiResponse<number>>('/api/inspection-exemptions', data);
}

export function updateInspectionExemption(id: number, data: {
  materialId?: number;
  supplierId?: number | null;
  validFrom?: string | null;
  validTo?: string | null;
}) {
  return apiPut<ApiResponse<void>>(`/api/inspection-exemptions/${id}`, data);
}

export function deleteInspectionExemption(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/inspection-exemptions/${id}`);
}

export function updateInspectionExemptionStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/inspection-exemptions/${id}/status?status=${status}`);
}

export function scanExpiredRules() {
  return apiPost<ApiResponse<number>>('/api/inspection-exemptions/scan-expired');
}
