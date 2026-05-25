/** Badge de tiempo transcurrido para pedidos activos. */
import { Clock } from 'lucide-react'

/**
 * Calcula minutos transcurridos desde la fecha de creacion de una comanda.
 */
const getElapsedMinutes = (createdAt: string) => {
  const createdTime = new Date(createdAt).getTime()

  if (Number.isNaN(createdTime)) return 0

  return Math.max(0, Math.floor((Date.now() - createdTime) / 60000))
}

/**
 * Props del badge de tiempo transcurrido.
 */
interface ElapsedTimerBadgeProps {
  createdAt: string
  urgentAfterMinutes?: number
}

/**
 * Muestra un contador compacto y marca urgencia al pasar el umbral configurado.
 */
export function ElapsedTimerBadge({ createdAt, urgentAfterMinutes = 20 }: ElapsedTimerBadgeProps) {
  const elapsedMinutes = getElapsedMinutes(createdAt)
  const hours = Math.floor(elapsedMinutes / 60)
  const minutes = elapsedMinutes % 60
  const label = hours > 0 ? `${hours}h ${minutes}m` : `${minutes}m`
  const isUrgent = elapsedMinutes >= urgentAfterMinutes

  return (
    <span className={`timer-badge ${isUrgent ? 'is-urgent' : ''}`}>
      <Clock aria-hidden="true" size={15} />
      {label}
    </span>
  )
}
