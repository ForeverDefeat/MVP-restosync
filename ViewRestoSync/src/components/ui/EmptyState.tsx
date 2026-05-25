/** Estado vacio reutilizable para errores, resultados sin datos y pantallas sin contenido. */
import { Inbox } from 'lucide-react'
import type { ReactNode } from 'react'

/**
 * Props para mensajes de listas vacias o errores sin datos.
 */
interface EmptyStateProps {
  action?: ReactNode
  description?: string
  title: string
}

/**
 * Renderiza una pantalla compacta para ausencia de resultados con accion opcional.
 */
export function EmptyState({ action, description, title }: EmptyStateProps) {
  return (
    <section className="empty-state">
      <Inbox aria-hidden="true" size={34} />
      <h2>{title}</h2>
      {description && <p>{description}</p>}
      {action}
    </section>
  )
}
