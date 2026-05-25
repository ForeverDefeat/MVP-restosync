/** Columna visual del tablero KDS/Bar para agrupar tickets por estado. */
import type { ReactNode } from 'react'

/**
 * Props de una columna Kanban para agrupar pedidos por estado.
 */
interface KanbanColumnProps {
  actions?: ReactNode
  children: ReactNode
  count: number
  status?: 'pending' | 'preparing' | 'ready'
  title: string
}

/**
 * Columna visual del KDS/Bar con color de estado, contador y lista de tarjetas.
 */
export function KanbanColumn({ actions, children, count, status = 'pending', title }: KanbanColumnProps) {
  return (
    <section className={`kanban-column status-${status}`}>
      <header>
        <div>
          <span className="kanban-status-dot" aria-hidden="true" />
          <h2>{title}</h2>
          <span className="kanban-count">{count}</span>
        </div>
        {actions}
      </header>
      <div className="kanban-list">{children}</div>
    </section>
  )
}
