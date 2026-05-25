/** Navegacion inferior para mobile reutilizando los items permitidos por rol. */
import { NavLink } from 'react-router-dom'
import type { UserRole } from '../../types/enums'
import { getNavigationItemsForRole } from './navigation'

/**
 * Props para generar tabs inferiores segun el rol actual.
 */
interface BottomTabBarProps {
  role: UserRole
}

/**
 * Navegacion inferior pensada para mobile, limitada a los primeros accesos del rol.
 */
export function BottomTabBar({ role }: BottomTabBarProps) {
  const items = getNavigationItemsForRole(role).slice(0, 4)

  return (
    <nav className="bottom-tabbar" aria-label="Navegacion mobile">
      {items.map((item) => {
        const Icon = item.icon

        return (
          <NavLink
            className={({ isActive }) => `bottom-tab ${isActive ? 'is-active' : ''}`}
            key={item.to}
            to={item.to}
          >
            <Icon aria-hidden="true" size={19} />
            <span>{item.label}</span>
          </NavLink>
        )
      })}
    </nav>
  )
}
