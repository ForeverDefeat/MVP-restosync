/** Barra superior con usuario actual, rol, notificaciones y cierre de sesion. */
import { Bell, LogOut, Menu } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'
import { useNotificationStore } from '../../store/notificationStore'
import { useUiStore } from '../../store/uiStore'
import type { User } from '../../types/models'
import { ROUTES } from '../../router/routes'

/**
 * Props de la barra superior con datos basicos del usuario autenticado.
 */
interface NavbarProps {
  user: User
}

/**
 * Navbar de aplicacion: muestra usuario, contador de toasts, menu mobile y logout.
 */
export function Navbar({ user }: NavbarProps) {
  const navigate = useNavigate()
  const logout = useAuthStore((state) => state.logout)
  const toggleSidebar = useUiStore((state) => state.toggleSidebar)
  const toastCount = useNotificationStore((state) => state.toasts.length)

  /** Cierra la sesion local y lleva al usuario al login. */
  const handleLogout = () => {
    logout()
    navigate(ROUTES.login, { replace: true })
  }

  return (
    <header className="app-navbar">
      <button aria-label="Abrir menu" className="icon-button mobile-only" onClick={toggleSidebar} type="button">
        <Menu aria-hidden="true" size={20} />
      </button>

      <div className="navbar-user">
        <strong>{user.name}</strong>
        <span>{user.role}</span>
      </div>

      <div className="navbar-actions">
        <button aria-label="Notificaciones" className="icon-button has-counter" type="button">
          <Bell aria-hidden="true" size={19} />
          {toastCount > 0 && <span>{toastCount}</span>}
        </button>
        <button aria-label="Cerrar sesion" className="icon-button" onClick={handleLogout} type="button">
          <LogOut aria-hidden="true" size={19} />
        </button>
      </div>
    </header>
  )
}
