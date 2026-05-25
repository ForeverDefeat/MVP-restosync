/** Hooks de React Query para leer y mutar pedidos desde la UI. */
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ordersApi } from '../api/ordersApi'
import type { CreateOrderRequest, EditOrderItemsRequest, OrderHistoryParams } from '../types/api'
import type { OrderStatus } from '../types/enums'
import { queryKeys } from './queryKeys'

/**
 * Consulta una orden individual y solo se habilita cuando el id es valido.
 */
export const useGetOrder = (id: number) =>
  useQuery({
    queryKey: queryKeys.orders.detail(id),
    queryFn: () => ordersApi.getOrder(id),
    enabled: Number.isFinite(id),
  })

/**
 * Consulta las comandas activas del usuario actual para la vista de mesero.
 */
export const useGetMyOrders = () =>
  useQuery({
    queryKey: queryKeys.orders.mine(),
    queryFn: ordersApi.getMyOrders,
  })

/**
 * Mantiene actualizada la bandeja de cocina con pedidos activos de platos.
 */
export const useGetKitchenOrders = () =>
  useQuery({
    queryKey: queryKeys.orders.activeKitchen(),
    queryFn: ordersApi.getActiveKitchenOrders,
    refetchInterval: 30000,
  })

/**
 * Mantiene actualizada la bandeja de bar con pedidos activos de bebidas.
 */
export const useGetBarOrders = () =>
  useQuery({
    queryKey: queryKeys.orders.activeBar(),
    queryFn: ordersApi.getActiveBarOrders,
    refetchInterval: 30000,
  })

/**
 * Consulta el historial paginado de pedidos usando los filtros recibidos.
 */
export const useGetOrderHistory = (params: OrderHistoryParams = {}) =>
  useQuery({
    queryKey: queryKeys.orders.history(params),
    queryFn: () => ordersApi.getHistory(params),
  })

/**
 * Crea comandas desde POS e invalida toda la cache de pedidos al terminar.
 */
export const useCreateOrder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: CreateOrderRequest) => ordersApi.createOrder(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.orders.all }),
  })
}

/**
 * Cambia el estado de una comanda e invalida las vistas dependientes.
 */
export const useUpdateStatus = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, status }: { id: number; status: OrderStatus }) => ordersApi.updateStatus(id, status),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.orders.all }),
  })
}

/**
 * Cancela una comanda con motivo e invalida las listas de pedidos.
 */
export const useCancelOrder = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, reason }: { id: number; reason: string }) => ordersApi.cancelOrder(id, reason),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.orders.all }),
  })
}

/**
 * Edita los items de una comanda existente e invalida datos de pedidos.
 */
export const useEditItems = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: EditOrderItemsRequest }) => ordersApi.editItems(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.orders.all }),
  })
}
