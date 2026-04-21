import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * Material Category API module.
 */

export interface MaterialCategoryDto {
  id: number;
  categoryCode: string;
  categoryName: string;
  isQualityCategory: boolean;
  parentId: number | null;
  parentName: string | null;
  status: number;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface MaterialCategoryTreeDto {
  id: number;
  categoryCode: string;
  categoryName: string;
  isQualityCategory: boolean;
  parentId: number | null;
  status: number;
  children: MaterialCategoryTreeDto[];
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

export interface MaterialCategoryQueryParams {
  categoryCode?: string;
  categoryName?: string;
  status?: number;
  page?: number;
  size?: number;
}

export function listMaterialCategories(params?: MaterialCategoryQueryParams) {
  return apiGet<ApiResponse<PagedResult<MaterialCategoryDto>>>('/api/material-categories', { params });
}

export function getMaterialCategoryTree() {
  return apiGet<ApiResponse<MaterialCategoryTreeDto[]>>('/api/material-categories/tree');
}

export function getMaterialCategory(id: number) {
  return apiGet<ApiResponse<MaterialCategoryDto>>(`/api/material-categories/${id}`);
}

export function createMaterialCategory(data: {
  categoryCode: string;
  categoryName: string;
  isQualityCategory?: boolean;
  parentId?: number | null;
}) {
  return apiPost<ApiResponse<number>>('/api/material-categories', data);
}

export function updateMaterialCategory(
  id: number,
  data: { categoryName?: string; isQualityCategory?: boolean; parentId?: number | null }
) {
  return apiPut<ApiResponse<void>>(`/api/material-categories/${id}`, data);
}

export function deleteMaterialCategory(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/material-categories/${id}`);
}

export function updateMaterialCategoryStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/material-categories/${id}/status?status=${status}`);
}
