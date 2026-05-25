/** Matriz central de capacidades por rol para navegacion y acciones criticas. */
import type { UserRole } from '../types/enums'

/**
 * Capacidades funcionales que pueden activarse por rol en UI o guards.
 */
export const PERMISSIONS = [
  'createOrder',
  'editOwnOrder',
  'cancelOwnOrder',
  'advanceKitchenOrder',
  'advanceBarOrder',
  'deliverOrder',
  'manageProducts',
  'manageUsers',
  'viewAdminHistory',
] as const

export type Permission = (typeof PERMISSIONS)[number]

/**
 * Matriz central de permisos por rol para no dispersar reglas en componentes.
 */
export const permissionsByRole: Record<UserRole, Permission[]> = {
  ADMINISTRADOR: ['manageProducts', 'manageUsers', 'viewAdminHistory'],
  MESERO: ['createOrder', 'editOwnOrder', 'cancelOwnOrder', 'deliverOrder'],
  COCINERO: ['advanceKitchenOrder'],
  BARTENDER: ['advanceBarOrder'],
}

/**
 * Indica si un rol contiene una capacidad especifica.
 */
export const roleHasPermission = (role: UserRole, permission: Permission) =>
  permissionsByRole[role].includes(permission)
