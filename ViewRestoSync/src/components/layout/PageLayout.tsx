/** Layout principal que compone sidebar, navbar, bottom tabs y area de contenido. */
import type { ReactNode } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'
import { ROUTES } from '../../router/routes'
import { BottomTabBar } from './BottomTabBar'
import { Navbar } from './Navbar'
import { Sidebar } from './Sidebar'

/**
 * Props del layout comun que envuelve todas las paginas privadas.
 */
interface PageLayoutProps {
  children: ReactNode
}

/**
 * Layout autenticado: sidebar, navbar, contenido animado y tabs mobile.
 */
export function PageLayout({ children }: PageLayoutProps) {
  const location = useLocation()
  const currentUser = useAuthStore((state) => state.currentUser)

  if (!currentUser) {
    return <Navigate to={ROUTES.login} replace />
  }

  const theme = currentUser.role === 'ADMINISTRADOR' ? 'admin' : 'operations'

  return (
    <div className="app-layout" data-theme={theme}>
      <Sidebar role={currentUser.role} />
      <div className="app-frame">
        <Navbar user={currentUser} />
        <main className="app-main">
          <div className="screen-transition" key={location.pathname}>
            {children}
          </div>
        </main>
      </div>
      <BottomTabBar role={currentUser.role} />
    </div>
  )
}
