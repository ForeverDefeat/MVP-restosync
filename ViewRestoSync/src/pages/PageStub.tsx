/** Pagina temporal reutilizable para modulos pendientes o vistas informativas simples. */
import { PageLayout } from '../components/layout/PageLayout'

/** Datos minimos para renderizar una pagina temporal dentro del layout principal. */
interface PageStubProps {
  eyebrow: string
  title: string
  description: string
}

/** Placeholder reutilizable para modulos que aun no tienen una experiencia completa. */
export function PageStub({ eyebrow, title, description }: PageStubProps) {
  return (
    <PageLayout>
      <section className="stub-panel">
        <span>{eyebrow}</span>
        <h1>{title}</h1>
        <p>{description}</p>
      </section>
    </PageLayout>
  )
}
