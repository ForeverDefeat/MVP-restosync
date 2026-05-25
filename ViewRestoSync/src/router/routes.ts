/** Constantes de rutas, permisos por ruta y redireccion segura por rol. */
import type { UserRole } from '../types/enums'

/**
 * Mapa unico de rutas para evitar strings duplicados entre router, sidebar y redirects.
 */
export const ROUTES = {
  root: '/',
  login: '/login',
  unauthorized: '/unauthorized',
  mesero: {
    pos: '/mesero/pos',
    activeOrders: '/mesero/orders',
    history: '/mesero/history',
  },
  cocina: {
    kds: '/cocina/kds',
  },
  bar: {
    board: '/bar',
  },
  admin: {
    dashboard: '/admin',
    catalog: '/admin/catalog',
    users: '/admin/users',
    history: '/admin/history',
  },
} as const

/**
 * Define la pantalla inicial segura para cada rol despues del login.
 */
export const ROLE_HOME_ROUTE: Record<UserRole, string> = {
  ADMINISTRADOR: ROUTES.admin.dashboard,
  MESERO: ROUTES.mesero.pos,
  COCINERO: ROUTES.cocina.kds,
  BARTENDER: ROUTES.bar.board,
}

/**
 * Lista los prefijos de ruta que cada rol puede abrir directamente.
 */
export const ROLE_ROUTE_PREFIXES: Record<UserRole, string[]> = {
  ADMINISTRADOR: [
    ROUTES.mesero.pos,
    ROUTES.mesero.activeOrders,
    ROUTES.mesero.history,
    ROUTES.cocina.kds,
    ROUTES.bar.board,
    ROUTES.admin.dashboard,
    ROUTES.admin.catalog,
    ROUTES.admin.users,
    ROUTES.admin.history,
  ],
  MESERO: [ROUTES.mesero.pos, ROUTES.mesero.activeOrders, ROUTES.mesero.history],
  COCINERO: [ROUTES.cocina.kds],
  BARTENDER: [ROUTES.bar.board],
}

/**
 * Verifica si un rol puede navegar a un pathname concreto o a una subruta suya.
 */
export const roleCanAccessPath = (role: UserRole, pathname: string) =>
  ROLE_ROUTE_PREFIXES[role].some((route) => pathname === route || pathname.startsWith(`${route}/`))

/**
 * Conserva una redireccion solicitada solo cuando el rol tiene permiso para verla.
 */
export const getSafeRedirectPath = (role: UserRole, requestedPath?: string) => {
  if (requestedPath && roleCanAccessPath(role, requestedPath)) {
    return requestedPath
  }

  return ROLE_HOME_ROUTE[role]
}
