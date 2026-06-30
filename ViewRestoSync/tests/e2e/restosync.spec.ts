import { expect, test } from '@playwright/test'

const credentials = {
  admin: { email: 'admin@restosync.com', password: '123456' },
  waiter: { email: 'james@restosync.com', password: '123456' },
  cook: { email: 'mike@restosync.com', password: '123456' },
}

async function login(page: import('@playwright/test').Page, email: string, password: string) {
  await page.goto('/login')
  await page.getByLabel(/email/i).fill(email)
  await page.getByLabel(/password/i).fill(password)
  await page.getByRole('button', { name: /iniciar sesion/i }).click()
}

test('admin entra al dashboard', async ({ page }) => {
  await login(page, credentials.admin.email, credentials.admin.password)

  await expect(page).toHaveURL(/\/admin/)
  await expect(page.getByText(/admin/i).first()).toBeVisible()
})

test('mesero crea una comanda desde POS', async ({ page }) => {
  await login(page, credentials.waiter.email, credentials.waiter.password)

  await expect(page).toHaveURL(/\/mesero\/pos/)
  await page.getByLabel('Agregar producto').first().click()
  await page.getByRole('button', { name: /enviar/i }).click()

  await expect(page.getByText(/comanda enviada/i)).toBeVisible()
})

test('cocina visualiza comandas activas', async ({ page }) => {
  await login(page, credentials.cook.email, credentials.cook.password)

  await expect(page).toHaveURL(/\/cocina\/kds/)
  await expect(page.getByText(/kitchen display/i)).toBeVisible()
})

test('rol sin permiso no entra a rutas restringidas', async ({ page }) => {
  await login(page, credentials.waiter.email, credentials.waiter.password)

  await page.goto('/admin/users')

  await expect(page).toHaveURL(/\/unauthorized/)
})

test('refresh de ruta interna mantiene navegacion valida', async ({ page }) => {
  await login(page, credentials.admin.email, credentials.admin.password)
  await page.goto('/admin/catalog')
  await page.reload()

  await expect(page).toHaveURL(/\/admin\/catalog/)
  await expect(page.getByRole('heading', { name: /catalogo/i })).toBeVisible()
})
