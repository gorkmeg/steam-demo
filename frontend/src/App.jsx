import { useEffect, useMemo, useState } from "react";

const initialForm = {
  name: "",
  description: "",
  price: "",
  gameType: "",
  publisherId: "",
  producerId: ""
};

const initialAuthForm = {
  username: "",
  password: ""
};

function App() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [gameError, setGameError] = useState("");
  const [form, setForm] = useState(initialForm);
  const [registerForm, setRegisterForm] = useState(initialAuthForm);
  const [loginForm, setLoginForm] = useState(initialAuthForm);
  const [registering, setRegistering] = useState(false);
  const [loggingIn, setLoggingIn] = useState(false);
  const [authError, setAuthError] = useState("");
  const [authInfo, setAuthInfo] = useState("");
  const [currentUser, setCurrentUser] = useState(null);
  const [token, setToken] = useState(() => localStorage.getItem("auth_token") || "");

  const hasGames = useMemo(() => games.length > 0, [games.length]);

  function authHeaders() {
    if (!token) {
      return {};
    }
    return { Authorization: `Bearer ${token}` };
  }

  async function loadGames() {
    if (!token) {
      setGames([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setGameError("");
    try {
      const response = await fetch("/api/games", {
        headers: authHeaders()
      });
      if (!response.ok) {
        throw new Error(`Failed to load games (${response.status})`);
      }
      const data = await response.json();
      setGames(data);
    } catch (e) {
      setGameError(e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (!token) {
      setLoading(false);
      return;
    }
    loadGames();
  }, [token]);

  async function handleCreateGame(event) {
    event.preventDefault();
    if (!token) {
      setGameError("Login is required before creating a game.");
      return;
    }
    setSaving(true);
    setGameError("");
    try {
      const payload = {
        ...form,
        price: Number(form.price),
        gameType: form.gameType.trim()
      };
      const response = await fetch("/api/games", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...authHeaders()
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
      setGameError(e.message);
    } finally {
      setSaving(false);
    }
  }

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  function handleRegisterChange(event) {
    const { name, value } = event.target;
    setRegisterForm((prev) => ({ ...prev, [name]: value }));
  }

  function handleLoginChange(event) {
    const { name, value } = event.target;
    setLoginForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleRegister(event) {
    event.preventDefault();
    setRegistering(true);
    setAuthError("");
    setAuthInfo("");
    try {
      const response = await fetch("/api/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(registerForm)
      });
      if (!response.ok) {
        const message = await response.text();
        throw new Error(message || `Register failed (${response.status})`);
      }
      const user = await response.json();
      setCurrentUser(user);
      setAuthInfo(`Registered as ${user.username}`);
      setRegisterForm(initialAuthForm);
      setForm((prev) => ({
        ...prev,
        publisherId: prev.publisherId || user.id,
        producerId: prev.producerId || user.id
      }));
    } catch (e) {
      setAuthError(e.message);
    } finally {
      setRegistering(false);
    }
  }

  async function handleLogin(event) {
    event.preventDefault();
    setLoggingIn(true);
    setAuthError("");
    setAuthInfo("");
    try {
      const response = await fetch("/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginForm)
      });
      if (!response.ok) {
        const message = await response.text();
        throw new Error(message || `Login failed (${response.status})`);
      }
      const data = await response.json();
      localStorage.setItem("auth_token", data.token);
      setToken(data.token);
      setCurrentUser(data.user);
      setAuthInfo(`Logged in as ${data.user.username}`);
      setLoginForm(initialAuthForm);
      setForm((prev) => ({
        ...prev,
        publisherId: prev.publisherId || data.user.id,
        producerId: prev.producerId || data.user.id
      }));
    } catch (e) {
      setAuthError(e.message);
    } finally {
      setLoggingIn(false);
    }
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

      <section className="panel auth-panel">
        <div className="list-header">
          <h2>Account</h2>
          {currentUser ? <p className="muted">Current: {currentUser.username}</p> : null}
        </div>
        <div className="auth-grid">
          <form onSubmit={handleRegister} className="form-grid">
            <h3>Register</h3>
            <label>
              Username
              <input
                name="username"
                value={registerForm.username}
                onChange={handleRegisterChange}
                required
              />
            </label>
            <label>
              Password
              <input
                type="password"
                name="password"
                value={registerForm.password}
                onChange={handleRegisterChange}
                required
              />
            </label>
            <button type="submit" disabled={registering}>
              {registering ? "Registering..." : "Register"}
            </button>
          </form>

          <form onSubmit={handleLogin} className="form-grid">
            <h3>Login</h3>
            <label>
              Username
              <input
                name="username"
                value={loginForm.username}
                onChange={handleLoginChange}
                required
              />
            </label>
            <label>
              Password
              <input
                type="password"
                name="password"
                value={loginForm.password}
                onChange={handleLoginChange}
                required
              />
            </label>
            <button type="submit" disabled={loggingIn}>
              {loggingIn ? "Logging in..." : "Login"}
            </button>
          </form>
        </div>
        {authError ? <p className="error">{authError}</p> : null}
        {authInfo ? <p className="muted">{authInfo}</p> : null}
        {!token ? <p className="muted">Login required for game operations.</p> : null}
      </section>

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

          {gameError ? <p className="error">{gameError}</p> : null}
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
