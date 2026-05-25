/** Componente raiz: aplica estilos globales y delega la navegacion al AppRouter. */
import './App.css'
import { AppRouter } from './router/AppRouter'

/**
 * Componente raiz que delega toda la navegacion al router principal.
 */
function App() {
  return <AppRouter />
}

export default App
