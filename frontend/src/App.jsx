import { useEffect, useMemo, useState } from "react";
import { BrowserRouter as Router, Link, Navigate, Route, Routes, useNavigate } from "react-router-dom";

const gameTypeOptions = ["GAME_SIMULATION", "GAME_STRATEGY", "GAME_SPORT"];
const initialGameForm = { name: "", description: "", price: "", gameType: "" };
const initialAuthForm = { username: "", displayName: "", password: "" };

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
  const [form, setForm] = useState({ username: "", password: "" });
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

      if (!response.ok) {
        throw new Error("Login failed.");
      }

      const data = await response.json();
      localStorage.setItem("auth_token", data.token);
      localStorage.setItem("auth_user", JSON.stringify(data.user));
      setToken(data.token);
      setCurrentUser(data.user);
      navigate("/");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <header className="hero">
        <p className="eyebrow">Steam Demo</p>
        <h1>Sign In</h1>
        <p className="sub">Access your store, add funds, and manage your owned games.</p>
      </header>

      <form onSubmit={handleLogin} className="panel form-grid auth-panel">
        <label>
          Username
          <input
            name="username"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            required
          />
        </label>

        <label>
          Password
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
          />
        </label>

        <button disabled={loading}>{loading ? "Connecting..." : "Login"}</button>
        {error ? <p className="error">{error}</p> : null}
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
    setError("");
    setInfo("");

    try {
      const response = await fetch("/api/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form)
      });

      if (!response.ok) {
        throw new Error("Register failed.");
      }

      setInfo("Success! Redirecting to login...");
      setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="auth-container">
      <header className="hero">
        <p className="eyebrow">Steam Demo</p>
        <h1>Create Account</h1>
        <p className="sub">Create your account and start building your game collection.</p>
      </header>

      <form onSubmit={handleRegister} className="panel form-grid auth-panel">
        <label>
          Username
          <input
            name="username"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            required
          />
        </label>

        <label>
          Display Name
          <input
            name="displayName"
            value={form.displayName}
            onChange={(e) => setForm({ ...form, displayName: e.target.value })}
            required
          />
        </label>

        <label>
          Password
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
          />
        </label>

        <button>Register</button>
        {error ? <p className="error">{error}</p> : null}
        {info ? <p className="success">{info}</p> : null}
        <p className="muted">Already have an account? <Link to="/login">Login here</Link></p>
      </form>
    </div>
  );
}

function Dashboard({ token, currentUser, logout }) {
  const [games, setGames] = useState([]);
  const [library, setLibrary] = useState([]);
  const [profile, setProfile] = useState(currentUser);
  const [displayNameInput, setDisplayNameInput] = useState(currentUser?.displayName || "");
  const [updatingDisplayName, setUpdatingDisplayName] = useState(false);
  const [displayNameStatus, setDisplayNameStatus] = useState({ type: "", text: "" });
  const [storeLoading, setStoreLoading] = useState(true);
  const [libraryLoading, setLibraryLoading] = useState(true);
  const [buyingId, setBuyingId] = useState(null);
  const [refundingId, setRefundingId] = useState(null);
  const [balanceAmount, setBalanceAmount] = useState("");
  const [balanceStatus, setBalanceStatus] = useState({ type: "", text: "" });
  const [addingBalance, setAddingBalance] = useState(false);
  const [gameForm, setGameForm] = useState({ ...initialGameForm });
  const navigate = useNavigate();

  const canCreateGame = useMemo(
    () => currentUser?.userType === "ROLE_PUBLISHER" || currentUser?.userType === "ROLE_PRODUCER",
    [currentUser]
  );
  const formattedBalance = useMemo(() => {
    const numericBalance = Number(profile?.balance ?? 0);
    return Number.isFinite(numericBalance) ? numericBalance.toFixed(2) : "0.00";
  }, [profile]);

  const authHeaders = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`
  };

  const handleUnauthorized = (status) => {
    if (status === 401 || status === 403) {
      logout();
      navigate("/login");
      return true;
    }
    return false;
  };

  const loadGames = async () => {
    setStoreLoading(true);
    try {
      const res = await fetch("/api/games", { headers: authHeaders });
      if (handleUnauthorized(res.status)) {
        return;
      }
      if (!res.ok) {
        setGames([]);
        return;
      }
      const data = await res.json();
      setGames(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Load games error:", err);
      setGames([]);
    } finally {
      setStoreLoading(false);
    }
  };

  const loadLibrary = async () => {
    setLibraryLoading(true);
    try {
      const res = await fetch("/api/library", { headers: authHeaders });
      if (handleUnauthorized(res.status)) {
        return;
      }
      if (!res.ok) {
        setLibrary([]);
        return;
      }
      const data = await res.json();
      setLibrary(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Load library error:", err);
      setLibrary([]);
    } finally {
      setLibraryLoading(false);
    }
  };

  const loadProfile = async () => {
    try {
      const res = await fetch("/api/users/me", { headers: authHeaders });
      if (handleUnauthorized(res.status)) {
        return;
      }
      if (!res.ok) {
        return;
      }
      const data = await res.json();
      setProfile(data);
      localStorage.setItem("auth_user", JSON.stringify(data));
    } catch (err) {
      console.error("Load profile error:", err);
    }
  };

  useEffect(() => {
    loadGames();
    loadLibrary();
    loadProfile();
  }, [token]);

  useEffect(() => {
    setProfile(currentUser);
    setDisplayNameInput(currentUser?.displayName || "");
  }, [currentUser]);

  const handleUpdateDisplayName = async (e) => {
    e.preventDefault();
    setDisplayNameStatus({ type: "", text: "" });

    if (!displayNameInput.trim()) {
      setDisplayNameStatus({ type: "error", text: "Display name cannot be empty." });
      return;
    }

    setUpdatingDisplayName(true);
    try {
      const response = await fetch("/api/users/update-display-name", {
        method: "PUT",
        headers: authHeaders,
        body: JSON.stringify({ displayName: displayNameInput })
      });

      if (handleUnauthorized(response.status)) {
        return;
      }

      if (!response.ok) {
        const msg = await response.text();
        setDisplayNameStatus({ type: "error", text: msg || "Could not update display name." });
        return;
      }

      setDisplayNameStatus({ type: "success", text: "Display name updated successfully." });
      await loadProfile();
    } catch (err) {
      setDisplayNameStatus({ type: "error", text: "Error while updating display name." });
    } finally {
      setUpdatingDisplayName(false);
    }
  };

  const handleBuy = async (gameId) => {
    setBuyingId(gameId);
    try {
      const response = await fetch("/api/library", {
        method: "POST",
        headers: authHeaders,
        body: JSON.stringify({ gameId })
      });

      if (response.ok) {
        await loadLibrary();
        await loadProfile();
        alert("Game purchased successfully.");
      } else {
        const msg = await response.text();
        alert(`Purchase failed: ${msg || "Unknown error"}`);
      }
    } catch (err) {
      alert(`Error: ${err.message}`);
    } finally {
      setBuyingId(null);
    }
  };

  const handleAddBalance = async (e) => {
    e.preventDefault();
    setBalanceStatus({ type: "", text: "" });

    const value = Number(balanceAmount);
    if (Number.isNaN(value) || value <= 0) {
      setBalanceStatus({ type: "error", text: "Enter a valid amount greater than 0." });
      return;
    }

    setAddingBalance(true);
    try {
      const response = await fetch("/api/users/add-balance", {
        method: "POST",
        headers: authHeaders,
        body: JSON.stringify({ balance: value })
      });

      if (!response.ok) {
        const msg = await response.text();
        setBalanceStatus({ type: "error", text: msg || "Could not add balance." });
        return;
      }

      setBalanceAmount("");
      setBalanceStatus({ type: "success", text: "Balance added successfully." });
      await loadProfile();
    } catch (err) {
      setBalanceStatus({ type: "error", text: "Error while adding balance." });
    } finally {
      setAddingBalance(false);
    }
  };

  const handleRefund = async (libraryItemId, gameName) => {
    const confirmed = window.confirm(`Refund "${gameName}" and remove it from your library?`);
    if (!confirmed) {
      return;
    }

    setRefundingId(libraryItemId);
    try {
      const response = await fetch(`/api/library/${libraryItemId}`, {
        method: "DELETE",
        headers: authHeaders
      });

      if (handleUnauthorized(response.status)) {
        return;
      }

      if (!response.ok) {
        const msg = await response.text();
        alert(`Refund failed: ${msg || "Unknown error"}`);
        return;
      }

      await loadLibrary();
      await loadProfile();
      alert("Refund completed successfully.");
    } catch (err) {
      alert(`Refund error: ${err.message}`);
    } finally {
      setRefundingId(null);
    }
  };

  const handleCreateGame = async (e) => {
    e.preventDefault();

    if (!canCreateGame) {
      alert("Only publisher or producer can create games.");
      return;
    }

    const response = await fetch("/api/games/create-game", {
      method: "POST",
      headers: authHeaders,
      body: JSON.stringify({ ...gameForm, price: Number(gameForm.price) })
    });

    if (!response.ok) {
      const message = await response.text();
      alert(`Create game failed: ${message || response.status}`);
      return;
    }

    setGameForm({ ...initialGameForm });
    await loadGames();
  };

  return (
    <div className="layout-wrapper">
      <header className="hero dashboard-hero">
        <p className="eyebrow">Control Center</p>
        <div className="user-nav">
          <h1>Welcome, {profile?.displayName || currentUser?.username}</h1>
          <p className="balance-chip">Current Balance: ${formattedBalance}</p>
          <button className="btn-sm" onClick={logout}>Logout</button>
        </div>
        <p className="sub">Browse the store, buy games, and track your personal library in one place.</p>
      </header>

      <main className="layout">
        <section className="side-column">
          <section className="panel balance-panel">
            <h2>Profile</h2>
            <form onSubmit={handleUpdateDisplayName} className="form-grid">
              <input
                type="text"
                placeholder="Display name"
                value={displayNameInput}
                onChange={(e) => setDisplayNameInput(e.target.value)}
                required
              />
              <button type="submit" disabled={updatingDisplayName}>
                {updatingDisplayName ? "Saving..." : "Update Display Name"}
              </button>
            </form>
            {displayNameStatus.text ? (
              <p className={displayNameStatus.type === "error" ? "error" : "success"}>{displayNameStatus.text}</p>
            ) : null}
          </section>

          <section className="panel balance-panel">
            <h2>Add Balance</h2>
            <form onSubmit={handleAddBalance} className="form-grid">
              <input
                type="number"
                min="0.01"
                step="0.01"
                placeholder="Amount"
                value={balanceAmount}
                onChange={(e) => setBalanceAmount(e.target.value)}
                required
              />
              <button type="submit" disabled={addingBalance}>
                {addingBalance ? "Adding..." : "Add Balance"}
              </button>
            </form>
            {balanceStatus.text ? (
              <p className={balanceStatus.type === "error" ? "error" : "success"}>{balanceStatus.text}</p>
            ) : null}
          </section>

          {canCreateGame ? (
            <section className="panel form-panel">
              <h2>Add New Game</h2>
              <form onSubmit={handleCreateGame} className="form-grid">
                <input
                  placeholder="Game Name"
                  value={gameForm.name}
                  onChange={(e) => setGameForm({ ...gameForm, name: e.target.value })}
                  required
                />
                <textarea
                  placeholder="Description"
                  value={gameForm.description}
                  onChange={(e) => setGameForm({ ...gameForm, description: e.target.value })}
                  required
                />
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="Price"
                  value={gameForm.price}
                  onChange={(e) => setGameForm({ ...gameForm, price: e.target.value })}
                  required
                />
                <select
                  value={gameForm.gameType}
                  onChange={(e) => setGameForm({ ...gameForm, gameType: e.target.value })}
                  required
                >
                  <option value="" disabled>Select game type</option>
                  {gameTypeOptions.map((type) => (
                    <option key={type} value={type}>{type.replace("GAME_","")}</option>
                  ))}
                </select>
                <button type="submit">Create Game</button>
              </form>
            </section>
          ) : null}
        </section>

        <section className="panel list-panel">
          <div className="list-header">
            <h2>Store Front</h2>
            <button onClick={loadGames}>Refresh List</button>
          </div>
          {storeLoading ? <p className="muted">Loading games...</p> : null}
          {!storeLoading && games.length === 0 ? <p className="muted">No games found.</p> : null}
          <div className="cards">
            {games.map((game) => (
              <article key={game.id} className="card">
                <div className="card-content">
                  <h3>{game.name}</h3>
                  <p>{game.description}</p>
                  <div className="badge">${game.price} | {game.gameType.replace("GAME_","")}</div>
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

        <section className="panel list-panel full-width">
          <div className="list-header">
            <h2>My Library</h2>
            <button onClick={loadLibrary}>Refresh Library</button>
          </div>
          {libraryLoading ? <p className="muted">Loading library...</p> : null}
          {!libraryLoading && library.length === 0 ? <p className="muted">No games in your library yet.</p> : null}

          <div className="cards">
            {library.map((item) => (
              <article key={item.libraryItemId} className="card">
                <div className="card-content">
                  <h3>{item.gameName}</h3>
                  <p>{item.gameDescription}</p>
                  <div className="badge">${item.gamePrice} | {item.gameType.replace("GAME_","")}</div>
                  <p className="muted small">
                    Added: {item.addedAt ? new Date(item.addedAt).toLocaleString() : "-"}
                  </p>
                </div>
                <button
                  className="buy-button"
                  onClick={() => handleRefund(item.libraryItemId, item.gameName)}
                  disabled={refundingId === item.libraryItemId}
                >
                  {refundingId === item.libraryItemId ? "Refunding..." : "Refund"}
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
