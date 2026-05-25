/**
 * Roles soportados por el frontend y alineados con los enums del backend.
 */
/** Enumeraciones del frontend representadas como const unions para mantener tipos estrictos. */
export const USER_ROLES = ['ADMINISTRADOR', 'MESERO', 'COCINERO', 'BARTENDER'] as const
export type UserRole = (typeof USER_ROLES)[number]

/**
 * Estados de comanda usados por POS, KDS, bar e historial.
 */
export const ORDER_STATUSES = [
  'PENDIENTE',
  'EN_PREPARACION',
  'LISTO',
  'ENTREGADO',
  'CANCELADO',
] as const
export type OrderStatus = (typeof ORDER_STATUSES)[number]

/**
 * Categorias de producto que separan platos para cocina y bebidas para bar.
 */
export const PRODUCT_CATEGORIES = ['PLATO', 'BEBIDA'] as const
export type ProductCategory = (typeof PRODUCT_CATEGORIES)[number]
