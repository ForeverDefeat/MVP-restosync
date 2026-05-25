/** Controles simples de paginacion para listados del backend. */
import { ChevronLeft, ChevronRight } from 'lucide-react'

/**
 * Props para controlar una paginacion simple basada en indice cero.
 */
interface PaginationProps {
  page: number
  totalPages: number
  onPageChange: (page: number) => void
}

/**
 * Controles prev/next con estado deshabilitado segun la pagina actual.
 */
export function Pagination({ page, totalPages, onPageChange }: PaginationProps) {
  const canGoBack = page > 0
  const canGoNext = page + 1 < totalPages

  return (
    <nav className="pagination" aria-label="Paginacion">
      <button
        aria-label="Pagina anterior"
        className="icon-button"
        disabled={!canGoBack}
        onClick={() => onPageChange(page - 1)}
        type="button"
      >
        <ChevronLeft aria-hidden="true" size={18} />
      </button>
      <span>
        {page + 1} / {Math.max(totalPages, 1)}
      </span>
      <button
        aria-label="Pagina siguiente"
        className="icon-button"
        disabled={!canGoNext}
        onClick={() => onPageChange(page + 1)}
        type="button"
      >
        <ChevronRight aria-hidden="true" size={18} />
      </button>
    </nav>
  )
}
