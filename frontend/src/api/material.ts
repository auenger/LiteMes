import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * Material Master API module.
 */

export interface MaterialDto {
  id: number;
  materialCode: string;
  materialName: string;
  status: number;
  basicCategory: string;
  categoryId: number;
  categoryName: string;
  attributeCategory: string;
  uomId: number;
  uomName: string;
  size: number | null;
  length: number | null;
  width: number | null;
  model: string | null;
  specification: string | null;
  thickness: number | null;
  color: string | null;
  tgValue: string | null;
  copperThickness: string | null;
  isCopperContained: boolean | null;
  diameter: number | null;
  bladeLength: number | null;
  totalLength: number | null;
  ext1: string | null;
  ext2: string | null;
  ext3: string | null;
  ext4: string | null;
  ext5: string | null;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface MaterialVersionDto {
  id: number;
  materialId: number;
  materialCode: string;
  materialName: string;
  versionNo: string;
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

export interface MaterialQueryParams {
  materialCode?: string;
  materialName?: string;
  categoryId?: number;
  basicCategory?: string;
  status?: number;
  page?: number;
  size?: number;
}

// Material Master CRUD

export function listMaterials(params?: MaterialQueryParams) {
  return apiGet<ApiResponse<PagedResult<MaterialDto>>>('/api/materials', { params });
}

export function getMaterial(id: number) {
  return apiGet<ApiResponse<MaterialDto>>(`/api/materials/${id}`);
}

export function createMaterial(data: Partial<MaterialDto>) {
  return apiPost<ApiResponse<number>>('/api/materials', data);
}

export function updateMaterial(id: number, data: Partial<MaterialDto>) {
  return apiPut<ApiResponse<void>>(`/api/materials/${id}`, data);
}

export function deleteMaterial(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/materials/${id}`);
}

export function updateMaterialStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/materials/${id}/status?status=${status}`);
}

// Material Version CRUD

export function listMaterialVersions(materialId: number) {
  return apiGet<ApiResponse<MaterialVersionDto[]>>(`/api/materials/${materialId}/versions`);
}

export function createMaterialVersion(materialId: number, data: { versionNo: string }) {
  return apiPost<ApiResponse<number>>(`/api/materials/${materialId}/versions`, data);
}
