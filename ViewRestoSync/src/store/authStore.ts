/** Store global de autenticacion persistido en localStorage. */
import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { authApi } from '../api/authApi'
import type { User } from '../types/models'

/**
 * Estado global de autenticacion persistido para mantener sesion entre recargas.
 */
interface AuthState {
  currentUser: User | null
  token: string | null
  expiresAt: number | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (email: string, password: string) => Promise<User>
  logout: () => void
  setSession: (currentUser: User, token: string, expiresIn: number) => void
}

/**
 * Store de sesion: guarda usuario, token, vencimiento y acciones de login/logout.
 */
export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      currentUser: null,
      token: null,
      expiresAt: null,
      isAuthenticated: false,
      isLoading: false,

      /** Autentica contra backend y persiste el token recibido. */
      async login(email, password) {
        set({ isLoading: true })

        try {
          const auth = await authApi.login(email, password)
          const expiresAt = Date.now() + auth.expiresIn * 1000

          set({
            currentUser: auth.user,
            token: auth.token,
            expiresAt,
            isAuthenticated: true,
            isLoading: false,
          })

          return auth.user
        } catch (error) {
          set({ isLoading: false })
          throw error
        }
      },

      /** Limpia usuario, token y estado de carga para cerrar la sesion local. */
      logout() {
        set({
          currentUser: null,
          token: null,
          expiresAt: null,
          isAuthenticated: false,
          isLoading: false,
        })
      },

      /** Permite hidratar la sesion cuando ya se tiene un usuario y token valido. */
      setSession(currentUser, token, expiresIn) {
        set({
          currentUser,
          token,
          expiresAt: Date.now() + expiresIn * 1000,
          isAuthenticated: true,
        })
      },
    }),
    {
      name: 'restosync-auth',
      partialize: ({ currentUser, token, expiresAt, isAuthenticated }) => ({
        currentUser,
        token,
        expiresAt,
        isAuthenticated,
      }),
    },
  ),
)

if (typeof window !== 'undefined') {
  /** Escucha expiraciones disparadas por la capa HTTP para cerrar sesion globalmente. */
  window.addEventListener('restosync:auth-expired', () => {
    useAuthStore.getState().logout()
  })
}
