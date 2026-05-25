/** Sidebar de escritorio con navegacion filtrada por rol y accion de colapso. */
import { NavLink } from 'react-router-dom'
import { X } from 'lucide-react'
import { useUiStore } from '../../store/uiStore'
import type { UserRole } from '../../types/enums'
import { getNavigationItemsForRole } from './navigation'

/**
 * Props para renderizar la navegacion lateral segun rol.
 */
interface SidebarProps {
  role: UserRole
}

/**
 * Sidebar principal con links filtrados por rol y cierre automatico en mobile.
 */
export function Sidebar({ role }: SidebarProps) {
  const sidebarOpen = useUiStore((state) => state.sidebarOpen)
  const setSidebarOpen = useUiStore((state) => state.setSidebarOpen)
  const items = getNavigationItemsForRole(role)

  return (
    <>
      <aside className={`app-sidebar ${sidebarOpen ? 'is-open' : ''}`}>
        <div className="sidebar-brand">
          <strong>RestoSync</strong>
          <button
            aria-label="Cerrar menu"
            className="icon-button mobile-only"
            onClick={() => setSidebarOpen(false)}
            type="button"
          >
            <X aria-hidden="true" size={18} />
          </button>
        </div>

        <nav className="sidebar-nav" aria-label="Navegacion principal">
          {items.map((item) => {
            const Icon = item.icon

            return (
              <NavLink
                className={({ isActive }) => `nav-item ${isActive ? 'is-active' : ''}`}
                key={item.to}
                onClick={() => setSidebarOpen(false)}
                to={item.to}
              >
                <Icon aria-hidden="true" size={18} />
                <span>{item.label}</span>
              </NavLink>
            )
          })}
        </nav>
      </aside>

      {sidebarOpen && (
        <button
          aria-label="Cerrar menu"
          className="sidebar-backdrop"
          onClick={() => setSidebarOpen(false)}
          type="button"
        />
      )}
    </>
  )
}
