/** Hooks de React Query para gestion de usuarios y cambios de rol/estado. */
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { usersApi } from '../api/usersApi'
import type { CreateUserRequest } from '../types/api'
import type { UserRole } from '../types/enums'
import { queryKeys } from './queryKeys'

/**
 * Consulta los usuarios administrables del sistema.
 */
export const useGetUsers = () =>
  useQuery({
    queryKey: queryKeys.users.list(),
    queryFn: usersApi.getUsers,
  })

/**
 * Crea usuarios nuevos e invalida la cache de usuarios.
 */
export const useCreateUser = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: CreateUserRequest) => usersApi.createUser(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.users.all }),
  })
}

/**
 * Cambia el rol de un usuario e invalida las vistas administrativas.
 */
export const useUpdateRole = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, role }: { id: number; role: UserRole }) => usersApi.updateRole(id, role),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.users.all }),
  })
}

/**
 * Activa o desactiva usuarios e invalida el listado.
 */
export const useToggleActive = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => usersApi.toggleActive(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.users.all }),
  })
}
