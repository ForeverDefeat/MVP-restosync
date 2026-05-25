/** Utilidades compartidas por paginas para dinero y transiciones de estados de pedido. */
import type { OrderStatus } from '../types/enums'

/**
 * Formatea montos en soles para tarjetas, tablas e historial.
 */
export const formatMoney = (value: number) => `S/ ${value.toFixed(2)}`

/**
 * Calcula el siguiente estado permitido para el flujo operativo de una comanda.
 */
export const getNextOrderStatus = (status: OrderStatus): OrderStatus | null => {
  if (status === 'PENDIENTE') return 'EN_PREPARACION'
  if (status === 'EN_PREPARACION') return 'LISTO'
  if (status === 'LISTO') return 'ENTREGADO'
  return null
}

/**
 * Devuelve la etiqueta del boton de accion segun el estado actual de la comanda.
 */
export const orderActionLabel = (status: OrderStatus) => {
  if (status === 'PENDIENTE') return 'Iniciar'
  if (status === 'EN_PREPARACION') return 'Marcar listo'
  if (status === 'LISTO') return 'Entregar'
  return 'Actualizar'
}
