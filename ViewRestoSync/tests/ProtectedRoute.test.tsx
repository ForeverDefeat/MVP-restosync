import { screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { describe, expect, it } from 'vitest'
import { ProtectedRoute } from '../src/router/ProtectedRoute'
import { useAuthStore } from '../src/store/authStore'
import { adminUser, waiterUser } from './testServer'
import { renderWithProviders } from './renderWithProviders'

describe('ProtectedRoute', () => {
  it('redirige a login cuando no hay sesion', async () => {
    renderWithProviders(
      <MemoryRouter initialEntries={['/admin']}>
        <Routes>
          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRoles={['ADMINISTRADOR']}>
                <p>Privado</p>
              </ProtectedRoute>
            }
          />
          <Route path="/login" element={<p>Login visible</p>} />
        </Routes>
      </MemoryRouter>,
    )

    expect(await screen.findByText('Login visible')).toBeInTheDocument()
  })

  it('redirige a unauthorized cuando el rol no tiene permiso', async () => {
    useAuthStore.getState().setSession(waiterUser, 'waiter-token', 3600)

    renderWithProviders(
      <MemoryRouter initialEntries={['/admin']}>
        <Routes>
          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRoles={['ADMINISTRADOR']}>
                <p>Privado</p>
              </ProtectedRoute>
            }
          />
          <Route path="/unauthorized" element={<p>No autorizado</p>} />
        </Routes>
      </MemoryRouter>,
    )

    expect(await screen.findByText('No autorizado')).toBeInTheDocument()
  })

  it('renderiza contenido privado con rol permitido', () => {
    useAuthStore.getState().setSession(adminUser, 'admin-token', 3600)

    renderWithProviders(
      <MemoryRouter initialEntries={['/admin']}>
        <Routes>
          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRoles={['ADMINISTRADOR']}>
                <p>Privado</p>
              </ProtectedRoute>
            }
          />
        </Routes>
      </MemoryRouter>,
    )

    expect(screen.getByText('Privado')).toBeInTheDocument()
  })
})
