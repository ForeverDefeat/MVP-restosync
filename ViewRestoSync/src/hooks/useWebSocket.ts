/** Hook de realtime: conecta SockJS/STOMP e invalida caches cuando llegan eventos. */
import { Client } from '@stomp/stompjs'
import { useEffect } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { queryKeys } from './queryKeys'

const WS_URL = import.meta.env.VITE_WS_URL ?? 'http://localhost:8080/ws'

/**
 * Abre una conexion STOMP/SockJS para invalidar caches cuando backend notifica cambios.
 */
export const useWebSocket = (enabled = true) => {
  const queryClient = useQueryClient()

  useEffect(() => {
    if (!enabled) return undefined

    let cancelled = false
    let client: Client | undefined

    /** Carga SockJS en tiempo de ejecucion para evitar problemas de global en Vite. */
    const connect = async () => {
      const browserGlobal = globalThis as typeof globalThis & { global?: typeof globalThis }
      browserGlobal.global ??= globalThis

      const { default: SockJS } = await import('sockjs-client')
      if (cancelled) return

      const stompClient = new Client({
        reconnectDelay: 5000,
        webSocketFactory: () => new SockJS(WS_URL),
        onConnect: () => {
          stompClient.subscribe('/topic/orders', () => {
            queryClient.invalidateQueries({ queryKey: queryKeys.orders.all })
          })
          stompClient.subscribe('/topic/products', () => {
            queryClient.invalidateQueries({ queryKey: queryKeys.products.all })
          })
          stompClient.subscribe('/topic/users', () => {
            queryClient.invalidateQueries({ queryKey: queryKeys.users.all })
          })
        },
      })

      client = stompClient
      stompClient.activate()
    }

    void connect()

    return () => {
      cancelled = true
      void client?.deactivate()
    }
  }, [enabled, queryClient])
}
