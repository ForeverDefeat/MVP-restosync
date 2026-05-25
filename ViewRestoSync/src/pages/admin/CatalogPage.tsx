/** Gestion administrativa de catalogo, disponibilidad y formulario de producto. */
import { Plus, ToggleLeft, ToggleRight } from 'lucide-react'
import { useState } from 'react'
import { getProductImage } from '../../assets/productImages'
import { PageLayout } from '../../components/layout/PageLayout'
import { EmptyState } from '../../components/ui/EmptyState'
import { SkeletonCard } from '../../components/ui/SkeletonCard'
import { useCreateProduct, useGetProducts, useToggleAvailability, useUpdateProduct } from '../../hooks/useProducts'
import { useNotificationStore } from '../../store/notificationStore'
import type { ProductPayload } from '../../types/api'
import type { Product } from '../../types/models'
import { formatMoney } from '../shared'
import { ProductFormModal } from './ProductFormModal'

/**
 * Pantalla administrativa para crear, editar y alternar disponibilidad de productos.
 */
export default function CatalogPage() {
  const productsQuery = useGetProducts()
  const createProduct = useCreateProduct()
  const updateProduct = useUpdateProduct()
  const toggleAvailability = useToggleAvailability()
  const notify = useNotificationStore((state) => state.add)
  const [editing, setEditing] = useState<Product | null>(null)
  const [modalOpen, setModalOpen] = useState(false)

  /** Decide si crear o actualizar segun exista un producto en edicion. */
  const saveProduct = async (payload: ProductPayload) => {
    try {
      if (editing) {
        await updateProduct.mutateAsync({ id: editing.id, payload })
      } else {
        await createProduct.mutateAsync(payload)
      }
      notify({ type: 'success', title: 'Producto guardado' })
      setModalOpen(false)
      setEditing(null)
    } catch {
      notify({ type: 'error', title: 'No se pudo guardar el producto' })
    }
  }

  return (
    <PageLayout>
      <section className="page-heading">
        <div>
          <span>ADMIN</span>
          <h1>Catalogo</h1>
        </div>
        <button className="primary-button" onClick={() => setModalOpen(true)} type="button">
          <Plus aria-hidden="true" size={17} />
          Nuevo
        </button>
      </section>

      {productsQuery.isLoading && (
        <div className="product-grid">
          <SkeletonCard />
          <SkeletonCard />
          <SkeletonCard />
        </div>
      )}
      {productsQuery.isError && <EmptyState title="No se pudo cargar el catalogo" />}
      {productsQuery.data?.length === 0 && <EmptyState title="No hay productos registrados" />}
      {productsQuery.data && productsQuery.data.length > 0 && (
        <div className="product-grid">
          {productsQuery.data.map((product) => {
            const productImage = getProductImage(product)

            return (
              <article className={`product-card ${product.available ? '' : 'is-unavailable'}`} key={product.id}>
                {productImage && <img alt="" src={productImage} />}
                <span className={`availability-badge ${product.available ? 'is-available' : 'is-unavailable'}`}>
                  {product.available ? 'Disponible' : 'No disponible'}
                </span>
                <div>
                  <span>{product.category}</span>
                  <h2>{product.name}</h2>
                  <p>
                    {formatMoney(product.price)} - {product.estimatedMinutes} min
                  </p>
                </div>
                <footer>
                  <button className="secondary-button" onClick={() => { setEditing(product); setModalOpen(true) }} type="button">
                    Editar
                  </button>
                  <button
                    aria-label={product.available ? 'Marcar como no disponible' : 'Marcar como disponible'}
                    className={`icon-button availability-toggle ${product.available ? 'is-available' : 'is-unavailable'}`}
                    onClick={() => toggleAvailability.mutate(product.id)}
                    type="button"
                  >
                    {product.available ? <ToggleRight aria-hidden="true" size={19} /> : <ToggleLeft aria-hidden="true" size={19} />}
                  </button>
                </footer>
              </article>
            )
          })}
        </div>
      )}

      <ProductFormModal
        key={editing?.id ?? 'new-product'}
        onClose={() => { setModalOpen(false); setEditing(null) }}
        onSubmit={saveProduct}
        open={modalOpen}
        product={editing}
      />
    </PageLayout>
  )
}
