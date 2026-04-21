import { apiGet, apiPost, apiPut, apiDelete } from './http';

/**
 * Shift Schedule API module.
 */

export interface ShiftScheduleDto {
  id: number;
  shiftCode: string;
  name: string;
  isDefault: number;
  status: number;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface ShiftDto {
  id: number;
  shiftScheduleId: number;
  shiftCode: string;
  name: string;
  startTime: string;
  endTime: string;
  crossDay: number;
  status: number;
  workHours: number;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface PageDto<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

// Shift Schedule APIs

export function listShiftSchedules(params?: {
  shiftCode?: string;
  name?: string;
  status?: number;
  pageNum?: number;
  pageSize?: number;
}) {
  const query = new URLSearchParams();
  if (params?.shiftCode) query.set('shiftCode', params.shiftCode);
  if (params?.name) query.set('name', params.name);
  if (params?.status !== undefined) query.set('status', String(params.status));
  if (params?.pageNum) query.set('pageNum', String(params.pageNum));
  if (params?.pageSize) query.set('pageSize', String(params.pageSize));
  const qs = query.toString();
  return apiGet<ApiResponse<PageDto<ShiftScheduleDto>>>(`/api/shift-schedules${qs ? '?' + qs : ''}`);
}

export function listAllShiftSchedules() {
  return apiGet<ApiResponse<ShiftScheduleDto[]>>('/api/shift-schedules/all');
}

export function getShiftSchedule(id: number) {
  return apiGet<ApiResponse<ShiftScheduleDto>>(`/api/shift-schedules/${id}`);
}

export function createShiftSchedule(data: {
  shiftCode: string;
  name: string;
  isDefault: number;
}) {
  return apiPost<ApiResponse<number>>('/api/shift-schedules', data);
}

export function updateShiftSchedule(id: number, data: {
  name?: string;
  isDefault?: number;
  status?: number;
}) {
  return apiPut<ApiResponse<void>>(`/api/shift-schedules/${id}`, data);
}

export function deleteShiftSchedule(id: number) {
  return apiDelete<ApiResponse<void>>(`/api/shift-schedules/${id}`);
}

export function updateShiftScheduleStatus(id: number, status: number) {
  return apiPut<ApiResponse<void>>(`/api/shift-schedules/${id}/status?status=${status}`);
}

// Shift APIs

export function listShifts(scheduleId: number) {
  return apiGet<ApiResponse<ShiftDto[]>>(`/api/shift-schedules/${scheduleId}/shifts`);
}

export function getShift(scheduleId: number, id: number) {
  return apiGet<ApiResponse<ShiftDto>>(`/api/shift-schedules/${scheduleId}/shifts/${id}`);
}

export function createShift(scheduleId: number, data: {
  shiftCode: string;
  name: string;
  startTime: string;
  endTime: string;
  crossDay?: number;
}) {
  return apiPost<ApiResponse<number>>(`/api/shift-schedules/${scheduleId}/shifts`, data);
}

export function updateShift(scheduleId: number, id: number, data: {
  name?: string;
  startTime?: string;
  endTime?: string;
  crossDay?: number;
  status?: number;
}) {
  return apiPut<ApiResponse<void>>(`/api/shift-schedules/${scheduleId}/shifts/${id}`, data);
}

export function deleteShift(scheduleId: number, id: number) {
  return apiDelete<ApiResponse<void>>(`/api/shift-schedules/${scheduleId}/shifts/${id}`);
}
