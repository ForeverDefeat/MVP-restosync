import { screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it } from 'vitest'
import BarPage from '../src/pages/bar/BarPage'
import KDSPage from '../src/pages/cocina/KDSPage'
import { useAuthStore } from '../src/store/authStore'
import { adminUser } from './testServer'
import { renderWithProviders } from './renderWithProviders'

describe('KDS y Bar', () => {
  it('renderiza pedidos activos de cocina', async () => {
    useAuthStore.getState().setSession(adminUser, 'admin-token', 3600)

    renderWithProviders(
      <MemoryRouter>
        <KDSPage />
      </MemoryRouter>,
    )

    expect(await screen.findByText('Kitchen Display')).toBeInTheDocument()
    expect(await screen.findByText('#1001')).toBeInTheDocument()
    expect(screen.getByText('Lomo saltado')).toBeInTheDocument()
  })

  it('renderiza estado vacio de bar', async () => {
    useAuthStore.getState().setSession(adminUser, 'admin-token', 3600)

    renderWithProviders(
      <MemoryRouter>
        <BarPage />
      </MemoryRouter>,
    )

    expect(await screen.findByText('Bar Display')).toBeInTheDocument()
    expect(await screen.findAllByText(/sin pendientes/i)).toHaveLength(1)
  })
})
