import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * EquipmentLedger API module.
 */

export interface EquipmentLedgerDto {
  id: number;
  equipmentCode: string;
  equipmentName: string;
  equipmentModelId: number;
  modelCode: string;
  modelName: string;
  equipmentTypeId: number;
  typeCode: string;
  typeName: string;
  runningStatus: string;
  manageStatus: string;
  factoryId: number;
  factoryCode: string;
  factoryName: string;
  manufacturer: string;
  commissioningDate: string;
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

export interface EquipmentLedgerQueryParams {
  equipmentCode?: string;
  equipmentName?: string;
  equipmentTypeId?: number;
  equipmentModelId?: number;
  runningStatus?: string;
  manageStatus?: string;
  factoryId?: number;
  status?: number;
  page?: number;
  size?: number;
}

// EquipmentLedger CRUD

export function listEquipmentLedgers(params?: EquipmentLedgerQueryParams) {
  return apiGet<ApiResponse<PagedResult<EquipmentLedgerDto>>>('/api/equipment-ledger', { params });
}

export function getEquipmentLedger(id: number) {
  return apiGet<ApiResponse<EquipmentLedgerDto>>(`/api/equipment-ledger/${id}`);
}

export function createEquipmentLedger(data: {
  equipmentCode: string;
  equipmentName: string;
  equipmentModelId: number;
  runningStatus: string;
  manageStatus: string;
  factoryId: number;
  manufacturer?: string;
  commissioningDate: string;
}) {
  return apiPost<ApiResponse<number>>('/api/equipment-ledger', data);
}

export function updateEquipmentLedger(id: number, data: {
  equipmentName?: string;
  equipmentModelId?: number;
  runningStatus?: string;
  manageStatus?: string;
  factoryId?: number;
  manufacturer?: string;
  commissioningDate?: string;
}) {
  return apiPut<ApiResponse<void>>(`/api/equipment-ledger/${id}`, data);
}

export function deleteEquipmentLedger(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/equipment-ledger/${id}`);
}

export function updateEquipmentLedgerStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/equipment-ledger/${id}/status?status=${status}`);
}
