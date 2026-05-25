/** Fabrica centralizada de claves de React Query para invalidaciones consistentes. */
import type { OrderHistoryParams, ProductFilters } from '../types/api'

/**
 * Define claves estables de React Query para centralizar cache e invalidaciones por dominio.
 */
export const queryKeys = {
  orders: {
    all: ['orders'] as const,
    mine: () => [...queryKeys.orders.all, 'mine'] as const,
    activeKitchen: () => [...queryKeys.orders.all, 'active', 'kitchen'] as const,
    activeBar: () => [...queryKeys.orders.all, 'active', 'bar'] as const,
    detail: (id: number) => [...queryKeys.orders.all, 'detail', id] as const,
    history: (params: OrderHistoryParams) => [...queryKeys.orders.all, 'history', params] as const,
  },
  products: {
    all: ['products'] as const,
    list: (filters: ProductFilters) => [...queryKeys.products.all, 'list', filters] as const,
    detail: (id: number) => [...queryKeys.products.all, 'detail', id] as const,
  },
  users: {
    all: ['users'] as const,
    list: () => [...queryKeys.users.all, 'list'] as const,
    detail: (id: number) => [...queryKeys.users.all, 'detail', id] as const,
  },
}
