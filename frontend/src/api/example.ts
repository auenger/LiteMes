import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * Example API module - demonstrates API call pattern.
 */

export interface ExampleDto {
  id: number;
  name: string;
  description: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

export function listExamples() {
  return apiGet<ApiResponse<ExampleDto[]>>('/api/examples');
}

export function getExample(id: number) {
  return apiGet<ApiResponse<ExampleDto>>(`/api/examples/${id}`);
}

export function createExample(data: { name: string; description?: string }) {
  return apiPost<ApiResponse<number>>('/api/examples', data);
}

export function updateExample(id: number, data: { name?: string; description?: string }) {
  return apiPut<ApiResponse<void>>(`/api/examples/${id}`, data);
}

export function deleteExample(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/examples/${id}`);
}
