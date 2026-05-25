/** Tarjeta resumen de comanda para vistas del mesero y listados activos. */
import type { ReactNode } from 'react'
import type { Order } from '../../types/models'
import { StatusBadge } from '../ui/StatusBadge'
import { ElapsedTimerBadge } from './ElapsedTimerBadge'
import { OrderItemRow } from './OrderItemRow'

/**
 * Props de tarjeta resumen de comanda reutilizada en vistas de mesero e historial.
 */
interface OrderCardProps {
  actions?: ReactNode
  order: Order
  showPrice?: boolean
}

/**
 * Presenta una comanda con estado, items, total y acciones opcionales.
 */
export function OrderCard({ actions, order, showPrice = true }: OrderCardProps) {
  return (
    <article className="order-card">
      <header>
        <div>
          <span>{order.ticketNumber}</span>
          <h3>{order.tableOrRegister}</h3>
        </div>
        <StatusBadge status={order.status} />
      </header>

      <div className="order-card-meta">
        <span>{order.waiterName}</span>
        <ElapsedTimerBadge createdAt={order.createdAt} />
      </div>

      <ul className="order-items">
        {order.items.map((item) => (
          <OrderItemRow item={item} key={item.id} showPrice={showPrice} />
        ))}
      </ul>

      {showPrice && (
        <footer>
          <span>Total</span>
          <strong>S/ {order.total.toFixed(2)}</strong>
        </footer>
      )}

      {actions && <div className="order-card-actions">{actions}</div>}
    </article>
  )
}
