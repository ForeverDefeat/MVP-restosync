/** Dashboard administrativo con KPIs del dia y accesos operativos. */
import { CheckCircle2, ClipboardList, Package, TrendingUp, Users } from 'lucide-react'
import { Link } from 'react-router-dom'
import { PageLayout } from '../../components/layout/PageLayout'
import { EmptyState } from '../../components/ui/EmptyState'
import { useGetOrderHistory } from '../../hooks/useOrders'
import { useGetProducts } from '../../hooks/useProducts'
import { useGetUsers } from '../../hooks/useUsers'
import { ROUTES } from '../../router/routes'
import { formatMoney } from '../shared'

/**
 * Dashboard administrativo con KPIs operativos y accesos rapidos.
 */
export default function DashboardPage() {
  const ordersQuery = useGetOrderHistory({ page: 0, size: 20 })
  const productsQuery = useGetProducts()
  const usersQuery = useGetUsers()
  const orders = ordersQuery.data?.content ?? []
  const revenue = orders.reduce((sum, order) => sum + order.total, 0)
  const ready = orders.filter((order) => order.status === 'LISTO').length

  return (
    <PageLayout>
      <section className="page-heading">
        <div>
          <span>ADMIN</span>
          <h1>Dashboard</h1>
        </div>
      </section>
      <section className="kpi-grid dashboard-kpis">
        <article className="kpi-card kpi-total">
          <header className="kpi-header">
            <span className="kpi-title">Pedidos recientes</span>
            <span className="kpi-icon-wrapper kpi-total">
              <ClipboardList aria-hidden="true" size={22} />
            </span>
          </header>
          <div className="kpi-content">
            <strong className="kpi-value">{orders.length}</strong>
          </div>
          <div className="kpi-progress-bg">
            <span className="kpi-progress-fill" style={{ width: `${Math.min(100, orders.length * 5)}%` }} />
          </div>
          <footer className="kpi-footer">
            <span className="kpi-trend trend-neutral">
              <TrendingUp aria-hidden="true" size={13} />
              Hoy
            </span>
            <span className="kpi-context">ultimos movimientos</span>
          </footer>
        </article>
        <article className="kpi-card kpi-preparing">
          <header className="kpi-header">
            <span className="kpi-title">Ventas</span>
            <span className="kpi-icon-wrapper kpi-preparing">
              <TrendingUp aria-hidden="true" size={22} />
            </span>
          </header>
          <div className="kpi-content">
            <strong className="kpi-value kpi-money">{formatMoney(revenue)}</strong>
          </div>
          <div className="kpi-progress-bg">
            <span className="kpi-progress-fill" style={{ width: `${Math.min(100, revenue / 10)}%` }} />
          </div>
          <footer className="kpi-footer">
            <span className="kpi-trend trend-up">Activo</span>
            <span className="kpi-context">recaudacion visible</span>
          </footer>
        </article>
        <article className="kpi-card kpi-ready">
          <header className="kpi-header">
            <span className="kpi-title">Productos</span>
            <span className="kpi-icon-wrapper kpi-ready">
              <Package aria-hidden="true" size={22} />
            </span>
          </header>
          <div className="kpi-content">
            <strong className="kpi-value">{productsQuery.data?.length ?? 0}</strong>
          </div>
          <div className="kpi-progress-bg">
            <span className="kpi-progress-fill" style={{ width: `${Math.min(100, (productsQuery.data?.length ?? 0) * 6)}%` }} />
          </div>
          <footer className="kpi-footer">
            <span className="kpi-trend trend-up">
              <CheckCircle2 aria-hidden="true" size={13} />
              Catalogo
            </span>
            <span className="kpi-context">platos y bebidas</span>
          </footer>
        </article>
        <article className="kpi-card kpi-pending">
          <header className="kpi-header">
            <span className="kpi-title">Usuarios</span>
            <span className="kpi-icon-wrapper kpi-pending">
              <Users aria-hidden="true" size={22} />
            </span>
          </header>
          <div className="kpi-content">
            <strong className="kpi-value">{usersQuery.data?.length ?? 0}</strong>
          </div>
          <div className="kpi-progress-bg">
            <span className="kpi-progress-fill" style={{ width: `${Math.min(100, (usersQuery.data?.length ?? 0) * 12)}%` }} />
          </div>
          <footer className="kpi-footer">
            <span className="kpi-trend trend-neutral">{ready} listos</span>
            <span className="kpi-context">equipo operativo</span>
          </footer>
        </article>
      </section>
      <section className="quick-grid">
        <Link to={ROUTES.admin.catalog}>Gestionar catalogo</Link>
        <Link to={ROUTES.admin.users}>Gestionar usuarios</Link>
        <Link to={ROUTES.admin.history}>Ver historial</Link>
      </section>
      {ordersQuery.isError && <EmptyState title="No se pudieron cargar las metricas" />}
    </PageLayout>
  )
}
