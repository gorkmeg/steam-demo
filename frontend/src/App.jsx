import { useEffect, useMemo, useState } from "react";

const initialForm = {
  name: "",
  description: "",
  price: "",
  gameType: "",
  publisherId: "",
  producerId: ""
};

function App() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [form, setForm] = useState(initialForm);

  const hasGames = useMemo(() => games.length > 0, [games.length]);

  async function loadGames() {
    setLoading(true);
    setError("");
    try {
      const response = await fetch("/api/games");
      if (!response.ok) {
        throw new Error(`Failed to load games (${response.status})`);
      }
      const data = await response.json();
      setGames(data);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadGames();
  }, []);

  async function handleCreateGame(event) {
    event.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = {
        ...form,
        price: Number(form.price),
        gameType: form.gameType.trim()
      };
      const response = await fetch("/api/games", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      });
      if (!response.ok) {
        const message = await response.text();
        throw new Error(message || `Failed to create game (${response.status})`);
      }
      setForm(initialForm);
      await loadGames();
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(false);
    }
  }

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  return (
    <div className="page">
      <div className="ambient ambient-left" />
      <div className="ambient ambient-right" />
      <header className="hero">
        <p className="eyebrow">Steam Demo</p>
        <h1>Game Library Console</h1>
        <p className="sub">
          Manage games, pricing, and publisher or producer links from one panel.
        </p>
      </header>

      <main className="layout">
        <section className="panel form-panel">
          <h2>Add New Game</h2>
          <form onSubmit={handleCreateGame} className="form-grid">
            <label>
              Name
              <input name="name" value={form.name} onChange={handleChange} required />
            </label>
            <label>
              Description
              <textarea
                name="description"
                value={form.description}
                onChange={handleChange}
                rows={4}
                required
              />
            </label>
            <label>
              Price
              <input
                name="price"
                value={form.price}
                onChange={handleChange}
                type="number"
                min="0"
                step="0.01"
                required
              />
            </label>
            <label>
              Game Type
              <input
                name="gameType"
                value={form.gameType}
                onChange={handleChange}
                placeholder="ACTION"
                required
              />
            </label>
            <label>
              Publisher ID
              <input
                name="publisherId"
                value={form.publisherId}
                onChange={handleChange}
                placeholder="UUID"
                required
              />
            </label>
            <label>
              Producer ID
              <input
                name="producerId"
                value={form.producerId}
                onChange={handleChange}
                placeholder="UUID"
                required
              />
            </label>
            <button type="submit" disabled={saving}>
              {saving ? "Saving..." : "Create Game"}
            </button>
          </form>
        </section>

        <section className="panel list-panel">
          <div className="list-header">
            <h2>Games</h2>
            <button onClick={loadGames} disabled={loading}>
              Refresh
            </button>
          </div>

          {error ? <p className="error">{error}</p> : null}
          {loading ? <p className="muted">Loading games...</p> : null}
          {!loading && !hasGames ? <p className="muted">No games yet.</p> : null}

          <div className="cards">
            {games.map((game) => (
              <article key={game.id} className="card">
                <h3>{game.name}</h3>
                <p>{game.description}</p>
                <dl>
                  <div>
                    <dt>Price</dt>
                    <dd>${game.price}</dd>
                  </div>
                  <div>
                    <dt>Type</dt>
                    <dd>{game.gameType}</dd>
                  </div>
                  <div>
                    <dt>Publisher</dt>
                    <dd>{game.publisher?.username || game.publisher?.id || "-"}</dd>
                  </div>
                  <div>
                    <dt>Producer</dt>
                    <dd>{game.producer?.username || game.producer?.id || "-"}</dd>
                  </div>
                </dl>
              </article>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}

export default App;
