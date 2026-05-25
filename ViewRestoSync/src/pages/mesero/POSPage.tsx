/** POS del mesero: catalogo, carrito y creacion de comandas. */
import { Minus, Plus, Send, Trash2, X } from 'lucide-react'
import { useMemo, useState } from 'react'
import { getProductImage } from '../../assets/productImages'
import { PageLayout } from '../../components/layout/PageLayout'
import { EmptyState } from '../../components/ui/EmptyState'
import { SkeletonCard } from '../../components/ui/SkeletonCard'
import { useCreateOrder } from '../../hooks/useOrders'
import { useGetProducts } from '../../hooks/useProducts'
import { useNotificationStore } from '../../store/notificationStore'
import type { ProductCategory } from '../../types/enums'
import type { Product } from '../../types/models'
import { formatMoney } from '../shared'

/**
 * Linea local del carrito antes de enviarla como item de comanda.
 */
interface CartLine {
  product: Product
  quantity: number
  notes: string
}

/**
 * Pagina POS del mesero: filtra catalogo, arma carrito y crea comandas.
 */
export default function POSPage() {
  const [category, setCategory] = useState<ProductCategory | undefined>()
  const [search, setSearch] = useState('')
  const [tableOrRegister, setTableOrRegister] = useState('Mesa 1')
  const [cart, setCart] = useState<CartLine[]>([])
  const productsQuery = useGetProducts({ available: true, category, search: search || undefined })
  const createOrder = useCreateOrder()
  const notify = useNotificationStore((state) => state.add)
  const total = useMemo(
    () => cart.reduce((sum, item) => sum + item.product.price * item.quantity, 0),
    [cart],
  )

  /** Agrega un producto al carrito o incrementa su cantidad si ya existe. */
  const addProduct = (product: Product) => {
    setCart((current) => {
      const existing = current.find((item) => item.product.id === product.id)
      if (existing) {
        return current.map((item) =>
          item.product.id === product.id ? { ...item, quantity: item.quantity + 1 } : item,
        )
      }

      return [...current, { product, quantity: 1, notes: '' }]
    })
  }

  /** Ajusta cantidades del carrito y elimina lineas que llegan a cero. */
  const changeQuantity = (productId: number, delta: number) => {
    setCart((current) =>
      current
        .map((item) => (item.product.id === productId ? { ...item, quantity: item.quantity + delta } : item))
        .filter((item) => item.quantity > 0),
    )
  }

  /** Envia el carrito al backend como comanda nueva. */
  const submitOrder = async () => {
    if (cart.length === 0) return

    try {
      await createOrder.mutateAsync({
        tableOrRegister,
        items: cart.map((item) => ({
          productId: item.product.id,
          quantity: item.quantity,
          notes: item.notes || undefined,
        })),
      })
      setCart([])
      notify({ type: 'success', title: 'Comanda enviada', message: 'El pedido fue enviado correctamente.' })
    } catch {
      notify({ type: 'error', title: 'No se pudo enviar', message: 'Revisa la conexion con el backend.' })
    }
  }

  return (
    <PageLayout>
      <section className="page-heading">
        <div>
          <span>MESERO</span>
          <h1>Nuevo pedido</h1>
        </div>
      </section>

      <div className="pos-grid">
        <section className="work-panel">
          <div className="toolbar">
            <input
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Buscar producto"
              value={search}
            />
            <div className="segmented">
              <button className={!category ? 'is-active' : ''} onClick={() => setCategory(undefined)} type="button">
                Todos
              </button>
              <button className={category === 'PLATO' ? 'is-active' : ''} onClick={() => setCategory('PLATO')} type="button">
                Platos
              </button>
              <button className={category === 'BEBIDA' ? 'is-active' : ''} onClick={() => setCategory('BEBIDA')} type="button">
                Bebidas
              </button>
            </div>
          </div>

          {productsQuery.isLoading && (
            <div className="product-grid">
              <SkeletonCard />
              <SkeletonCard />
              <SkeletonCard />
            </div>
          )}
          {productsQuery.isError && <EmptyState title="No se pudo cargar el catalogo" />}
          {productsQuery.data?.length === 0 && <EmptyState title="Sin productos disponibles" />}
          {productsQuery.data && productsQuery.data.length > 0 && (
            <div className="product-grid product-grid-transition" key={`${category ?? 'todos'}-${search}`}>
              {productsQuery.data.map((product) => {
                const productImage = getProductImage(product)

                return (
                  <article className="product-card" key={product.id}>
                    {productImage && <img alt="" src={productImage} />}
                    <div>
                      <span>{product.category}</span>
                      <h2>{product.name}</h2>
                      <p>{product.estimatedMinutes} min</p>
                    </div>
                    <footer>
                      <strong>{formatMoney(product.price)}</strong>
                      <button aria-label="Agregar producto" className="icon-button" onClick={() => addProduct(product)} type="button">
                        <Plus aria-hidden="true" size={18} />
                      </button>
                    </footer>
                  </article>
                )
              })}
            </div>
          )}
        </section>

        <aside className="work-panel cart-panel">
          <header>
            <h2>Resumen</h2>
            <button className="secondary-button danger-button" onClick={() => setCart([])} type="button">
              <Trash2 aria-hidden="true" size={15} />
              Vaciar
            </button>
          </header>
          <label>
            Mesa o caja
            <input onChange={(event) => setTableOrRegister(event.target.value)} value={tableOrRegister} />
          </label>

          <div className="cart-lines">
            {cart.length === 0 && <EmptyState title="Carrito vacio" description="Agrega productos para crear una comanda." />}
            {cart.map((item) => (
              <article className="cart-line" key={item.product.id}>
                <div>
                  <strong>{item.product.name}</strong>
                  <span>{formatMoney(item.product.price * item.quantity)}</span>
                </div>
                <input
                  onChange={(event) =>
                    setCart((current) =>
                      current.map((line) =>
                        line.product.id === item.product.id ? { ...line, notes: event.target.value } : line,
                      ),
                    )
                  }
                  placeholder="Notas"
                  value={item.notes}
                />
                <footer>
                  <div className="stepper">
                    <button onClick={() => changeQuantity(item.product.id, -1)} type="button">
                      <Minus aria-hidden="true" size={15} />
                    </button>
                    <span>{item.quantity}</span>
                    <button onClick={() => changeQuantity(item.product.id, 1)} type="button">
                      <Plus aria-hidden="true" size={15} />
                    </button>
                  </div>
                  <button className="icon-button" onClick={() => changeQuantity(item.product.id, -item.quantity)} type="button">
                    <X aria-hidden="true" size={16} />
                  </button>
                </footer>
              </article>
            ))}
          </div>

          <footer className="cart-total">
            <div>
              <span>Total</span>
              <strong>{formatMoney(total)}</strong>
            </div>
            <button
              className="primary-button"
              disabled={cart.length === 0 || createOrder.isPending}
              onClick={submitOrder}
              type="button"
            >
              <Send aria-hidden="true" size={17} />
              Enviar
            </button>
          </footer>
        </aside>
      </div>
    </PageLayout>
  )
}
