/** KPIs operativos compartidos por displays de cocina y bar. */
import { ArrowDown, ArrowUp, Clock3, Flame, Gauge, Timer, Utensils } from 'lucide-react'
import { useState } from 'react'
import type { Order } from '../../types/models'

/** Datos requeridos para calcular KPIs de una estacion operativa. */
interface KDSStatsBarProps {
  orders: Order[]
  station: 'cocina' | 'bar'
}

/** Barra de KPIs para cocina/bar: cola, preparacion, retrasos y carga actual. */
export function KDSStatsBar({ orders, station }: KDSStatsBarProps) {
  const [now] = useState(() => Date.now())
  const pending = orders.filter((order) => order.status === 'PENDIENTE').length
  const preparing = orders.filter((order) => order.status === 'EN_PREPARACION').length
  const delayed = orders.filter((order) => now - new Date(order.createdAt).getTime() > 20 * 60000).length
  const averageMinutes =
    orders.length === 0
      ? 0
      : Math.round(
          orders.reduce((total, order) => total + Math.max(0, now - new Date(order.createdAt).getTime()), 0) /
            orders.length /
            60000,
        )

  return (
    <section className="kpi-grid kds-stats-bar">
      <article className="kpi-card kpi-pending">
        <header className="kpi-header">
          <span className="kpi-title">Tickets Pendientes</span>
          <span className="kpi-icon-wrapper kpi-pending">
            <Timer aria-hidden="true" size={21} />
          </span>
        </header>
        <div className="kpi-content">
          <strong className="kpi-value">{pending}</strong>
        </div>
        <div className="kpi-progress-bg">
          <span className="kpi-progress-fill" style={{ width: `${Math.min(100, pending * 12)}%` }} />
        </div>
        <footer className="kpi-footer">
          <span className="kpi-trend trend-down">
            <ArrowDown aria-hidden="true" size={13} />
            Cola
          </span>
          <span className="kpi-context">por iniciar</span>
        </footer>
      </article>
      <article className="kpi-card kpi-preparing">
        <header className="kpi-header">
          <span className="kpi-title">{station === 'cocina' ? 'Platos en Preparacion' : 'Bebidas en Preparacion'}</span>
          <span className="kpi-icon-wrapper kpi-preparing">
            <Flame aria-hidden="true" size={21} />
          </span>
        </header>
        <div className="kpi-content">
          <strong className="kpi-value">{preparing}</strong>
        </div>
        <div className="kpi-progress-bg">
          <span className="kpi-progress-fill" style={{ width: `${Math.min(100, preparing * 10)}%` }} />
        </div>
        <footer className="kpi-footer">
          <span className="kpi-trend trend-neutral">Capacidad</span>
          <span className="kpi-context">{orders.length === 0 ? 'sin carga' : `${Math.min(100, Math.round((preparing / orders.length) * 100))}% activa`}</span>
        </footer>
      </article>
      <article className="kpi-card kpi-total">
        <header className="kpi-header">
          <span className="kpi-title">Tiempo Promedio</span>
          <span className="kpi-icon-wrapper kpi-total">
            <Gauge aria-hidden="true" size={21} />
          </span>
        </header>
        <div className="kpi-content">
          <strong className="kpi-value">{averageMinutes}m</strong>
        </div>
        <div className="kpi-progress-bg">
          <span className="kpi-progress-fill" style={{ width: `${Math.min(100, (averageMinutes / 25) * 100)}%` }} />
        </div>
        <footer className="kpi-footer">
          <span className="kpi-trend trend-up">
            <ArrowUp aria-hidden="true" size={13} />
            Meta
          </span>
          <span className="kpi-context">objetivo 20m</span>
        </footer>
      </article>
      <article className="kpi-card kpi-delayed">
        <header className="kpi-header">
          <span className="kpi-title">Tickets Retrasados</span>
          <span className="kpi-icon-wrapper kpi-delayed">
            <Clock3 aria-hidden="true" size={21} />
          </span>
        </header>
        <div className="kpi-content">
          <strong className="kpi-value">{delayed}</strong>
        </div>
        <div className="kpi-progress-bg">
          <span className="kpi-progress-fill" style={{ width: `${Math.min(100, delayed * 20)}%` }} />
        </div>
        <footer className="kpi-footer">
          <span className="kpi-trend trend-down">Urgente</span>
          <span className="kpi-context">{'> 20 minutos'}</span>
        </footer>
      </article>
      <article className="kpi-card kpi-ready">
        <header className="kpi-header">
          <span className="kpi-title">Estacion</span>
          <span className="kpi-icon-wrapper kpi-ready">
            <Utensils aria-hidden="true" size={21} />
          </span>
        </header>
        <div className="kpi-content">
          <strong className="kpi-value kpi-label">{station === 'cocina' ? 'Platos' : 'Bebidas'}</strong>
        </div>
        <div className="kpi-progress-bg">
          <span className="kpi-progress-fill" style={{ width: '100%' }} />
        </div>
        <footer className="kpi-footer">
          <span className="kpi-trend trend-up">Online</span>
          <span className="kpi-context">{orders.length} tickets</span>
        </footer>
      </article>
    </section>
  )
}
