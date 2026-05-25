/** Vista del mesero para dar seguimiento, avanzar o cancelar comandas activas. */
import { useState } from 'react'
import { PageLayout } from '../../components/layout/PageLayout'
import { OrderCard } from '../../components/orders/OrderCard'
import { ConfirmDialog } from '../../components/ui/ConfirmDialog'
import { EmptyState } from '../../components/ui/EmptyState'
import { SkeletonCard } from '../../components/ui/SkeletonCard'
import { useCancelOrder, useGetMyOrders, useUpdateStatus } from '../../hooks/useOrders'
import { useNotificationStore } from '../../store/notificationStore'
import type { Order } from '../../types/models'
import { getNextOrderStatus, orderActionLabel } from '../shared'

/**
 * Vista de mesero para monitorear, avanzar y cancelar comandas activas.
 */
export default function ActiveOrdersPage() {
  const ordersQuery = useGetMyOrders()
  const updateStatus = useUpdateStatus()
  const cancelOrder = useCancelOrder()
  const notify = useNotificationStore((state) => state.add)
  const [cancelTarget, setCancelTarget] = useState<Order | null>(null)

  /** Avanza una comanda al siguiente estado permitido. */
  const advance = async (order: Order) => {
    const nextStatus = getNextOrderStatus(order.status)
    if (!nextStatus) return

    try {
      await updateStatus.mutateAsync({ id: order.id, status: nextStatus })
      notify({ type: 'success', title: 'Estado actualizado' })
    } catch {
      notify({ type: 'error', title: 'No se pudo actualizar el pedido' })
    }
  }

  /** Confirma cancelacion de la comanda seleccionada. */
  const confirmCancel = async () => {
    if (!cancelTarget) return

    try {
      await cancelOrder.mutateAsync({ id: cancelTarget.id, reason: 'Cancelado desde panel de mesero' })
      notify({ type: 'success', title: 'Pedido cancelado' })
      setCancelTarget(null)
    } catch {
      notify({ type: 'error', title: 'No se pudo cancelar el pedido' })
    }
  }

  return (
    <PageLayout>
      <section className="page-heading">
        <div>
          <span>MESERO</span>
          <h1>Comandas activas</h1>
        </div>
      </section>

      {ordersQuery.isLoading && (
        <div className="content-grid">
          <SkeletonCard />
          <SkeletonCard />
        </div>
      )}
      {ordersQuery.isError && <EmptyState title="No se pudieron cargar tus comandas" />}
      {ordersQuery.data?.length === 0 && <EmptyState title="Sin comandas activas" />}
      {ordersQuery.data && ordersQuery.data.length > 0 && (
        <div className="content-grid">
          {ordersQuery.data.map((order) => (
            <OrderCard
              actions={
                <>
                  {order.status === 'PENDIENTE' && (
                    <button className="secondary-button danger-button" onClick={() => setCancelTarget(order)} type="button">
                      Cancelar
                    </button>
                  )}
                  {getNextOrderStatus(order.status) && (
                    <button className="primary-button" onClick={() => advance(order)} type="button">
                      {orderActionLabel(order.status)}
                    </button>
                  )}
                </>
              }
              key={order.id}
              order={order}
            />
          ))}
        </div>
      )}

      <ConfirmDialog
        confirmLabel="Cancelar pedido"
        message="Esta accion cancelara la comanda mientras aun esta pendiente."
        onCancel={() => setCancelTarget(null)}
        onConfirm={confirmCancel}
        open={Boolean(cancelTarget)}
        title="Cancelar comanda"
      />
    </PageLayout>
  )
}
