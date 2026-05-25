/** Ticket visual compartido por cocina y bar para operar pedidos activos. */
import { ArrowRight, ChefHat, Clock, UserRound } from 'lucide-react'
import { ElapsedTimerBadge } from '../../components/orders/ElapsedTimerBadge'
import { StatusBadge } from '../../components/ui/StatusBadge'
import type { Order } from '../../types/models'
import { getNextOrderStatus, orderActionLabel } from '../shared'

/** Props de una tarjeta/ticket de cocina o bar. */
interface KDSCardProps {
  order: Order
  onAdvance: (order: Order) => void
}

/** Renderiza un ticket operativo con mesa, items, timer y accion para avanzar estado. */
export function KDSCard({ order, onAdvance }: KDSCardProps) {
  const nextStatus = getNextOrderStatus(order.status)

  return (
    <article className={`kds-card kds-card-${order.status.toLowerCase()}`}>
      <header>
        <div>
          <span>{order.ticketNumber}</span>
          <h3>{order.tableOrRegister}</h3>
        </div>
        <ElapsedTimerBadge createdAt={order.createdAt} />
      </header>

      <div className="kds-card-meta">
        <StatusBadge status={order.status} />
        <span>
          <UserRound aria-hidden="true" size={14} />
          {order.waiterName}
        </span>
      </div>

      <ul className="kds-items">
        {order.items.map((item) => (
          <li key={item.id}>
            <strong>{item.quantity}x</strong>
            <div>
              <span>{item.productName}</span>
              {item.notes && <p>{item.notes}</p>}
            </div>
            <ChefHat aria-hidden="true" size={16} />
          </li>
        ))}
      </ul>

      <footer>
        <span>
          <Clock aria-hidden="true" size={14} />
          {new Date(order.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
        </span>
        {nextStatus && (
          <button className="primary-button" onClick={() => onAdvance(order)} type="button">
            {orderActionLabel(order.status)}
            <ArrowRight aria-hidden="true" size={16} />
          </button>
        )
        }
      </footer>
    </article>
  )
}
