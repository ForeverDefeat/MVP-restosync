/** Store global para la cola de toasts y mensajes temporales. */
import { create } from 'zustand'

/**
 * Tipos visuales soportados por los toasts globales.
 */
export type NotificationType = 'success' | 'error' | 'info' | 'warning'

/**
 * Modelo de una notificacion en cola para el viewport de toasts.
 */
export interface ToastNotification {
  id: string
  type: NotificationType
  title: string
  message?: string
  duration?: number
  createdAt: number
}

/**
 * Estado y acciones para administrar la cola de notificaciones.
 */
interface NotificationState {
  toasts: ToastNotification[]
  add: (toast: Omit<ToastNotification, 'id' | 'createdAt'>) => string
  dismiss: (id: string) => void
  clear: () => void
}

/**
 * Store global de toasts con acciones para agregar, cerrar o limpiar notificaciones.
 */
export const useNotificationStore = create<NotificationState>((set) => ({
  toasts: [],

  /** Agrega un toast y devuelve su id para poder cerrarlo manualmente. */
  add(toast) {
    const id = crypto.randomUUID()

    set((state) => ({
      toasts: [...state.toasts, { ...toast, id, createdAt: Date.now() }],
    }))

    return id
  },

  /** Elimina un toast especifico de la cola. */
  dismiss(id) {
    set((state) => ({
      toasts: state.toasts.filter((toast) => toast.id !== id),
    }))
  },

  /** Limpia todas las notificaciones visibles. */
  clear() {
    set({ toasts: [] })
  },
}))
