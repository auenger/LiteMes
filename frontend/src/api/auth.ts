import { apiPost, apiGet } from './http';

export interface LoginResult {
  token: string;
}

export interface UserInfo {
  id: number;
  username: string;
  realName: string;
  roles: string[];
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

export function login(username: string, password: string) {
  return apiPost<ApiResponse<LoginResult>>('/api/auth/login', { username, password });
}

export function getUserInfo() {
  return apiGet<ApiResponse<UserInfo>>('/api/auth/userinfo');
}
