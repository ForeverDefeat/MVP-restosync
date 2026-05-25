/** Modelos de dominio que consume la UI despues de mapear las respuestas del backend. */
import type { OrderStatus, ProductCategory, UserRole } from './enums'

/**
 * Usuario autenticable o administrable dentro del sistema.
 */
export interface User {
  id: number
  name: string
  email: string
  role: UserRole
  active: boolean
  createdAt: string
}

/**
 * Producto del catalogo que puede venderse como plato o bebida.
 */
export interface Product {
  id: number
  name: string
  category: ProductCategory
  price: number
  available: boolean
  estimatedMinutes: number
  imageUrl?: string | null
  createdAt: string
  updatedAt: string
}

/**
 * Linea individual de una comanda con snapshot de nombre, precio y notas.
 */
export interface OrderItem {
  id: number
  productId: number
  productName: string
  category: ProductCategory
  quantity: number
  unitPrice: number
  subtotal: number
  notes?: string | null
}

/**
 * Comanda completa que alimenta POS, KDS, bar e historial.
 */
export interface Order {
  id: number
  ticketNumber: string
  tableOrRegister: string
  status: OrderStatus
  total: number
  waiterId: number
  waiterName: string
  cancellationReason?: string | null
  items: OrderItem[]
  createdAt: string
  updatedAt: string
}

/**
 * Registro de auditoria para acciones relevantes ejecutadas en el sistema.
 */
export interface AuditLog {
  id: number
  action: string
  userId: number
  userName: string
  orderId?: number | null
  details: Record<string, unknown>
  createdAt: string
}
