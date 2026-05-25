/** Configuracion HTTP compartida: cliente Axios, JWT, errores y helpers de respuesta. */
import axios, { AxiosError } from 'axios'
import type { AuthResponse } from '../types/api'

/**
 * Centraliza la configuracion HTTP de Axios y las reglas transversales de autenticacion.
 */
const AUTH_STORAGE_KEY = 'restosync-auth'

/**
 * Lee el JWT persistido por Zustand sin acoplar la capa HTTP al store de React.
 */
const getStoredToken = () => {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    if (!raw) return null

    const parsed = JSON.parse(raw) as { state?: { token?: string } }
    return parsed.state?.token ?? null
  } catch {
    return null
  }
}

/**
 * Limpia la sesion local y avisa al resto de la app que el token expiro o ya no es valido.
 */
export const clearStoredSession = () => {
  localStorage.removeItem(AUTH_STORAGE_KEY)
  window.dispatchEvent(new Event('restosync:auth-expired'))
}

/**
 * Instancia base para todas las llamadas al backend REST.
 */
export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

/**
 * Agrega el header Authorization a cada request cuando existe un token persistido.
 */
apiClient.interceptors.request.use((config) => {
  const token = getStoredToken()

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

/**
 * Redirige al login ante respuestas 401 para evitar que el usuario siga en una sesion invalida.
 */
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      clearStoredSession()

      if (window.location.pathname !== '/login') {
        window.location.assign('/login')
      }
    }

    return Promise.reject(error)
  },
)

/**
 * Extrae el campo data del envelope estandar de la API y falla si la respuesta no trae payload.
 */
export const unwrapApiResponse = <T>(response: { data: { data?: T } }) => {
  if (response.data.data === undefined) {
    throw new Error('La respuesta de la API no incluyo datos.')
  }

  return response.data.data
}

/**
 * Construye un header Authorization desde la respuesta de autenticacion.
 */
export const buildAuthorizationHeader = (auth: AuthResponse) =>
  `${auth.tokenType ?? 'Bearer'} ${auth.token}`
