import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * EquipmentType API module.
 */

export interface EquipmentTypeDto {
  id: number;
  typeCode: string;
  typeName: string;
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

export interface EquipmentTypeQueryParams {
  typeCode?: string;
  typeName?: string;
  status?: number;
  page?: number;
  size?: number;
}

// EquipmentType CRUD

export function listEquipmentTypes(params?: EquipmentTypeQueryParams) {
  return apiGet<ApiResponse<PagedResult<EquipmentTypeDto>>>('/api/equipment-types', { params });
}

export function getEquipmentType(id: number) {
  return apiGet<ApiResponse<EquipmentTypeDto>>(`/api/equipment-types/${id}`);
}

export function createEquipmentType(data: { typeCode: string; typeName: string }) {
  return apiPost<ApiResponse<number>>('/api/equipment-types', data);
}

export function updateEquipmentType(id: number, data: { typeName?: string }) {
  return apiPut<ApiResponse<void>>(`/api/equipment-types/${id}`, data);
}

export function deleteEquipmentType(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/equipment-types/${id}`);
}

export function updateEquipmentTypeStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/equipment-types/${id}/status?status=${status}`);
}
