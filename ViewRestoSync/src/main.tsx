/** Punto de entrada del frontend: monta React, React Query y el router principal. */
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import './styles/themes.css'
import './index.css'
import App from './App.tsx'

/**
 * Cliente global de React Query con valores por defecto para todas las consultas.
 */
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      staleTime: 15000,
    },
  },
})

/**
 * Punto de montaje de React: registra providers globales y renderiza la app.
 */
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
    </QueryClientProvider>
  </StrictMode>,
)
