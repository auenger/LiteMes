import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * Supplier API module.
 */

export interface SupplierMaterialDto {
  id: number;
  supplierId: number;
  materialId: number;
  materialCode: string;
  materialName: string;
}

export interface SupplierDto {
  id: number;
  supplierCode: string;
  supplierName: string;
  status: number;
  shortName: string;
  contactPerson: string;
  phone: string;
  address: string;
  email: string;
  description: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
  materials: SupplierMaterialDto[];
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

export interface SupplierQueryParams {
  supplierCode?: string;
  supplierName?: string;
  status?: number;
  page?: number;
  size?: number;
}

export function listSuppliers(params?: SupplierQueryParams) {
  return apiGet<ApiResponse<PagedResult<SupplierDto>>>('/api/suppliers', { params });
}

export function getSupplier(id: number) {
  return apiGet<ApiResponse<SupplierDto>>(`/api/suppliers/${id}`);
}

export function createSupplier(data: {
  supplierCode: string;
  supplierName: string;
  shortName?: string;
  contactPerson?: string;
  phone?: string;
  address?: string;
  email?: string;
  description?: string;
}) {
  return apiPost<ApiResponse<number>>('/api/suppliers', data);
}

export function updateSupplier(id: number, data: {
  supplierName?: string;
  shortName?: string;
  contactPerson?: string;
  phone?: string;
  address?: string;
  email?: string;
  description?: string;
}) {
  return apiPut<ApiResponse<void>>(`/api/suppliers/${id}`, data);
}

export function deleteSupplier(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/suppliers/${id}`);
}

export function updateSupplierStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/suppliers/${id}/status?status=${status}`);
}

export function linkMaterials(supplierId: number, materialIds: number[]) {
  return apiPost<ApiResponse<void>>(`/api/suppliers/${supplierId}/materials`, materialIds);
}

export function unlinkMaterial(supplierId: number, materialId: number) {
  return apiDelete<ApiResponse<void>>(`/api/suppliers/${supplierId}/materials/${materialId}`);
}

export function getSupplierMaterials(supplierId: number) {
  return apiGet<ApiResponse<SupplierMaterialDto[]>>(`/api/suppliers/${supplierId}/materials`);
}
