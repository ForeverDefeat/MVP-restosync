/** Hooks de React Query para catalogo, creacion, edicion y disponibilidad de productos. */
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { productsApi } from '../api/productsApi'
import type { ProductFilters, ProductPayload } from '../types/api'
import { queryKeys } from './queryKeys'

/**
 * Consulta el catalogo aplicando filtros de categoria, disponibilidad o busqueda.
 */
export const useGetProducts = (filters: ProductFilters = {}) =>
  useQuery({
    queryKey: queryKeys.products.list(filters),
    queryFn: () => productsApi.getProducts(filters),
  })

/**
 * Crea productos e invalida el catalogo cacheado.
 */
export const useCreateProduct = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: ProductPayload) => productsApi.createProduct(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.products.all }),
  })
}

/**
 * Actualiza productos existentes e invalida todas las consultas del catalogo.
 */
export const useUpdateProduct = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: ProductPayload }) => productsApi.updateProduct(id, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.products.all }),
  })
}

/**
 * Alterna disponibilidad de un producto e invalida las vistas que lo consumen.
 */
export const useToggleAvailability = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => productsApi.toggleAvailability(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.products.all }),
  })
}
