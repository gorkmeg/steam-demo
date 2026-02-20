import { useEffect, useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, Link, useNavigate } from "react-router-dom";

const gameTypeOptions = ["GAME_SIMULATION", "GAME_STRATEGY", "GAME_SPORT"];
const initialForm = { name: "", description: "", price: "", gameType: "" };
const initialAuthForm = { username: "", password: "" };

function App() {
  const [token, setToken] = useState(() => localStorage.getItem("auth_token") || "");
  const [currentUser, setCurrentUser] = useState(() => {
    const raw = localStorage.getItem("auth_user");
    return raw ? JSON.parse(raw) : null;
  });

  const logout = () => {
    localStorage.removeItem("auth_token");
    localStorage.removeItem("auth_user");
    setToken("");
    setCurrentUser(null);
  };

  return (
    <Router>
      <div className="page">
        <div className="ambient ambient-left" />
        <div className="ambient ambient-right" />

        <Routes>
          <Route
            path="/login"
            element={!token ? <LoginPage setToken={setToken} setCurrentUser={setCurrentUser} /> : <Navigate to="/" />}
          />
          <Route
            path="/register"
            element={!token ? <RegisterPage /> : <Navigate to="/" />}
          />

          <Route
            path="/"
            element={token ? <Dashboard token={token} currentUser={currentUser} logout={logout} /> : <Navigate to="/login" />}
          />
        </Routes>
      </div>
    </Router>
  );
}

function LoginPage({ setToken, setCurrentUser }) {
  const [form, setForm] = useState(initialAuthForm);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const response = await fetch("/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form)
      });
      if (!response.ok) throw new Error("Login failed.");
      const data = await response.json();
      localStorage.setItem("auth_token", data.token);
      localStorage.setItem("auth_user", JSON.stringify(data.user));
      setToken(data.token);
      setCurrentUser(data.user);
      navigate("/");
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <header className="hero"><h1>Login</h1></header>
      <form onSubmit={handleLogin} className="panel form-grid">
        <label>Username <input name="username" onChange={(e) => setForm({ ...form, username: e.target.value })} required /></label>
        <label>Password <input type="password" name="password" onChange={(e) => setForm({ ...form, password: e.target.value })} required /></label>
        <button disabled={loading}>{loading ? "Connecting..." : "Login"}</button>
        {error && <p className="error">{error}</p>}
        <p className="muted">Don&apos;t have an account? <Link to="/register">Register here</Link></p>
      </form>
    </div>
  );
}

function RegisterPage() {
  const [form, setForm] = useState(initialAuthForm);
  const [error, setError] = useState("");
  const [info, setInfo] = useState("");
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch("/api/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form)
      });
      if (!response.ok) throw new Error("Register failed.");
      setInfo("Success! Redirecting to login...");
      setTimeout(() => navigate("/login"), 2000);
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div className="auth-container">
      <header className="hero"><h1>Create Account</h1></header>
      <form onSubmit={handleRegister} className="panel form-grid">
        <label>Username <input name="username" onChange={(e) => setForm({ ...form, username: e.target.value })} required /></label>
        <label>Password <input type="password" name="password" onChange={(e) => setForm({ ...form, password: e.target.value })} required /></label>
        <button>Register</button>
        {error && <p className="error">{error}</p>}
        {info && <p className="success">{info}</p>}
        <p className="muted">Already have an account? <Link to="/login">Login here</Link></p>
      </form>
    </div>
  );
}

function Dashboard({ token, currentUser, logout }) {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [buyingId, setBuyingId] = useState(null);
  const [form, setForm] = useState({ ...initialForm });
  const navigate = useNavigate();
  const canCreateGame =
    currentUser?.userType === "ROLE_PUBLISHER" || currentUser?.userType === "ROLE_PRODUCER";

  const authHeaders = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`
  };

  const loadGames = async () => {
    setLoading(true);
    try {
      const res = await fetch("/api/games", { headers: authHeaders });
      if (res.status === 401 || res.status === 403) {
        logout();
        navigate("/login");
        return;
      }
      if (!res.ok) {
        setGames([]);
        return;
      }

      const text = await res.text();
      const data = text ? JSON.parse(text) : [];
      setGames(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error("Load error:", e);
      setGames([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadGames();
  }, []);

  const handleBuy = async (gameId) => {
    setBuyingId(gameId);
    try {
      const response = await fetch(`/api/library`, {
        method: "POST",
        headers: authHeaders,
        body: JSON.stringify({ gameId, userId: currentUser.id })
      });

      if (response.ok) {
        alert("Game purchased successfully! Check your library.");
      } else {
        const msg = await response.text();
        alert("Purchase failed: " + msg);
      }
    } catch (e) {
      alert("Error: " + e.message);
    } finally {
      setBuyingId(null);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!canCreateGame) {
      alert("Only publisher or producer can create games.");
      return;
    }
    const response = await fetch("/api/games", {
      method: "POST",
      headers: authHeaders,
      body: JSON.stringify({ ...form, price: Number(form.price) })
    });

    if (!response.ok) {
      const message = await response.text();
      alert("Create game failed: " + (message || response.status));
      return;
    }

    setForm({ ...initialForm });
    loadGames();
  };

  return (
    <div className="layout-wrapper">
      <header className="hero">
        <div className="user-nav">
          <span>Welcome, <strong>{currentUser?.username}</strong></span>
          <button className="btn-sm" onClick={logout}>Logout</button>
        </div>
        <h1>Game Library</h1>
      </header>

      <main className="layout">
        {canCreateGame ? (
          <section className="panel form-panel">
            <h2>Add New Game</h2>
            <form onSubmit={handleCreate} className="form-grid">
              <input placeholder="Game Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
              <textarea placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} required />
              <input type="number" placeholder="Price" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} required />
              <select value={form.gameType} onChange={(e) => setForm({ ...form, gameType: e.target.value })} required>
                <option value="" disabled>Select game type</option>
                {gameTypeOptions.map((type) => (
                  <option key={type} value={type}>{type}</option>
                ))}
              </select>
              <button type="submit" className="btn-success">Create Game</button>
            </form>
          </section>
        ) : null}

        <section className="panel list-panel">
          <div className="list-header">
            <h2>Store Front</h2>
            <button onClick={loadGames}>Refresh List</button>
          </div>
          {loading ? <p>Loading games...</p> : null}
          <div className="cards">
            {games.map((game) => (
              <article key={game.id} className="card">
                <div className="card-content">
                  <h3>{game.name}</h3>
                  <p>{game.description}</p>
                  <div className="badge">${game.price} - {game.gameType}</div>
                </div>

                <button
                  className="buy-button"
                  onClick={() => handleBuy(game.id)}
                  disabled={buyingId === game.id}
                >
                  {buyingId === game.id ? "Processing..." : "Buy Game"}
                </button>
              </article>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}

export default App;
