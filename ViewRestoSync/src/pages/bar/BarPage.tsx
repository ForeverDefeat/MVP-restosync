/** Display operativo de bar para bebidas agrupadas por estado. */
import { PageLayout } from '../../components/layout/PageLayout'
import { KanbanColumn } from '../../components/orders/KanbanColumn'
import { EmptyState } from '../../components/ui/EmptyState'
import { SkeletonCard } from '../../components/ui/SkeletonCard'
import { useGetBarOrders, useUpdateStatus } from '../../hooks/useOrders'
import { useWebSocket } from '../../hooks/useWebSocket'
import { useNotificationStore } from '../../store/notificationStore'
import type { Order } from '../../types/models'
import { KDSCard } from '../cocina/KDSCard'
import { KDSStatsBar } from '../cocina/KDSStatsBar'
import { getNextOrderStatus } from '../shared'

/** Vista principal del bar: reutiliza el flujo KDS filtrado para pedidos de bebidas. */
export default function BarPage() {
  useWebSocket()
  const ordersQuery = useGetBarOrders()
  const updateStatus = useUpdateStatus()
  const notify = useNotificationStore((state) => state.add)
  const orders = ordersQuery.data ?? []
  const pending = orders.filter((order) => order.status === 'PENDIENTE')
  const preparing = orders.filter((order) => order.status === 'EN_PREPARACION')
  const ready = orders.filter((order) => order.status === 'LISTO')

  /** Avanza un pedido de bebida al siguiente estado y muestra error si falla la mutacion. */
  const advance = async (order: Order) => {
    const nextStatus = getNextOrderStatus(order.status)
    if (!nextStatus) return

    try {
      await updateStatus.mutateAsync({ id: order.id, status: nextStatus })
    } catch {
      notify({ type: 'error', title: 'No se pudo cambiar el estado' })
    }
  }

  return (
    <PageLayout>
      <section className="page-heading kds-heading">
        <div>
          <span>BAR</span>
          <h1>Bar Display</h1>
          <p>Pedidos activos de bebidas ordenados por tiempo de espera.</p>
        </div>
      </section>
      <KDSStatsBar orders={orders} station="bar" />
      {ordersQuery.isLoading && (
        <div className="kanban-board">
          <SkeletonCard />
          <SkeletonCard />
          <SkeletonCard />
        </div>
      )}
      {ordersQuery.isError && <EmptyState title="No se pudo cargar bar" />}
      {!ordersQuery.isLoading && !ordersQuery.isError && (
        <div className="kanban-board">
          <KanbanColumn count={pending.length} status="pending" title="Pendiente">
            {pending.length === 0 && <EmptyState title="Sin pendientes" />}
            {pending.map((order) => <KDSCard key={order.id} order={order} onAdvance={advance} />)}
          </KanbanColumn>
          <KanbanColumn count={preparing.length} status="preparing" title="En preparacion">
            {preparing.length === 0 && <EmptyState title="Sin preparacion" />}
            {preparing.map((order) => <KDSCard key={order.id} order={order} onAdvance={advance} />)}
          </KanbanColumn>
          <KanbanColumn count={ready.length} status="ready" title="Listo">
            {ready.length === 0 && <EmptyState title="Sin listos" />}
            {ready.map((order) => <KDSCard key={order.id} order={order} onAdvance={advance} />)}
          </KanbanColumn>
        </div>
      )}
    </PageLayout>
  )
}
