-- ══════════════════════════════════════════════════════════════════════════════
-- RestoSync — Datos iniciales
-- Contraseña de TODOS los usuarios: 123456
-- Hash BCrypt $2a$10$ válido generado y verificado
-- ══════════════════════════════════════════════════════════════════════════════

-- ── Usuarios ─────────────────────────────────────────────────────────────────

INSERT IGNORE INTO users (id, name, email, password, role, active, created_at) VALUES
(1, 'Sarah Jenkins',     'admin@restosync.com',   '$2a$10$Nl3fnwI.Bd0vOhvjMsC7SeElA.lpmEwBwAsQ2DfPxWYeMqS67iwki', 'ADMINISTRADOR', true, NOW()),
(2, 'James Williams',    'james@restosync.com',   '$2a$10$Nl3fnwI.Bd0vOhvjMsC7SeElA.lpmEwBwAsQ2DfPxWYeMqS67iwki', 'MESERO',        true, NOW()),
(3, 'Elena Rodriguez',   'elena@restosync.com',   '$2a$10$Nl3fnwI.Bd0vOhvjMsC7SeElA.lpmEwBwAsQ2DfPxWYeMqS67iwki', 'MESERO',        true, NOW()),
(4, 'Mike Torres',       'mike@restosync.com',    '$2a$10$Nl3fnwI.Bd0vOhvjMsC7SeElA.lpmEwBwAsQ2DfPxWYeMqS67iwki', 'COCINERO',      true, NOW()),
(5, 'Roberto Fernandez', 'roberto@restosync.com', '$2a$10$Nl3fnwI.Bd0vOhvjMsC7SeElA.lpmEwBwAsQ2DfPxWYeMqS67iwki', 'BARTENDER',     true, NOW());

-- ── Productos — Platos ───────────────────────────────────────────────────────

INSERT IGNORE INTO products (id, name, category, price, available, estimated_minutes, image_url, created_at, updated_at) VALUES
(1,  'Ceviche de Pescado',                    'PLATO', 32.00, true, 14, NULL, NOW(), NOW()),
(2,  'Papa a la Huancaína',                   'PLATO', 18.00, true,  8, NULL, NOW(), NOW()),
(3,  'Leche de Tigre',                        'PLATO', 24.00, true, 10, NULL, NOW(), NOW()),
(4,  'Chicharrón de pescado',                 'PLATO', 34.00, true, 16, NULL, NOW(), NOW()),
(5,  'Arroz con pato',                        'PLATO', 38.00, true, 18, NULL, NOW(), NOW()),
(6,  'Carapulcra con sopa seca',              'PLATO', 36.00, true, 18, NULL, NOW(), NOW()),
(7,  'Pallares con seco de res',              'PLATO', 35.00, true, 17, NULL, NOW(), NOW()),
(8,  'Cabrito a la norteña con pallares',     'PLATO', 42.00, true, 20, NULL, NOW(), NOW()),
(9,  'Cuy chactado',                          'PLATO', 46.00, true, 22, NULL, NOW(), NOW()),
(10, 'Buffet Personal',                       'PLATO', 49.00, true, 20, NULL, NOW(), NOW());

-- ── Productos — Bebidas ──────────────────────────────────────────────────────

INSERT IGNORE INTO products (id, name, category, price, available, estimated_minutes, image_url, created_at, updated_at) VALUES
(11, 'Pisco sour',              'BEBIDA', 18.00, true, 5, NULL, NOW(), NOW()),
(12, 'Chilcano de pisco',       'BEBIDA', 17.00, true, 5, NULL, NOW(), NOW()),
(13, 'Maracuyá sour',           'BEBIDA', 18.00, true, 5, NULL, NOW(), NOW()),
(14, 'Pallar punch',            'BEBIDA', 19.00, true, 5, NULL, NOW(), NOW()),
(15, 'Coctel de algarrobina',   'BEBIDA', 18.00, true, 5, NULL, NOW(), NOW()),
(16, 'Pisco sour doble',        'BEBIDA', 26.00, true, 6, NULL, NOW(), NOW());

UPDATE products SET name = 'Ceviche de Pescado', category = 'PLATO', price = 32.00, estimated_minutes = 14, image_url = NULL, updated_at = NOW() WHERE id = 1;
UPDATE products SET name = 'Papa a la Huancaína', category = 'PLATO', price = 18.00, estimated_minutes = 8, image_url = NULL, updated_at = NOW() WHERE id = 2;
UPDATE products SET name = 'Leche de Tigre', category = 'PLATO', price = 24.00, estimated_minutes = 10, image_url = NULL, updated_at = NOW() WHERE id = 3;
UPDATE products SET name = 'Chicharrón de pescado', category = 'PLATO', price = 34.00, estimated_minutes = 16, image_url = NULL, updated_at = NOW() WHERE id = 4;
UPDATE products SET name = 'Arroz con pato', category = 'PLATO', price = 38.00, estimated_minutes = 18, image_url = NULL, updated_at = NOW() WHERE id = 5;
UPDATE products SET name = 'Carapulcra con sopa seca', category = 'PLATO', price = 36.00, estimated_minutes = 18, image_url = NULL, updated_at = NOW() WHERE id = 6;
UPDATE products SET name = 'Pallares con seco de res', category = 'PLATO', price = 35.00, estimated_minutes = 17, image_url = NULL, updated_at = NOW() WHERE id = 7;
UPDATE products SET name = 'Cabrito a la norteña con pallares', category = 'PLATO', price = 42.00, estimated_minutes = 20, image_url = NULL, updated_at = NOW() WHERE id = 8;
UPDATE products SET name = 'Cuy chactado', category = 'PLATO', price = 46.00, estimated_minutes = 22, image_url = NULL, updated_at = NOW() WHERE id = 9;
UPDATE products SET name = 'Buffet Personal', category = 'PLATO', price = 49.00, estimated_minutes = 20, image_url = NULL, updated_at = NOW() WHERE id = 10;
UPDATE products SET name = 'Pisco sour', category = 'BEBIDA', price = 18.00, estimated_minutes = 5, image_url = NULL, updated_at = NOW() WHERE id = 11;
UPDATE products SET name = 'Chilcano de pisco', category = 'BEBIDA', price = 17.00, estimated_minutes = 5, image_url = NULL, updated_at = NOW() WHERE id = 12;
UPDATE products SET name = 'Maracuyá sour', category = 'BEBIDA', price = 18.00, estimated_minutes = 5, image_url = NULL, updated_at = NOW() WHERE id = 13;
UPDATE products SET name = 'Pallar punch', category = 'BEBIDA', price = 19.00, estimated_minutes = 5, image_url = NULL, updated_at = NOW() WHERE id = 14;
UPDATE products SET name = 'Coctel de algarrobina', category = 'BEBIDA', price = 18.00, estimated_minutes = 5, image_url = NULL, updated_at = NOW() WHERE id = 15;
UPDATE products SET name = 'Pisco sour doble', category = 'BEBIDA', price = 26.00, estimated_minutes = 6, image_url = NULL, updated_at = NOW() WHERE id = 16;

-- ── Órdenes de ejemplo ───────────────────────────────────────────────────────

INSERT IGNORE INTO orders (id, ticket_number, table_or_register, status, total, waiter_id, cancellation_reason, created_at, updated_at) VALUES
(1, '#8921', 'Mesa 12', 'PENDIENTE',      82.00, 2, NULL, NOW(), NOW()),
(2, '#8915', 'Mesa 08', 'EN_PREPARACION', 94.00, 2, NULL, NOW(), NOW()),
(3, '#8910', 'Mesa 21', 'LISTO',          90.00, 3, NULL, NOW(), NOW()),
(4, '#8912', 'Mesa 05', 'LISTO',          46.00, 3, NULL, NOW(), NOW()),
(5, '#8918', 'Mesa 15', 'EN_PREPARACION',112.00, 2, NULL, NOW(), NOW());

UPDATE orders SET total = 82.00, updated_at = NOW() WHERE id = 1;
UPDATE orders SET total = 94.00, updated_at = NOW() WHERE id = 2;
UPDATE orders SET total = 90.00, updated_at = NOW() WHERE id = 3;
UPDATE orders SET total = 46.00, updated_at = NOW() WHERE id = 4;
UPDATE orders SET total = 112.00, updated_at = NOW() WHERE id = 5;

-- ── Ítems de órdenes ─────────────────────────────────────────────────────────

INSERT IGNORE INTO order_items (id, order_id, product_id, product_name, category, quantity, unit_price, notes) VALUES
-- Orden 1 — Mesa 12 PENDIENTE (total: 14.50*2 + 8.00 = 37.00)
(1,  1, 1, 'Ceviche de Pescado',       'PLATO',  2, 32.00, 'Sin ají'),
(2,  1, 2, 'Papa a la Huancaína',      'PLATO',  1, 18.00, NULL),

-- Orden 2 — Mesa 08 EN_PREPARACION (total: 35.00*2 + 8.00 = 78.00)
(3,  2, 5, 'Arroz con pato',           'PLATO',  2, 38.00, 'Salsa criolla aparte'),
(4,  2, 2, 'Papa a la Huancaína',      'PLATO',  1, 18.00, NULL),

-- Orden 3 — Mesa 21 LISTO (total: 14.00*2 + 8.00 = 36.00)
(5,  3, 6, 'Carapulcra con sopa seca', 'PLATO',  2, 36.00, NULL),
(6,  3, 11,'Pisco sour',              'BEBIDA', 1, 18.00, 'Sin bitter'),

-- Orden 4 — Mesa 05 LISTO (total: 12.00)
(7,  4, 9, 'Cuy chactado',            'PLATO',  1, 46.00, NULL),

-- Orden 5 — Mesa 15 EN_PREPARACION (total: 13.50*2 + 15.00 = 42.00)
(8,  5, 7, 'Pallares con seco de res', 'PLATO',  2, 35.00, NULL),
(9,  5, 8, 'Cabrito a la norteña con pallares', 'PLATO',  1, 42.00, NULL);

UPDATE order_items SET product_id = 1, product_name = 'Ceviche de Pescado', category = 'PLATO', quantity = 2, unit_price = 32.00, notes = 'Sin ají' WHERE id = 1;
UPDATE order_items SET product_id = 2, product_name = 'Papa a la Huancaína', category = 'PLATO', quantity = 1, unit_price = 18.00, notes = NULL WHERE id = 2;
UPDATE order_items SET product_id = 5, product_name = 'Arroz con pato', category = 'PLATO', quantity = 2, unit_price = 38.00, notes = 'Salsa criolla aparte' WHERE id = 3;
UPDATE order_items SET product_id = 2, product_name = 'Papa a la Huancaína', category = 'PLATO', quantity = 1, unit_price = 18.00, notes = NULL WHERE id = 4;
UPDATE order_items SET product_id = 6, product_name = 'Carapulcra con sopa seca', category = 'PLATO', quantity = 2, unit_price = 36.00, notes = NULL WHERE id = 5;
UPDATE order_items SET product_id = 11, product_name = 'Pisco sour', category = 'BEBIDA', quantity = 1, unit_price = 18.00, notes = 'Sin bitter' WHERE id = 6;
UPDATE order_items SET product_id = 9, product_name = 'Cuy chactado', category = 'PLATO', quantity = 1, unit_price = 46.00, notes = NULL WHERE id = 7;
UPDATE order_items SET product_id = 7, product_name = 'Pallares con seco de res', category = 'PLATO', quantity = 2, unit_price = 35.00, notes = NULL WHERE id = 8;
UPDATE order_items SET product_id = 8, product_name = 'Cabrito a la norteña con pallares', category = 'PLATO', quantity = 1, unit_price = 42.00, notes = NULL WHERE id = 9;
