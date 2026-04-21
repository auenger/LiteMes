import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * EquipmentModel API module.
 */

export interface EquipmentModelDto {
  id: number;
  modelCode: string;
  modelName: string;
  equipmentTypeId: number;
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

export interface EquipmentModelQueryParams {
  modelCode?: string;
  modelName?: string;
  equipmentTypeId?: number;
  status?: number;
  page?: number;
  size?: number;
}

// EquipmentModel CRUD

export function listEquipmentModels(params?: EquipmentModelQueryParams) {
  return apiGet<ApiResponse<PagedResult<EquipmentModelDto>>>('/api/equipment-models', { params });
}

export function getEquipmentModel(id: number) {
  return apiGet<ApiResponse<EquipmentModelDto>>(`/api/equipment-models/${id}`);
}

export function createEquipmentModel(data: { modelCode: string; modelName: string; equipmentTypeId: number }) {
  return apiPost<ApiResponse<number>>('/api/equipment-models', data);
}

export function updateEquipmentModel(id: number, data: { modelName?: string; equipmentTypeId?: number }) {
  return apiPut<ApiResponse<void>>(`/api/equipment-models/${id}`, data);
}

export function deleteEquipmentModel(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/equipment-models/${id}`);
}

export function updateEquipmentModelStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/equipment-models/${id}/status?status=${status}`);
}
