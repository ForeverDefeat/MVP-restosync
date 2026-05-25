/** Endpoints de productos: catalogo, CRUD y disponibilidad. */
import { apiClient, unwrapApiResponse } from './axios'
import type { ApiResponse, ProductFilters, ProductPayload } from '../types/api'
import type { Product } from '../types/models'

/**
 * Encapsula las llamadas del catalogo de productos y disponibilidad.
 */
export const productsApi = {
  /** Lista productos aplicando filtros opcionales de categoria, texto o disponibilidad. */
  async getProducts(filters: ProductFilters = {}) {
    const response = await apiClient.get<ApiResponse<Product[]>>('/products', {
      params: filters,
    })

    return unwrapApiResponse(response)
  },

  /** Obtiene el detalle de un producto especifico. */
  async getProduct(id: number) {
    const response = await apiClient.get<ApiResponse<Product>>(`/products/${id}`)

    return unwrapApiResponse(response)
  },

  /** Crea un producto nuevo en el catalogo. */
  async createProduct(payload: ProductPayload) {
    const response = await apiClient.post<ApiResponse<Product>>('/products', payload)

    return unwrapApiResponse(response)
  },

  /** Actualiza datos editables de un producto existente. */
  async updateProduct(id: number, payload: ProductPayload) {
    const response = await apiClient.put<ApiResponse<Product>>(`/products/${id}`, payload)

    return unwrapApiResponse(response)
  },

  /** Alterna la disponibilidad de un producto para POS y catalogo. */
  async toggleAvailability(id: number) {
    const response = await apiClient.patch<ApiResponse<Product>>(`/products/${id}/availability`)

    return unwrapApiResponse(response)
  },
}
