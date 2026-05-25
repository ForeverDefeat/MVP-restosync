/** Endpoints de usuarios: listado, alta, roles y activacion. */
import { apiClient, unwrapApiResponse } from './axios'
import type { ApiResponse, CreateUserRequest, UpdateUserRoleRequest } from '../types/api'
import type { User } from '../types/models'

/**
 * Reune los endpoints administrativos para usuarios, roles y estado activo.
 */
export const usersApi = {
  /** Lista todos los usuarios visibles para administracion. */
  async getUsers() {
    const response = await apiClient.get<ApiResponse<User[]>>('/users')

    return unwrapApiResponse(response)
  },

  /** Obtiene el detalle de un usuario por identificador. */
  async getUser(id: number) {
    const response = await apiClient.get<ApiResponse<User>>(`/users/${id}`)

    return unwrapApiResponse(response)
  },

  /** Registra un usuario nuevo con credenciales y rol inicial. */
  async createUser(payload: CreateUserRequest) {
    const response = await apiClient.post<ApiResponse<User>>('/users', payload)

    return unwrapApiResponse(response)
  },

  /** Cambia el rol operativo o administrativo de un usuario. */
  async updateRole(id: number, role: UpdateUserRoleRequest['role']) {
    const payload: UpdateUserRoleRequest = { role }
    const response = await apiClient.patch<ApiResponse<User>>(`/users/${id}/role`, payload)

    return unwrapApiResponse(response)
  },

  /** Activa o desactiva el acceso de un usuario existente. */
  async toggleActive(id: number) {
    const response = await apiClient.patch<ApiResponse<User>>(`/users/${id}/active`)

    return unwrapApiResponse(response)
  },
}
