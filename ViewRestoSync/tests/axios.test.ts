import { http, HttpResponse } from 'msw'
import { describe, expect, it, vi } from 'vitest'
import { apiClient } from '../src/api/axios'
import { useAuthStore } from '../src/store/authStore'
import { adminUser, server } from './testServer'

describe('apiClient', () => {
  it('agrega Authorization cuando existe token persistido', async () => {
    let authorization: string | null = null
    useAuthStore.getState().setSession(adminUser, 'admin-token', 3600)
    server.use(
      http.get('http://localhost:8080/api/protected', ({ request }) => {
        authorization = request.headers.get('Authorization')
        return HttpResponse.json({ success: true, data: {}, timestamp: '2026-01-01T00:00:00Z' })
      }),
    )

    await apiClient.get('/protected')

    expect(authorization).toBe('Bearer admin-token')
  })

  it('limpia sesion y redirige al login en 401', async () => {
    useAuthStore.getState().setSession(adminUser, 'admin-token', 3600)
    const assign = vi.fn()
    Object.defineProperty(window, 'location', {
      value: { ...window.location, pathname: '/admin', assign },
      writable: true,
    })
    server.use(
      http.get('http://localhost:8080/api/protected', () =>
        HttpResponse.json({ success: false, error: 'AUTHENTICATION_ERROR', status: 401 }, { status: 401 }),
      ),
    )

    await expect(apiClient.get('/protected')).rejects.toBeTruthy()

    expect(useAuthStore.getState().token).toBeNull()
    expect(useAuthStore.getState().isAuthenticated).toBe(false)
    expect(assign).toHaveBeenCalledWith('/login')
  })
})
