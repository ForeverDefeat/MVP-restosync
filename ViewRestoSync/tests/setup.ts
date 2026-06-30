import '@testing-library/jest-dom/vitest'
import { afterAll, afterEach, beforeAll, vi } from 'vitest'
import { cleanup } from '@testing-library/react'
import { server } from './testServer'
import { useAuthStore } from '../src/store/authStore'

beforeAll(() => server.listen({ onUnhandledRequest: 'error' }))

afterEach(() => {
  cleanup()
  localStorage.clear()
  useAuthStore.setState({
    currentUser: null,
    token: null,
    expiresAt: null,
    isAuthenticated: false,
    isLoading: false,
  })
  server.resetHandlers()
  vi.restoreAllMocks()
})

afterAll(() => server.close())

vi.mock('../src/hooks/useWebSocket', () => ({
  useWebSocket: vi.fn(),
}))
