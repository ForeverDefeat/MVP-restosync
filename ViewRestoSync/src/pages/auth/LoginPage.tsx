/** Pantalla de login con fondo visual, formulario y redireccion por rol autenticado. */
import { Eye, Grid3X3, Lock, User, UtensilsCrossed } from 'lucide-react'
import { useEffect, useState, type FormEvent } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'
import { getSafeRedirectPath } from '../../router/routes'

/** Estado opcional que React Router deja cuando una ruta protegida redirige al login. */
interface LocationState {
  from?: {
    pathname?: string
  }
}

/** Pagina de acceso: limpia sesiones previas, autentica credenciales y redirige segun el rol. */
export default function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const login = useAuthStore((state) => state.login)
  const logout = useAuthStore((state) => state.logout)
  const isLoading = useAuthStore((state) => state.isLoading)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    logout()
  }, [logout])

  /** Envia las credenciales al store de auth y navega a una ruta segura para el rol autenticado. */
  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setError(null)

    try {
      const user = await login(email, password)
      const locationState = location.state as LocationState | null
      navigate(getSafeRedirectPath(user.role, locationState?.from?.pathname), { replace: true })
    } catch {
      setError('No se pudo iniciar sesion. Revisa tus credenciales.')
    }
  }

  return (
    <main className="auth-shell">
      <form className="auth-card" onSubmit={handleSubmit}>
        <div className="auth-brand">
          <span className="auth-logo">
            <UtensilsCrossed aria-hidden="true" size={25} />
          </span>
          <strong>RestoSync</strong>
        </div>

        <div className="auth-heading">
          <h1>Bienvenido de nuevo</h1>
          <p>Inicia sesion en tu cuenta para continuar</p>
        </div>

        <label>
          ID de empleado o email
          <span className="auth-input-shell">
            <User aria-hidden="true" size={18} />
            <input
              autoComplete="email"
              onChange={(event) => setEmail(event.target.value)}
              required
              type="email"
              value={email}
            />
          </span>
        </label>

        <label>
          Password
          <span className="auth-input-shell">
            <Lock aria-hidden="true" size={18} />
            <input
              autoComplete="current-password"
              onChange={(event) => setPassword(event.target.value)}
              required
              type="password"
              value={password}
            />
            <button aria-label="Mostrar password" className="auth-eye-button" tabIndex={-1} type="button">
              <Eye aria-hidden="true" size={18} />
            </button>
          </span>
        </label>

        <button className="auth-forgot" type="button">
          ¿Olvidaste tu contraseña?
        </button>

        {error && <p className="form-error">{error}</p>}

        <button disabled={isLoading} type="submit">
          {isLoading ? 'Ingresando...' : 'Iniciar sesion'}
        </button>

        <button className="auth-pin-button" disabled type="button">
          <Grid3X3 aria-hidden="true" size={17} />
          Ingresar con PIN
        </button>
      </form>
    </main>
  )
}
