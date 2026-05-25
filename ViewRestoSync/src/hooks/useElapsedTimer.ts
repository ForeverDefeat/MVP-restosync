/** Hook utilitario para mostrar timers vivos a partir de una fecha de creacion. */
import { useEffect, useMemo, useState } from 'react'

/**
 * Convierte una fecha de creacion en un contador mm:ss respecto al instante actual.
 */
const formatElapsed = (createdAt: string, now: number) => {
  const diff = Math.max(0, now - new Date(createdAt).getTime())
  const totalSeconds = Math.floor(diff / 1000)
  const minutes = Math.floor(totalSeconds / 60)
  const seconds = totalSeconds % 60

  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

/**
 * Devuelve un timer vivo que se recalcula cada segundo desde createdAt.
 */
export const useElapsedTimer = (createdAt: string) => {
  const [now, setNow] = useState(() => Date.now())

  useEffect(() => {
    const intervalId = window.setInterval(() => setNow(Date.now()), 1000)
    return () => window.clearInterval(intervalId)
  }, [])

  return useMemo(() => formatElapsed(createdAt, now), [createdAt, now])
}
