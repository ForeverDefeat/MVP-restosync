/** Guard de rutas privadas que valida autenticacion y roles permitidos. */
import { useEffect, useState, type ReactNode } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import type { UserRole } from '../types/enums'
import { ROUTES } from './routes'

/**
 * Props del guard que envuelve rutas privadas y opcionalmente limita roles.
 */
interface ProtectedRouteProps {
  children: ReactNode
  allowedRoles?: UserRole[]
}

/**
 * Protege rutas validando token, expiracion de sesion y rol permitido.
 */
export function ProtectedRoute({ children, allowedRoles }: ProtectedRouteProps) {
  const location = useLocation()
  const currentUser = useAuthStore((state) => state.currentUser)
  const token = useAuthStore((state) => state.token)
  const expiresAt = useAuthStore((state) => state.expiresAt)
  const logout = useAuthStore((state) => state.logout)
  const [now, setNow] = useState(() => Date.now())

  const sessionExpired = Boolean(expiresAt && expiresAt <= now)

  useEffect(() => {
    if (sessionExpired) {
      logout()
    }
  }, [logout, sessionExpired])

  useEffect(() => {
    const timeoutId = window.setTimeout(() => setNow(Date.now()), 1000)
    return () => window.clearTimeout(timeoutId)
  }, [now])

  if (!token || !currentUser || sessionExpired) {
    return <Navigate to={ROUTES.login} replace state={{ from: location }} />
  }

  if (allowedRoles && !allowedRoles.includes(currentUser.role)) {
    return <Navigate to={ROUTES.unauthorized} replace />
  }

  return children
}
