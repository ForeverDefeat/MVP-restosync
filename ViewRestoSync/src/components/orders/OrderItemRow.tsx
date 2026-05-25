/** Fila reutilizable para mostrar un item de pedido con cantidad, notas y precio. */
import type { OrderItem } from '../../types/models'

/**
 * Props para una fila de item dentro de una comanda.
 */
interface OrderItemRowProps {
  item: OrderItem
  showPrice?: boolean
}

/**
 * Muestra cantidad, nombre, notas y subtotal opcional de un item.
 */
export function OrderItemRow({ item, showPrice = true }: OrderItemRowProps) {
  return (
    <li className="order-item-row">
      <div>
        <strong>
          {item.quantity}x {item.productName}
        </strong>
        {item.notes && <p>{item.notes}</p>}
      </div>
      {showPrice && <span>S/ {item.subtotal.toFixed(2)}</span>}
    </li>
  )
}
