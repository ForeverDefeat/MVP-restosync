/** Placeholder animado para representar tarjetas mientras cargan datos. */
/**
 * Props del placeholder de carga tipo tarjeta.
 */
interface SkeletonCardProps {
  lines?: number
}

/**
 * Dibuja un skeleton reutilizable mientras cargan listados o tarjetas.
 */
export function SkeletonCard({ lines = 3 }: SkeletonCardProps) {
  return (
    <div className="skeleton-card" aria-hidden="true">
      <span />
      {Array.from({ length: lines }).map((_, index) => (
        <i key={index} />
      ))}
    </div>
  )
}
