/** Mapa de imagenes locales para asignar fotos del catalogo a productos por nombre. */
import type { Product } from '../types/models'

/**
 * Carga las imagenes locales del menu para poder resolverlas en runtime por nombre de producto.
 */
const images = import.meta.glob<string>('./*.png', {
  eager: true,
  import: 'default',
  query: '?url',
})

/**
 * Relacion entre nombres de productos y archivos reales dentro de assets.
 */
const imageByProductName: Record<string, string> = {
  'Arroz con pato': './Arroz con pato (con papa a la huancaína y salsa criolla).png',
  'Buffet Personal': './Buffet Personal  (Papa a la Huancaína, Costillar de cordero, Carapulcra, Sopa seca, Pallares).png',
  'Cabrito a la norteña con pallares': './Cabrito a la norteña con pallares (arroz y salsa criolla).png',
  'Carapulcra con sopa seca': './Carapulcra con sopa seca.png',
  'Ceviche de Pescado': './Ceviche de Pescado.png',
  'Chicharrón de pescado': './Chicharrón de pescado (con salsa criolla).png',
  'Chilcano de pisco': './Chilcano de pisco (Pisco, Ginger Ale, limón).png',
  'Coctel de algarrobina': './Coctel de algarrobina (Pisco, algarrobina, leche, bitter).png',
  'Combinado Arroz con pato - Carapulcra': './Combinado Arroz con pato - Carapulcra.png',
  'Cuy chactado': './Cuy chactado (Con papas doradas, salsa, arroz).png',
  'Ensalada de pallares verdes': './Ensalada de pallares verdes.png',
  'Leche de Tigre': './Leche de Tigre.png',
  'Maracuyá sour': './Maracuyá sour (Pisco, maracuyá, clara de huevo, bitter).png',
  'Pallar punch': './Pallar punch (Pisco, zumo y jarabe de piña, limón).png',
  'Pallares con filete de pescado': './Pallares con filete de pescado.png',
  'Pallares con seco de res': './Pallares con seco de res (Con salsa criolla).png',
  'Papa a la Huancaína': './Papa a la Huancaína.png',
  'Picante de pallares verdes con costillar': './Picante de pallares verdes con costillar de cordero o seco de res.png',
  'Picante de pallares verdes con pescado': './Picante de pallares verdes c filete de pescado.png',
  'Pisco sour': './Pisco sour (Pisco, limón, clara de huevo, bitter).png',
  'Pisco sour doble': './Pisco sour (Pisco, limón, clara de huevo, bitter).png',
  'Triple Arroz con pato - Carapulcra - Sopa seca': './Triple  Arroz con pato – Carapulcra - Sopa seca.png',

  'Smash Burger Especial': './Ceviche de Pescado.png',
  'Papas Trufadas': './Papa a la Huancaína.png',
  'Salmon a la Parrilla': './Chicharrón de pescado (con salsa criolla).png',
  'Ensalada Cesar': './Ensalada de pallares verdes.png',
  'Ribeye 12oz': './Costillar de cordero (arroz, papa dorada y salsa criolla).png',
  'Pizza Margarita': './Carapulcra con sopa seca.png',
  'Risotto de Champinones': './Pallares con seco de res (Con salsa criolla).png',
  'Pechuga Rellena': './Cabrito a la norteña con pallares (arroz y salsa criolla).png',
  'Bowl Vegano': './Pallares con filete de pescado.png',
  'Tacos al Pastor': './Cuy chactado (Con papas doradas, salsa, arroz).png',
  'Margarita Picante': './Pisco sour (Pisco, limón, clara de huevo, bitter).png',
  'Mojito Clasico': './Chilcano de pisco (Pisco, Ginger Ale, limón).png',
  'Agua Mineral': './Pallar punch (Pisco, zumo y jarabe de piña, limón).png',
  'Jugo de Naranja Natural': './Maracuyá sour (Pisco, maracuyá, clara de huevo, bitter).png',
  'Cerveza Artesanal IPA': './Coctel de algarrobina (Pisco, algarrobina, leche, bitter).png',
  'Vino Tinto Copa': './Pisco sour (Pisco, limón, clara de huevo, bitter).png',
}

/**
 * Normaliza strings para comparar nombres aunque tengan mayusculas o acentos distintos.
 */
const normalize = (value: string) =>
  value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/\s+/g, ' ')
    .trim()
    .toLowerCase()

const normalizedImageByProductName = Object.fromEntries(
  Object.entries(imageByProductName).map(([name, path]) => [normalize(name), path]),
)

/**
 * Devuelve la imagen local del producto o cae al imageUrl remoto si no hay coincidencia.
 */
export const getProductImage = (product: Pick<Product, 'imageUrl' | 'name'>) => {
  const localPath = normalizedImageByProductName[normalize(product.name)]
  return (localPath && images[localPath]) || product.imageUrl || null
}
