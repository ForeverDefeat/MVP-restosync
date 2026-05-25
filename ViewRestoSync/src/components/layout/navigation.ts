/** Define la navegacion visible por rol para sidebar y barra inferior. */
import {
  BarChart3,
  ClipboardList,
  CookingPot,
  History,
  LayoutDashboard,
  Martini,
  Package,
  ShoppingCart,
  Users,
  type LucideIcon,
} from 'lucide-react'
import type { UserRole } from '../../types/enums'
import { ROUTES } from '../../router/routes'

/**
 * Elemento de navegacion que puede mostrarse en sidebar o tabs mobile.
 */
export interface NavigationItem {
  label: string
  to: string
  icon: LucideIcon
  roles: UserRole[]
}

/**
 * Catalogo completo de enlaces visibles por rol.
 */
export const navigationItems: NavigationItem[] = [
  {
    label: 'POS',
    to: ROUTES.mesero.pos,
    icon: ShoppingCart,
    roles: ['MESERO', 'ADMINISTRADOR'],
  },
  {
    label: 'Activas',
    to: ROUTES.mesero.activeOrders,
    icon: ClipboardList,
    roles: ['MESERO', 'ADMINISTRADOR'],
  },
  {
    label: 'Historial',
    to: ROUTES.mesero.history,
    icon: History,
    roles: ['MESERO', 'ADMINISTRADOR'],
  },
  {
    label: 'Cocina',
    to: ROUTES.cocina.kds,
    icon: CookingPot,
    roles: ['COCINERO', 'ADMINISTRADOR'],
  },
  {
    label: 'Bar',
    to: ROUTES.bar.board,
    icon: Martini,
    roles: ['BARTENDER', 'ADMINISTRADOR'],
  },
  {
    label: 'Dashboard',
    to: ROUTES.admin.dashboard,
    icon: LayoutDashboard,
    roles: ['ADMINISTRADOR'],
  },
  {
    label: 'Catalogo',
    to: ROUTES.admin.catalog,
    icon: Package,
    roles: ['ADMINISTRADOR'],
  },
  {
    label: 'Usuarios',
    to: ROUTES.admin.users,
    icon: Users,
    roles: ['ADMINISTRADOR'],
  },
  {
    label: 'Operaciones',
    to: ROUTES.admin.history,
    icon: BarChart3,
    roles: ['ADMINISTRADOR'],
  },
]

/**
 * Filtra la navegacion para el rol autenticado.
 */
export const getNavigationItemsForRole = (role: UserRole) =>
  navigationItems.filter((item) => item.roles.includes(role))
