import { http, HttpResponse } from 'msw'
import { setupServer } from 'msw/node'
import type { AuthResponse } from '../src/types/api'
import type { Order, Product, User } from '../src/types/models'

export const adminUser: User = {
  id: 1,
  name: 'Admin Test',
  email: 'admin@restosync.com',
  role: 'ADMINISTRADOR',
  active: true,
  createdAt: '2026-01-01T00:00:00Z',
}

export const waiterUser: User = {
  id: 2,
  name: 'Mesero Test',
  email: 'james@restosync.com',
  role: 'MESERO',
  active: true,
  createdAt: '2026-01-01T00:00:00Z',
}

export const plateProduct: Product = {
  id: 10,
  name: 'Lomo saltado',
  category: 'PLATO',
  price: 32,
  available: true,
  estimatedMinutes: 15,
  imageUrl: null,
  createdAt: '2026-01-01T00:00:00Z',
  updatedAt: '2026-01-01T00:00:00Z',
}

export const drinkProduct: Product = {
  id: 11,
  name: 'Chicha morada',
  category: 'BEBIDA',
  price: 8,
  available: true,
  estimatedMinutes: 3,
  imageUrl: null,
  createdAt: '2026-01-01T00:00:00Z',
  updatedAt: '2026-01-01T00:00:00Z',
}

export const activeOrder: Order = {
  id: 100,
  ticketNumber: '#1001',
  tableOrRegister: 'Mesa 3',
  status: 'PENDIENTE',
  total: 32,
  waiterId: waiterUser.id,
  waiterName: waiterUser.name,
  cancellationReason: null,
  items: [
    {
      id: 1000,
      productId: plateProduct.id,
      productName: plateProduct.name,
      category: 'PLATO',
      quantity: 1,
      unitPrice: plateProduct.price,
      subtotal: plateProduct.price,
      notes: null,
    },
  ],
  createdAt: '2026-01-01T00:00:00Z',
  updatedAt: '2026-01-01T00:00:00Z',
}

const ok = <T>(data: T) =>
  HttpResponse.json({
    success: true,
    data,
    timestamp: '2026-01-01T00:00:00Z',
  })

export const handlers = [
  http.post('http://localhost:8080/api/auth/login', async ({ request }) => {
    const body = (await request.json()) as { email: string; password: string }
    if (body.password !== '123456') {
      return HttpResponse.json(
        { success: false, error: 'INVALID_CREDENTIALS', message: 'Email o contrasena incorrectos', status: 401 },
        { status: 401 },
      )
    }

    const user = body.email.startsWith('admin') ? adminUser : waiterUser
    const auth: AuthResponse = {
      token: `${user.role.toLowerCase()}-token`,
      tokenType: 'Bearer',
      expiresIn: 86400,
      user,
    }

    return ok(auth)
  }),
  http.get('http://localhost:8080/api/products', () => ok([plateProduct, drinkProduct])),
  http.post('http://localhost:8080/api/orders', () => ok({ ...activeOrder, id: 101 })),
  http.get('http://localhost:8080/api/orders/my', () => ok([activeOrder])),
  http.get('http://localhost:8080/api/orders/active/cocina', () => ok([activeOrder])),
  http.get('http://localhost:8080/api/orders/active/bar', () => ok([])),
  http.patch('http://localhost:8080/api/orders/:id/status', () => ok({ ...activeOrder, status: 'EN_PREPARACION' })),
]

export const server = setupServer(...handlers)
