/** Dialogo reutilizable para confirmar acciones sensibles o destructivas. */
import { Modal } from './Modal'

/**
 * Props de dialogo de confirmacion reutilizable para acciones destructivas o sensibles.
 */
interface ConfirmDialogProps {
  cancelLabel?: string
  confirmLabel?: string
  message: string
  open: boolean
  title: string
  onCancel: () => void
  onConfirm: () => void
}

/**
 * Muestra un modal simple con mensaje y acciones confirmar/cancelar.
 */
export function ConfirmDialog({
  cancelLabel = 'Cancelar',
  confirmLabel = 'Confirmar',
  message,
  open,
  title,
  onCancel,
  onConfirm,
}: ConfirmDialogProps) {
  return (
    <Modal open={open} title={title} onClose={onCancel}>
      <div className="confirm-dialog">
        <p>{message}</p>
        <div>
          <button className="secondary-button" onClick={onCancel} type="button">
            {cancelLabel}
          </button>
          <button className="primary-button" onClick={onConfirm} type="button">
            {confirmLabel}
          </button>
        </div>
      </div>
    </Modal>
  )
}
