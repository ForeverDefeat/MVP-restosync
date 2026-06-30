import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it } from 'vitest'
import LoginPage from '../src/pages/auth/LoginPage'
import { useAuthStore } from '../src/store/authStore'
import { renderWithProviders } from './renderWithProviders'

describe('LoginPage', () => {
  it('guarda sesion y redirige segun rol', async () => {
    const user = userEvent.setup()
    renderWithProviders(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    )

    await user.type(screen.getByLabelText(/email/i), 'admin@restosync.com')
    await user.type(screen.getByLabelText('Password'), '123456')
    await user.click(screen.getByRole('button', { name: /iniciar sesion/i }))

    await waitFor(() => expect(useAuthStore.getState().currentUser?.role).toBe('ADMINISTRADOR'))
    expect(useAuthStore.getState().token).toBe('administrador-token')
  })

  it('muestra error cuando credenciales fallan', async () => {
    const user = userEvent.setup()
    renderWithProviders(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    )

    await user.type(screen.getByLabelText(/email/i), 'admin@restosync.com')
    await user.type(screen.getByLabelText('Password'), 'bad')
    await user.click(screen.getByRole('button', { name: /iniciar sesion/i }))

    expect(await screen.findByText(/no se pudo iniciar sesion/i)).toBeInTheDocument()
  })
})
