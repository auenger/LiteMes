import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * Customer API module.
 */

export interface CustomerMaterialDto {
  id: number;
  customerId: number;
  materialId: number;
  materialCode: string;
  materialName: string;
}

export interface CustomerDto {
  id: number;
  customerCode: string;
  customerName: string;
  status: number;
  type: string;
  shortName: string;
  contactPerson: string;
  phone: string;
  address: string;
  email: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
  materials: CustomerMaterialDto[];
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

export interface CustomerQueryParams {
  customerCode?: string;
  customerName?: string;
  type?: string;
  status?: number;
  page?: number;
  size?: number;
}

export function listCustomers(params?: CustomerQueryParams) {
  return apiGet<ApiResponse<PagedResult<CustomerDto>>>('/api/customers', { params });
}

export function getCustomer(id: number) {
  return apiGet<ApiResponse<CustomerDto>>(`/api/customers/${id}`);
}

export function createCustomer(data: {
  customerCode: string;
  customerName: string;
  type?: string;
  shortName?: string;
  contactPerson?: string;
  phone?: string;
  address?: string;
  email?: string;
}) {
  return apiPost<ApiResponse<number>>('/api/customers', data);
}

export function updateCustomer(id: number, data: {
  customerName?: string;
  type?: string;
  shortName?: string;
  contactPerson?: string;
  phone?: string;
  address?: string;
  email?: string;
}) {
  return apiPut<ApiResponse<void>>(`/api/customers/${id}`, data);
}

export function deleteCustomer(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/customers/${id}`);
}

export function updateCustomerStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/customers/${id}/status?status=${status}`);
}

export function linkMaterials(customerId: number, materialIds: number[]) {
  return apiPost<ApiResponse<void>>(`/api/customers/${customerId}/materials`, materialIds);
}

export function unlinkMaterial(customerId: number, materialId: number) {
  return apiDelete<ApiResponse<void>>(`/api/customers/${customerId}/materials/${materialId}`);
}

export function getCustomerMaterials(customerId: number) {
  return apiGet<ApiResponse<CustomerMaterialDto[]>>(`/api/customers/${customerId}/materials`);
}
