/** Contratos genericos para respuestas HTTP, paginacion y errores de la API. */
import type { OrderStatus, ProductCategory, UserRole } from './enums'
import type { User } from './models'

/**
 * Envelope generico que el backend usa para respuestas exitosas o informativas.
 */
export interface ApiResponse<T> {
  success: boolean
  data?: T
  message?: string
  error?: string
  status?: number
  timestamp: string
}

/**
 * Forma comun de respuestas paginadas en listados historicos.
 */
export interface PaginatedResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

/**
 * Respuesta estandar cuando backend informa un error controlado.
 */
export interface ErrorResponse {
  success: false
  error: string
  message: string
  status: number
  timestamp: string
}

/**
 * Payload recibido tras autenticar o refrescar sesion.
 */
export interface AuthResponse {
  token: string
  tokenType: string
  expiresIn: number
  user: User
}

/**
 * Credenciales enviadas desde el formulario de login.
 */
export interface LoginRequest {
  email: string
  password: string
}

/**
 * Item solicitado al crear o editar una comanda.
 */
export interface OrderItemRequest {
  productId: number
  quantity: number
  notes?: string
}

/**
 * Payload para crear una comanda desde POS.
 */
export interface CreateOrderRequest {
  tableOrRegister: string
  items: OrderItemRequest[]
}

/**
 * Payload para reemplazar items de una comanda editable.
 */
export interface EditOrderItemsRequest {
  items: OrderItemRequest[]
}

/**
 * Payload para cambiar estado operativo de una comanda.
 */
export interface UpdateOrderStatusRequest {
  newStatus: OrderStatus
}

/**
 * Payload usado al cancelar una comanda.
 */
export interface CancelOrderRequest {
  reason: string
}

/**
 * Filtros y paginacion disponibles para historial de pedidos.
 */
export interface OrderHistoryParams {
  startDate?: string
  endDate?: string
  tableOrRegister?: string
  status?: OrderStatus
  page?: number
  size?: number
}

/**
 * Filtros del catalogo usados por POS y administracion.
 */
export interface ProductFilters {
  category?: ProductCategory
  available?: boolean
  search?: string
}

/**
 * Datos editables al crear o actualizar un producto.
 */
export interface ProductPayload {
  name: string
  category: ProductCategory
  price: number
  available: boolean
  estimatedMinutes: number
  imageUrl?: string
}

/**
 * Datos requeridos para registrar un usuario nuevo.
 */
export interface CreateUserRequest {
  name: string
  email: string
  password: string
  role: UserRole
}

/**
 * Payload minimo para actualizar el rol de un usuario.
 */
export interface UpdateUserRoleRequest {
  role: UserRole
}
