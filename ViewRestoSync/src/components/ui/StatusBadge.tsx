/** Badge visual para traducir estados de pedido a etiquetas y colores. */
import type { OrderStatus } from '../../types/enums'

/**
 * Traduce estados internos de comanda a etiquetas visibles.
 */
const statusLabels: Record<OrderStatus, string> = {
  PENDIENTE: 'Pendiente',
  EN_PREPARACION: 'En preparacion',
  LISTO: 'Listo',
  ENTREGADO: 'Entregado',
  CANCELADO: 'Cancelado',
}

/**
 * Props del badge visual de estado de comanda.
 */
interface StatusBadgeProps {
  status: OrderStatus
}

/**
 * Muestra el estado de una comanda con clase semantica para colores por estado.
 */
export function StatusBadge({ status }: StatusBadgeProps) {
  return <span className={`status-badge status-${status.toLowerCase()}`}>{statusLabels[status]}</span>
}
