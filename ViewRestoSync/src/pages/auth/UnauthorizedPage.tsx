/** Pantalla de acceso denegado para rutas privadas sin permisos suficientes. */
import { Link } from 'react-router-dom'
import { ROUTES } from '../../router/routes'

/** Pantalla 403 para usuarios autenticados que intentan abrir una ruta sin permisos. */
export default function UnauthorizedPage() {
  return (
    <main className="route-shell">
      <section className="stub-panel">
        <span>403</span>
        <h1>Acceso no autorizado</h1>
        <p>Tu rol no tiene permisos para abrir esta seccion.</p>
        <Link className="text-link" to={ROUTES.root}>
          Volver al inicio
        </Link>
      </section>
    </main>
  )
}
