import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * Uom (Unit of Measure) API module.
 */

export interface UomDto {
  id: number;
  uomCode: string;
  uomName: string;
  status: number;
  precision: number | null;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface UomConversionDto {
  id: number;
  fromUomId: number;
  fromUomCode: string;
  fromUomName: string;
  toUomId: number;
  toUomCode: string;
  toUomName: string;
  conversionRate: number;
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

export interface UomQueryParams {
  uomCode?: string;
  uomName?: string;
  status?: number;
  page?: number;
  size?: number;
}

export interface UomConversionQueryParams {
  fromUom?: string;
  toUom?: string;
  status?: number;
  page?: number;
  size?: number;
}

// Uom CRUD

export function listUoms(params?: UomQueryParams) {
  return apiGet<ApiResponse<PagedResult<UomDto>>>('/api/uoms', { params });
}

export function getUom(id: number) {
  return apiGet<ApiResponse<UomDto>>(`/api/uoms/${id}`);
}

export function createUom(data: { uomCode: string; uomName: string; precision?: number }) {
  return apiPost<ApiResponse<number>>('/api/uoms', data);
}

export function updateUom(id: number, data: { uomName?: string; precision?: number }) {
  return apiPut<ApiResponse<void>>(`/api/uoms/${id}`, data);
}

export function deleteUom(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/uoms/${id}`);
}

export function updateUomStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/uoms/${id}/status?status=${status}`);
}

// UomConversion CRUD

export function listUomConversions(params?: UomConversionQueryParams) {
  return apiGet<ApiResponse<PagedResult<UomConversionDto>>>('/api/uom-conversions', { params });
}

export function getUomConversion(id: number) {
  return apiGet<ApiResponse<UomConversionDto>>(`/api/uom-conversions/${id}`);
}

export function createUomConversion(data: { fromUomId: number; toUomId: number; conversionRate: number }) {
  return apiPost<ApiResponse<number>>('/api/uom-conversions', data);
}

export function updateUomConversion(id: number, data: { fromUomId?: number; toUomId?: number; conversionRate?: number }) {
  return apiPut<ApiResponse<void>>(`/api/uom-conversions/${id}`, data);
}

export function deleteUomConversion(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/uom-conversions/${id}`);
}
