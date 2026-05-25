/** Gestion administrativa de empleados, filtros por rol y cambios de perfil. */
import { Clock3, Fingerprint, Mail, Power, Search, UserPlus } from 'lucide-react'
import { useMemo, useState, type FormEvent } from 'react'
import { PageLayout } from '../../components/layout/PageLayout'
import { EmptyState } from '../../components/ui/EmptyState'
import { Modal } from '../../components/ui/Modal'
import { useCreateUser, useGetUsers, useToggleActive, useUpdateRole } from '../../hooks/useUsers'
import { useNotificationStore } from '../../store/notificationStore'
import type { UserRole } from '../../types/enums'
import type { User } from '../../types/models'

/**
 * Etiquetas visibles para el chip de rol de cada empleado.
 */
const roleBadges: Record<UserRole, string> = {
  ADMINISTRADOR: 'Administracion',
  MESERO: 'Mesero',
  COCINERO: 'Cocinero de Linea',
  BARTENDER: 'Bartender',
}

/**
 * Texto de turno simulado por rol para completar la ficha del empleado.
 */
const shiftByRole: Record<UserRole, string> = {
  ADMINISTRADOR: 'Turno actual: 09:00 - 18:00',
  MESERO: 'Turno actual: 10:00 - 18:00',
  COCINERO: 'Turno actual: 08:00 - 16:00',
  BARTENDER: 'Turno actual: 16:00 - 00:00',
}

/**
 * Filtros disponibles para segmentar la grilla de empleados.
 */
const roleFilters: Array<{ label: string; value: UserRole | 'TODOS' }> = [
  { label: 'Todos', value: 'TODOS' },
  { label: 'Meseros', value: 'MESERO' },
  { label: 'Cocina', value: 'COCINERO' },
  { label: 'Bar', value: 'BARTENDER' },
  { label: 'Administrativo', value: 'ADMINISTRADOR' },
]

/**
 * Genera iniciales para el avatar cuando no existe imagen de usuario.
 */
const getInitials = (name: string) =>
  name
    .split(' ')
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()

/**
 * Genera un codigo visual de empleado a partir del id real.
 */
const getEmployeeId = (id: number) => `EMP-${String(id).padStart(4, '0')}`

/**
 * Gestion administrativa de usuarios con filtros, tarjetas y selector de rol.
 */
export default function UsersPage() {
  const usersQuery = useGetUsers()
  const createUser = useCreateUser()
  const updateRole = useUpdateRole()
  const toggleActive = useToggleActive()
  const notify = useNotificationStore((state) => state.add)
  const [modalOpen, setModalOpen] = useState(false)
  const [search, setSearch] = useState('')
  const [roleFilter, setRoleFilter] = useState<UserRole | 'TODOS'>('TODOS')

  const filteredUsers = useMemo(() => {
    const query = search.trim().toLowerCase()

    return (usersQuery.data ?? []).filter((user) => {
      const matchesRole = roleFilter === 'TODOS' || user.role === roleFilter
      const matchesSearch =
        !query ||
        user.name.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query) ||
        getEmployeeId(user.id).toLowerCase().includes(query)

      return matchesRole && matchesSearch
    })
  }, [roleFilter, search, usersQuery.data])

  /** Crea un usuario desde el modal de alta. */
  const handleCreate = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const form = new FormData(event.currentTarget)

    try {
      await createUser.mutateAsync({
        name: String(form.get('name')),
        email: String(form.get('email')),
        password: String(form.get('password')),
        role: String(form.get('role')) as UserRole,
      })
      notify({ type: 'success', title: 'Usuario creado' })
      setModalOpen(false)
    } catch {
      notify({ type: 'error', title: 'No se pudo crear usuario' })
    }
  }

  /** Actualiza el rol del usuario si el selector cambio de valor. */
  const changeRole = (user: User, role: UserRole) => {
    if (user.role === role) return
    updateRole.mutate({ id: user.id, role })
  }

  return (
    <PageLayout>
      <section className="page-heading">
        <div>
          <span>ADMIN</span>
          <h1>Usuarios</h1>
        </div>
        <button className="primary-button" onClick={() => setModalOpen(true)} type="button">
          <UserPlus aria-hidden="true" size={17} />
          Añadir empleado
        </button>
      </section>

      <section className="staff-controls">
        <label className="staff-search">
          <Search aria-hidden="true" size={17} />
          <input
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Buscar empleado por nombre o ID..."
            value={search}
          />
        </label>
        <div className="staff-tabs" role="tablist" aria-label="Filtrar usuarios por rol">
          {roleFilters.map((filter) => (
            <button
              className={roleFilter === filter.value ? 'is-active' : ''}
              key={filter.value}
              onClick={() => setRoleFilter(filter.value)}
              type="button"
            >
              {filter.label}
            </button>
          ))}
        </div>
      </section>

      {usersQuery.isError && <EmptyState title="No se pudieron cargar los usuarios" />}
      {usersQuery.data?.length === 0 && <EmptyState title="No hay usuarios registrados" />}
      {usersQuery.data && filteredUsers.length === 0 && <EmptyState title="Sin resultados" />}
      {filteredUsers.length > 0 && (
        <section className="staff-grid">
          {filteredUsers.map((user) => (
            <article className={`staff-card ${user.active ? '' : 'is-inactive'}`} key={user.id}>
              <header>
                <div className="staff-avatar" data-role={user.role}>
                  {getInitials(user.name)}
                  <span className={user.active ? 'is-online' : 'is-offline'} />
                </div>
                <div>
                  <h2>{user.name}</h2>
                  <span className={`staff-role role-${user.role.toLowerCase()}`}>{roleBadges[user.role]}</span>
                </div>
              </header>

              <div className="staff-meta">
                <span>
                  <Fingerprint aria-hidden="true" size={15} />
                  ID: {getEmployeeId(user.id)}
                </span>
                <span>
                  <Mail aria-hidden="true" size={15} />
                  {user.email}
                </span>
                <span className={user.active ? '' : 'is-danger'}>
                  <Clock3 aria-hidden="true" size={15} />
                  {user.active ? shiftByRole[user.role] : 'Inactivo'}
                </span>
              </div>

              <footer>
                <label className="role-select">
                  Rol
                  <select onChange={(event) => changeRole(user, event.target.value as UserRole)} value={user.role}>
                    <option value="ADMINISTRADOR">Administrador</option>
                    <option value="MESERO">Mesero</option>
                    <option value="COCINERO">Cocinero</option>
                    <option value="BARTENDER">Bartender</option>
                  </select>
                </label>
                <button className="staff-action" onClick={() => toggleActive.mutate(user.id)} type="button">
                  <Power aria-hidden="true" size={15} />
                  {user.active ? 'Desactivar' : 'Reactivar'}
                </button>
              </footer>
            </article>
          ))}
        </section>
      )}

      <Modal open={modalOpen} title="Nuevo usuario" onClose={() => setModalOpen(false)}>
        <form className="entity-form" onSubmit={handleCreate}>
          <label>Nombre<input name="name" required /></label>
          <label>Email<input name="email" required type="email" /></label>
          <label>Password<input minLength={8} name="password" required type="password" /></label>
          <label>
            Rol
            <select name="role" defaultValue="MESERO">
              <option value="ADMINISTRADOR">Administrador</option>
              <option value="MESERO">Mesero</option>
              <option value="COCINERO">Cocinero</option>
              <option value="BARTENDER">Bartender</option>
            </select>
          </label>
          <div className="form-actions">
            <button className="secondary-button" onClick={() => setModalOpen(false)} type="button">Cancelar</button>
            <button className="primary-button" type="submit">Crear</button>
          </div>
        </form>
      </Modal>
    </PageLayout>
  )
}
