/** Historial paginado de pedidos del mesero autenticado. */
import { useState } from 'react'
import { PageLayout } from '../../components/layout/PageLayout'
import { Pagination } from '../../components/ui/Pagination'
import { StatusBadge } from '../../components/ui/StatusBadge'
import { EmptyState } from '../../components/ui/EmptyState'
import { useGetOrderHistory } from '../../hooks/useOrders'
import { formatMoney } from '../shared'

/**
 * Historial paginado de comandas asociado al rol mesero.
 */
export default function HistoryPage() {
  const [page, setPage] = useState(0)
  const historyQuery = useGetOrderHistory({ page, size: 10 })
  const rows = historyQuery.data?.content ?? []

  return (
    <PageLayout>
      <section className="page-heading">
        <div>
          <span>MESERO</span>
          <h1>Historial</h1>
        </div>
      </section>

      <section className="work-panel">
        {historyQuery.isError && <EmptyState title="No se pudo cargar el historial" />}
        {!historyQuery.isError && rows.length === 0 && <EmptyState title="Aun no hay pedidos historicos" />}
        {rows.length > 0 && (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Ticket</th>
                  <th>Mesa</th>
                  <th>Estado</th>
                  <th>Total</th>
                  <th>Fecha</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((order) => (
                  <tr key={order.id}>
                    <td>{order.ticketNumber}</td>
                    <td>{order.tableOrRegister}</td>
                    <td>
                      <StatusBadge status={order.status} />
                    </td>
                    <td>{formatMoney(order.total)}</td>
                    <td>{new Date(order.createdAt).toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <Pagination page={page} totalPages={historyQuery.data?.totalPages ?? 1} onPageChange={setPage} />
      </section>
    </PageLayout>
  )
}
