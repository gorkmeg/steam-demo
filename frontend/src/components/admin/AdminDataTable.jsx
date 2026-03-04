function PaginationControls({ page, onPageChange, loading }) {
  const currentPage = (page?.number ?? 0) + 1;
  const totalPages = Math.max(page?.totalPages ?? 1, 1);

  return (
    <div className="table-footer">
      <p className="muted small">
        Page {currentPage} of {totalPages} • Total: {page?.totalElements ?? 0}
      </p>
      <div className="table-actions">
        <button
          type="button"
          className="btn-sm"
          onClick={() => onPageChange((page?.number ?? 0) - 1)}
          disabled={loading || !(page?.hasPrevious)}
        >
          Previous
        </button>
        <button
          type="button"
          className="btn-sm"
          onClick={() => onPageChange((page?.number ?? 0) + 1)}
          disabled={loading || !(page?.hasNext)}
        >
          Next
        </button>
      </div>
    </div>
  );
}

export function AdminDataTable({
  title,
  loading,
  rows,
  columns,
  keyField = "id",
  emptyMessage,
  onAction,
  actionLabel = "Delete",
  actionLoadingLabel = "Processing...",
  deletingId,
  page,
  onPageChange
}) {
  return (
    <section className="panel list-panel full-width">
      <div className="list-header">
        <h2>{title}</h2>
      </div>

      <div className="table-container">
        <table className="admin-table">
          <thead>
            <tr>
              {columns.map((column) => (
                <th key={column.key}>{column.label}</th>
              ))}
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={columns.length + 1} className="muted">Loading...</td>
              </tr>
            ) : rows.length === 0 ? (
              <tr>
                <td colSpan={columns.length + 1} className="muted">{emptyMessage}</td>
              </tr>
            ) : (
              rows.map((row) => (
                <tr key={row[keyField]}>
                  {columns.map((column) => (
                    <td key={column.key}>{column.render ? column.render(row[column.key], row) : row[column.key]}</td>
                  ))}
                  <td>
                    <button
                      type="button"
                      className="danger-button"
                      onClick={() => onAction(row)}
                      disabled={deletingId === row[keyField]}
                    >
                      {deletingId === row[keyField] ? actionLoadingLabel : actionLabel}
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <PaginationControls page={page} onPageChange={onPageChange} loading={loading} />
    </section>
  );
}
