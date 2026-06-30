import { describe, expect, it } from 'vitest'
import { useAuthStore } from '../src/store/authStore'
import { adminUser } from './testServer'

describe('authStore', () => {
  it('guarda sesion autenticada', () => {
    useAuthStore.getState().setSession(adminUser, 'admin-token', 3600)

    const state = useAuthStore.getState()
    expect(state.isAuthenticated).toBe(true)
    expect(state.currentUser?.role).toBe('ADMINISTRADOR')
    expect(state.token).toBe('admin-token')
    expect(state.expiresAt).toBeGreaterThan(Date.now())
  })

  it('limpia sesion al cerrar', () => {
    useAuthStore.getState().setSession(adminUser, 'admin-token', 3600)

    useAuthStore.getState().logout()

    expect(useAuthStore.getState().isAuthenticated).toBe(false)
    expect(useAuthStore.getState().currentUser).toBeNull()
    expect(useAuthStore.getState().token).toBeNull()
  })
})
