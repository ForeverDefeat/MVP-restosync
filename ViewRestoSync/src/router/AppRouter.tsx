/** Definicion lazy-loaded de todas las rutas publicas y privadas de la app. */
import { lazy, Suspense } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { ToastViewport } from '../components/ui/Toast'
import { ProtectedRoute } from './ProtectedRoute'
import { ROUTES } from './routes'

const LoginPage = lazy(() => import('../pages/auth/LoginPage'))
const UnauthorizedPage = lazy(() => import('../pages/auth/UnauthorizedPage'))
const POSPage = lazy(() => import('../pages/mesero/POSPage'))
const ActiveOrdersPage = lazy(() => import('../pages/mesero/ActiveOrdersPage'))
const HistoryPage = lazy(() => import('../pages/mesero/HistoryPage'))
const KDSPage = lazy(() => import('../pages/cocina/KDSPage'))
const BarPage = lazy(() => import('../pages/bar/BarPage'))
const DashboardPage = lazy(() => import('../pages/admin/DashboardPage'))
const CatalogPage = lazy(() => import('../pages/admin/CatalogPage'))
const UsersPage = lazy(() => import('../pages/admin/UsersPage'))
const AdminHistoryPage = lazy(() => import('../pages/admin/AdminHistoryPage'))

/**
 * Redirige la raiz al login para obligar a resolver sesion antes de entrar.
 */
function HomeRedirect() {
  return <Navigate to={ROUTES.login} replace />
}

/**
 * Fallback visual mientras React descarga una pagina lazy.
 */
function LoadingRoute() {
  return (
    <main className="route-shell">
      <p>Cargando modulo...</p>
    </main>
  )
}

/**
 * Declara todas las rutas publicas y privadas de la aplicacion.
 */
export function AppRouter() {
  return (
    <BrowserRouter>
      <Suspense fallback={<LoadingRoute />}>
        <Routes>
          <Route path={ROUTES.root} element={<HomeRedirect />} />
          <Route path={ROUTES.login} element={<LoginPage />} />
          <Route path={ROUTES.unauthorized} element={<UnauthorizedPage />} />

          <Route
            path={ROUTES.mesero.pos}
            element={
              <ProtectedRoute allowedRoles={['MESERO', 'ADMINISTRADOR']}>
                <POSPage />
              </ProtectedRoute>
            }
          />
          <Route
            path={ROUTES.mesero.activeOrders}
            element={
              <ProtectedRoute allowedRoles={['MESERO', 'ADMINISTRADOR']}>
                <ActiveOrdersPage />
              </ProtectedRoute>
            }
          />
          <Route
            path={ROUTES.mesero.history}
            element={
              <ProtectedRoute allowedRoles={['MESERO', 'ADMINISTRADOR']}>
                <HistoryPage />
              </ProtectedRoute>
            }
          />

          <Route
            path={ROUTES.cocina.kds}
            element={
              <ProtectedRoute allowedRoles={['COCINERO', 'ADMINISTRADOR']}>
                <KDSPage />
              </ProtectedRoute>
            }
          />

          <Route
            path={ROUTES.bar.board}
            element={
              <ProtectedRoute allowedRoles={['BARTENDER', 'ADMINISTRADOR']}>
                <BarPage />
              </ProtectedRoute>
            }
          />

          <Route
            path={ROUTES.admin.dashboard}
            element={
              <ProtectedRoute allowedRoles={['ADMINISTRADOR']}>
                <DashboardPage />
              </ProtectedRoute>
            }
          />
          <Route
            path={ROUTES.admin.catalog}
            element={
              <ProtectedRoute allowedRoles={['ADMINISTRADOR']}>
                <CatalogPage />
              </ProtectedRoute>
            }
          />
          <Route
            path={ROUTES.admin.users}
            element={
              <ProtectedRoute allowedRoles={['ADMINISTRADOR']}>
                <UsersPage />
              </ProtectedRoute>
            }
          />
          <Route
            path={ROUTES.admin.history}
            element={
              <ProtectedRoute allowedRoles={['ADMINISTRADOR']}>
                <AdminHistoryPage />
              </ProtectedRoute>
            }
          />

          <Route path="*" element={<Navigate to={ROUTES.root} replace />} />
        </Routes>
      </Suspense>
      <ToastViewport />
    </BrowserRouter>
  )
}
