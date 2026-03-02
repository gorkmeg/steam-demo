export function ConfirmModal({ open, title, message, onCancel, onConfirm, confirmText = "Delete", loading = false }) {
  if (!open) {
    return null;
  }

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="confirm-modal-title">
      <div className="modal-card">
        <h3 id="confirm-modal-title">{title}</h3>
        <p className="muted">{message}</p>
        <div className="modal-actions">
          <button type="button" className="btn-sm" onClick={onCancel} disabled={loading}>
            Cancel
          </button>
          <button type="button" className="danger-button" onClick={onConfirm} disabled={loading}>
            {loading ? "Deleting..." : confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
