import { useEffect, useMemo, useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, Link, useNavigate } from "react-router-dom";

// --- INITIAL STATES ---
const initialForm = { name: "", description: "", price: "", gameType: "", publisherId: "", producerId: "" };
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
            {/* Kamu Sayfaları */}
            <Route
                path="/login"
                element={!token ? <LoginPage setToken={setToken} setCurrentUser={setCurrentUser} /> : <Navigate to="/" />}
            />
            <Route
                path="/register"
                element={!token ? <RegisterPage setToken={setToken} setCurrentUser={setCurrentUser} /> : <Navigate to="/" />}
            />

            {/* Korumalı Sayfalar (Sadece Login Olanlar) */}
            <Route
                path="/"
                element={token ? <Dashboard token={token} currentUser={currentUser} logout={logout} /> : <Navigate to="/login" />}
            />
          </Routes>
        </div>
      </Router>
  );
}

// --- SAYFA BİLEŞENLERİ ---

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
      if (!response.ok) throw new Error("Giriş başarısız.");
      const data = await response.json();
      localStorage.setItem("auth_token", data.token);
      localStorage.setItem("auth_user", JSON.stringify(data.user));
      setToken(data.token);
      setCurrentUser(data.user);
      navigate("/");
    } catch (e) { setError(e.message); }
    finally { setLoading(false); }
  };

  return (
      <div className="auth-container">
        <header className="hero"><h1>Login</h1></header>
        <form onSubmit={handleLogin} className="panel form-grid">
          <label>Username <input name="username" onChange={e => setForm({...form, username: e.target.value})} required /></label>
          <label>Password <input type="password" name="password" onChange={e => setForm({...form, password: e.target.value})} required /></label>
          <button disabled={loading}>{loading ? "Connecting..." : "Login"}</button>
          {error && <p className="error">{error}</p>}
          <p className="muted">Don't have an account? <Link to="/register">Register here</Link></p>
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
      if (!response.ok) throw new Error("Kayıt başarısız.");
      setInfo("Success! Redirecing to login...");
      setTimeout(() => navigate("/login"), 2000);
    } catch (e) { setError(e.message); }
  };

  return (
      <div className="auth-container">
        <header className="hero"><h1>Create Account</h1></header>
        <form onSubmit={handleRegister} className="panel form-grid">
          <label>Username <input name="username" onChange={e => setForm({...form, username: e.target.value})} required /></label>
          <label>Password <input type="password" name="password" onChange={e => setForm({...form, password: e.target.value})} required /></label>
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
  const [buyingId, setBuyingId] = useState(null); // Satın alma efekti için
  const [form, setForm] = useState({ ...initialForm, publisherId: currentUser?.id, producerId: currentUser?.id });

  const authHeaders = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`
  };

  const loadGames = async () => {
    setLoading(true);
    try {
      const res = await fetch("/api/games", { headers: authHeaders });
      const data = await res.json();
      setGames(data);
    } catch (e) { console.error("Load error:", e); }
    finally { setLoading(false); }
  };

  useEffect(() => { loadGames(); }, []);

  // --- SATIN ALMA FONKSİYONU ---
  const handleBuy = async (gameId) => {
    setBuyingId(gameId);
    try {
      const response = await fetch(`/api/library`, {
        method: "POST",
        headers: authHeaders,
        body: JSON.stringify({
          gameId: gameId,
          userId: currentUser.id
        })
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
    await fetch("/api/games", {
      method: "POST",
      headers: authHeaders,
      body: JSON.stringify({ ...form, price: Number(form.price) })
    });
    setForm({ ...initialForm, publisherId: currentUser?.id, producerId: currentUser?.id });
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
          <section className="panel form-panel">
            <h2>Add New Game</h2>
            <form onSubmit={handleCreate} className="form-grid">
              <input placeholder="Game Name" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
              <textarea placeholder="Description" value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} required />
              <input type="number" placeholder="Price" value={form.price} onChange={e => setForm({ ...form, price: e.target.value })} required />
              <input placeholder="Type (e.g. ACTION)" value={form.gameType} onChange={e => setForm({ ...form, gameType: e.target.value })} required />
              <button type="submit" className="btn-success">Create Game</button>
            </form>
          </section>

          <section className="panel list-panel">
            <div className="list-header">
              <h2>Store Front</h2>
              <button onClick={loadGames}>Refresh List</button>
            </div>
            <div className="cards">
              {games.map(game => (
                  <article key={game.id} className="card">
                    <div className="card-content">
                      <h3>{game.name}</h3>
                      <p>{game.description}</p>
                      <div className="badge">${game.price} - {game.gameType}</div>
                    </div>

                    {/* SATIN AL BUTONU */}
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