/** Modal de alta/edicion de producto con validacion basica de formulario. */
import { useState, type FormEvent } from 'react'
import { Modal } from '../../components/ui/Modal'
import type { ProductPayload } from '../../types/api'
import type { Product } from '../../types/models'
import type { ProductCategory } from '../../types/enums'

/**
 * Props del modal que comparte formulario para alta y edicion de productos.
 */
interface ProductFormModalProps {
  open: boolean
  product?: Product | null
  onClose: () => void
  onSubmit: (payload: ProductPayload) => void
}

/**
 * Valores iniciales para crear un producto nuevo.
 */
const defaultPayload: ProductPayload = {
  name: '',
  category: 'PLATO',
  price: 0,
  available: true,
  estimatedMinutes: 10,
  imageUrl: '',
}

/**
 * Convierte un producto existente en payload editable o usa defaults para alta.
 */
const getInitialPayload = (product?: Product | null): ProductPayload =>
  product
    ? {
        name: product.name,
        category: product.category,
        price: product.price,
        available: product.available,
        estimatedMinutes: product.estimatedMinutes,
        imageUrl: product.imageUrl ?? '',
      }
    : defaultPayload

/**
 * Modal de formulario controlado para productos del catalogo.
 */
export function ProductFormModal({ open, product, onClose, onSubmit }: ProductFormModalProps) {
  const [payload, setPayload] = useState<ProductPayload>(() => getInitialPayload(product))

  /** Evita reload del formulario y entrega el payload al contenedor padre. */
  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    onSubmit(payload)
  }

  return (
    <Modal open={open} title={product ? 'Editar producto' : 'Nuevo producto'} onClose={onClose}>
      <form className="entity-form" onSubmit={handleSubmit}>
        <label>
          Nombre
          <input
            onChange={(event) => setPayload({ ...payload, name: event.target.value })}
            required
            value={payload.name}
          />
        </label>
        <label>
          Categoria
          <select
            onChange={(event) => setPayload({ ...payload, category: event.target.value as ProductCategory })}
            value={payload.category}
          >
            <option value="PLATO">Plato</option>
            <option value="BEBIDA">Bebida</option>
          </select>
        </label>
        <label>
          Precio
          <input
            min="0.01"
            onChange={(event) => setPayload({ ...payload, price: Number(event.target.value) })}
            required
            step="0.01"
            type="number"
            value={payload.price}
          />
        </label>
        <label>
          Minutos estimados
          <input
            min="1"
            onChange={(event) => setPayload({ ...payload, estimatedMinutes: Number(event.target.value) })}
            required
            type="number"
            value={payload.estimatedMinutes}
          />
        </label>
        <label>
          URL de imagen
          <input
            onChange={(event) => setPayload({ ...payload, imageUrl: event.target.value })}
            value={payload.imageUrl}
          />
        </label>
        <label className="checkbox-field">
          <input
            checked={payload.available}
            onChange={(event) => setPayload({ ...payload, available: event.target.checked })}
            type="checkbox"
          />
          Disponible
        </label>
        <div className="form-actions">
          <button className="secondary-button" onClick={onClose} type="button">
            Cancelar
          </button>
          <button className="primary-button" type="submit">
            Guardar
          </button>
        </div>
      </form>
    </Modal>
  )
}
