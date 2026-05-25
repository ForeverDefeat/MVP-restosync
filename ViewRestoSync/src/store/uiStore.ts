/** Store global para estado de UI transversal como sidebar, modal y loading. */
import { create } from 'zustand'

/**
 * Identificadores de modales globales que pueden abrirse desde distintas vistas.
 */
export type ActiveModal =
  | 'create-product'
  | 'edit-product'
  | 'create-user'
  | 'cancel-order'
  | 'confirm'
  | null

/**
 * Estado visual compartido para layout, modales y carga global.
 */
interface UiState {
  sidebarOpen: boolean
  activeModal: ActiveModal
  globalLoading: boolean
  setSidebarOpen: (open: boolean) => void
  toggleSidebar: () => void
  setActiveModal: (modal: ActiveModal) => void
  setGlobalLoading: (loading: boolean) => void
}

/**
 * Store UI para coordinar sidebar, modal activo y loading global.
 */
export const useUiStore = create<UiState>((set) => ({
  sidebarOpen: false,
  activeModal: null,
  globalLoading: false,

  /** Define explicitamente si el sidebar mobile esta abierto. */
  setSidebarOpen(sidebarOpen) {
    set({ sidebarOpen })
  },

  /** Alterna la visibilidad del sidebar mobile. */
  toggleSidebar() {
    set((state) => ({ sidebarOpen: !state.sidebarOpen }))
  },

  /** Registra que modal global esta activo. */
  setActiveModal(activeModal) {
    set({ activeModal })
  },

  /** Marca o libera un estado de carga global. */
  setGlobalLoading(globalLoading) {
    set({ globalLoading })
  },
}))
