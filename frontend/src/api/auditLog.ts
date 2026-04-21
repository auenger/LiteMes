import { apiGet } from './http';

/**
 * AuditLog API module.
 */

export interface AuditLogDto {
  id: number;
  tableName: string;
  recordId: number;
  action: string;
  oldValue: string | null;
  newValue: string | null;
  changedFields: string | null;
  operatorId: number | null;
  operatorName: string | null;
  createdAt: string;
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

export interface AuditLogQueryParams {
  tableName?: string;
  recordId?: number;
  startTime?: string;
  endTime?: string;
  page?: number;
  size?: number;
}

export function listAuditLogs(params?: AuditLogQueryParams) {
  return apiGet<ApiResponse<PagedResult<AuditLogDto>>>('/api/audit-logs', { params });
}
