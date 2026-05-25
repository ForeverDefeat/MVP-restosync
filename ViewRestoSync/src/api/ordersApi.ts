/** Endpoints de pedidos: listados, historial, creacion y cambios de estado/items. */
import { apiClient, unwrapApiResponse } from './axios'
import type {
  ApiResponse,
  CancelOrderRequest,
  CreateOrderRequest,
  EditOrderItemsRequest,
  OrderHistoryParams,
  PaginatedResponse,
  UpdateOrderStatusRequest,
} from '../types/api'
import type { Order } from '../types/models'

/**
 * Agrupa las operaciones HTTP relacionadas con comandas y estados de pedidos.
 */
export const ordersApi = {
  /** Obtiene una comanda puntual por identificador. */
  async getOrder(id: number) {
    const response = await apiClient.get<ApiResponse<Order>>(`/orders/${id}`)

    return unwrapApiResponse(response)
  },

  /** Lista las comandas activas del usuario autenticado o todas si es administrador. */
  async getMyOrders() {
    const response = await apiClient.get<ApiResponse<Order[]>>('/orders/my')

    return unwrapApiResponse(response)
  },

  /** Obtiene pedidos activos que contienen platos para el display de cocina. */
  async getActiveKitchenOrders() {
    const response = await apiClient.get<ApiResponse<Order[]>>('/orders/active/cocina')

    return unwrapApiResponse(response)
  },

  /** Obtiene pedidos activos que contienen bebidas para el display de bar. */
  async getActiveBarOrders() {
    const response = await apiClient.get<ApiResponse<Order[]>>('/orders/active/bar')

    return unwrapApiResponse(response)
  },

  /** Consulta el historial paginado de pedidos usando los filtros enviados. */
  async getHistory(params: OrderHistoryParams = {}) {
    const response = await apiClient.get<ApiResponse<PaginatedResponse<Order>>>('/orders/history', {
      params,
    })

    return unwrapApiResponse(response)
  },

  /** Crea una nueva comanda desde el POS del mesero. */
  async createOrder(payload: CreateOrderRequest) {
    const response = await apiClient.post<ApiResponse<Order>>('/orders', payload)

    return unwrapApiResponse(response)
  },

  /** Avanza o cambia el estado operativo de una comanda. */
  async updateStatus(id: number, newStatus: UpdateOrderStatusRequest['newStatus']) {
    const payload: UpdateOrderStatusRequest = { newStatus }
    const response = await apiClient.patch<ApiResponse<Order>>(`/orders/${id}/status`, payload)

    return unwrapApiResponse(response)
  },

  /** Cancela una comanda registrando el motivo requerido por backend. */
  async cancelOrder(id: number, reason: string) {
    const payload: CancelOrderRequest = { reason }
    const response = await apiClient.delete<ApiResponse<Order>>(`/orders/${id}`, { data: payload })

    return unwrapApiResponse(response)
  },

  /** Reemplaza o actualiza los items de una comanda editable. */
  async editItems(id: number, payload: EditOrderItemsRequest) {
    const response = await apiClient.patch<ApiResponse<Order>>(`/orders/${id}/items`, payload)

    return unwrapApiResponse(response)
  },
}
