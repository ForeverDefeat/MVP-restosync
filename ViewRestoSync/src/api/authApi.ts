/** Endpoints de autenticacion usados por el store de sesion. */
import { apiClient, unwrapApiResponse } from './axios'
import type { ApiResponse, AuthResponse, LoginRequest } from '../types/api'

/**
 * Expone los endpoints de autenticacion y devuelve modelos listos para el store de sesion.
 */
export const authApi = {
  /** Inicia sesion con email/password y recibe token, usuario y vencimiento. */
  async login(email: string, password: string) {
    const response = await apiClient.post<ApiResponse<AuthResponse>, { data: ApiResponse<AuthResponse> }, LoginRequest>(
      '/auth/login',
      { email, password },
    )

    return unwrapApiResponse(response)
  },

  /** Solicita al backend renovar una sesion a partir de un token existente. */
  async refresh(token: string) {
    const response = await apiClient.post<ApiResponse<AuthResponse>>('/auth/refresh', { token })

    return unwrapApiResponse(response)
  },
}
