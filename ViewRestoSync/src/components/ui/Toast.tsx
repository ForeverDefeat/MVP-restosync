/** Vista de toasts que consume la cola global de notificaciones. */
import { CheckCircle2, Info, TriangleAlert, X, XCircle } from 'lucide-react'
import { useEffect } from 'react'
import { useNotificationStore, type ToastNotification } from '../../store/notificationStore'

/**
 * Relaciona cada tipo de notificacion con su icono.
 */
const toastIcons = {
  success: CheckCircle2,
  error: XCircle,
  info: Info,
  warning: TriangleAlert,
}

/**
 * Renderiza un toast individual y programa su auto-cierre cuando aplica.
 */
function ToastItem({ toast }: { toast: ToastNotification }) {
  const dismiss = useNotificationStore((state) => state.dismiss)
  const Icon = toastIcons[toast.type]

  useEffect(() => {
    if (toast.duration === 0) return

    const timeoutId = window.setTimeout(() => dismiss(toast.id), toast.duration ?? 4500)
    return () => window.clearTimeout(timeoutId)
  }, [dismiss, toast.duration, toast.id])

  return (
    <article className={`toast toast-${toast.type}`} role="status">
      <Icon aria-hidden="true" size={19} />
      <div>
        <strong>{toast.title}</strong>
        {toast.message && <p>{toast.message}</p>}
      </div>
      <button aria-label="Cerrar notificacion" className="icon-button" onClick={() => dismiss(toast.id)} type="button">
        <X aria-hidden="true" size={17} />
      </button>
    </article>
  )
}

/**
 * Contenedor fijo donde se apilan las notificaciones globales.
 */
export function ToastViewport() {
  const toasts = useNotificationStore((state) => state.toasts)

  if (toasts.length === 0) return null

  return (
    <section className="toast-viewport" aria-label="Notificaciones">
      {toasts.map((toast) => (
        <ToastItem key={toast.id} toast={toast} />
      ))}
    </section>
  )
}
