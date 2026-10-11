import { useState, useEffect } from "react";
import "./App.css";

// Backend address. Locally it's port 8080; after deployment we set VITE_API_URL.
const API = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

// Emoji "stickers": picked by looking for a keyword in the name
const INGREDIENT_EMOJI = [
  ["banana", "🍌"], ["tomato", "🍅"], ["egg", "🥚"], ["onion", "🧅"],
  ["potato", "🥔"], ["aloo", "🥔"], ["garlic", "🧄"], ["milk", "🥛"],
  ["flour", "🌾"], ["sugar", "🍬"], ["butter", "🧈"], ["salt", "🧂"],
  ["chilli", "🌶️"], ["paneer", "🧀"], ["cheese", "🧀"], ["dal", "🫘"],
  ["cumin", "🌿"], ["turmeric", "🟡"], ["oil", "🫒"], ["soda", "🥄"],
  ["rice", "🍚"], ["carrot", "🥕"], ["apple", "🍎"], ["lemon", "🍋"],
];

function pick(list, name, fallback) {
  const n = name.toLowerCase();
  const found = list.find(([key]) => n.includes(key));
  return found ? found[1] : fallback;
}

const ingredientEmoji = (name) => pick(INGREDIENT_EMOJI, name, "🍽️");

const RECIPE_EMOJI = [
  ["bread", "🍞"], ["smoothie", "🥤"], ["omelette", "🍳"], ["aloo", "🥔"],
  ["dal", "🍲"], ["paneer", "🧀"],
];
const recipeEmoji = (title) => pick(RECIPE_EMOJI, title, "🍽️");

// How many days from today until a date like "2026-10-12"
function daysLeft(dateStr) {
  if (!dateStr) return null;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const expiry = new Date(dateStr + "T00:00:00");
  return Math.round((expiry - today) / 86400000);
}

function expiryLabel(dateStr) {
  const d = daysLeft(dateStr);
  if (d === null) return "no expiry date";
  if (d < 0) return "expired";
  if (d === 0) return "expires today";
  if (d === 1) return "expires tomorrow";
  return `${d} days left`;
}

// Smart Alerts: bell icon with a count badge and a dropdown list
function AlertBell({ items }) {
  const [open, setOpen] = useState(false);
  return (
    <div className="bell-wrap">
      <button className="bell" onClick={() => setOpen(!open)}>
        🔔
        {items.length > 0 && <span className="badge">{items.length}</span>}
      </button>
      {open && (
        <div className="dropdown">
          <h4>Expiring soon</h4>
          {items.length === 0 ? (
            <p>Nothing expiring soon 🎉</p>
          ) : (
            items.map((i) => (
              <p key={i.id}>
                {ingredientEmoji(i.name)} <b>{i.name}</b> – {expiryLabel(i.expiryDate)}
              </p>
            ))
          )}
        </div>
      )}
    </div>
  );
}

// Form to add a pantry item (controlled inputs: React state holds the values)
function PantryForm({ onAdd }) {
  const empty = { name: "", quantity: "", unit: "pcs", expiryDate: "" };
  const [form, setForm] = useState(empty);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name.trim() || !form.quantity) return;
    await onAdd({
      name: form.name.trim(),
      quantity: Number(form.quantity),
      unit: form.unit,
      expiryDate: form.expiryDate || null,
    });
    setForm(empty);
  };

  return (
    <form className="form" onSubmit={handleSubmit}>
      <input className="full" name="name" placeholder="Ingredient (e.g. banana)" value={form.name} onChange={handleChange} />
      <input name="quantity" type="number" min="0" step="any" placeholder="Quantity" value={form.quantity} onChange={handleChange} />
      <select name="unit" value={form.unit} onChange={handleChange}>
        {["pcs", "g", "kg", "ml", "l", "cup", "katori", "tbsp", "tsp"].map((u) => (
          <option key={u}>{u}</option>
        ))}
      </select>
      <input className="full" name="expiryDate" type="date" value={form.expiryDate} onChange={handleChange} />
      <button type="submit">Add to pantry</button>
    </form>
  );
}

function PantryList({ items, onDelete }) {
  if (items.length === 0) return <p className="empty">Your pantry is empty. Add something above!</p>;
  return items.map((i) => {
    const d = daysLeft(i.expiryDate);
    const cls = d !== null && d < 0 ? "expired" : d !== null && d <= 2 ? "soon" : "";
    return (
      <div key={i.id} className={`item ${cls}`}>
        <div className="item-left">
          <span className="item-emoji">{ingredientEmoji(i.name)}</span>
          <div>
            <div className="item-name">{i.name}</div>
            <div className="item-sub">
              {i.quantity} {i.unit} · {expiryLabel(i.expiryDate)}
            </div>
          </div>
        </div>
        <button className="del" onClick={() => onDelete(i.id)} title="Remove">✕</button>
      </div>
    );
  });
}

// One recipe card. It has its own state to show/hide the cooking steps.
function RecipeCard({ m }) {
  const [open, setOpen] = useState(false);
  return (
    <div className="match">
      <div className="match-top">
        <div className="match-title">
          <div className="sticker">{recipeEmoji(m.title)}</div>
          <h3>{m.title}</h3>
        </div>
        <span className="percent">{m.matchPercent}% match</span>
      </div>
      <div className="bar">
        <div className="bar-fill" style={{ width: `${m.matchPercent}%` }} />
      </div>

      {m.usesExpiring.length > 0 && (
        <div>
          <p className="label">Uses ingredients about to expire:</p>
          {m.usesExpiring.map((n) => (
            <span key={n} className="tag expiring">⏰ {ingredientEmoji(n)} {n}</span>
          ))}
        </div>
      )}
      {m.have.length > 0 && (
        <div>
          <p className="label">You have:</p>
          {m.have.map((n) => (
            <span key={n} className="tag have">{ingredientEmoji(n)} {n}</span>
          ))}
        </div>
      )}
      {m.missing.length > 0 && (
        <div>
          <p className="label">Missing:</p>
          {m.missing.map((n) => (
            <span key={n} className="tag missing">{ingredientEmoji(n)} {n}</span>
          ))}
        </div>
      )}

      <button className="cook-btn" onClick={() => setOpen(!open)}>
        {open ? "Hide steps" : "How to cook"}
      </button>
      {open && <p className="steps">{m.instructions}</p>}
    </div>
  );
}

function MatchList({ matches }) {
  if (matches.length === 0) return <p className="empty">No recipes found.</p>;
  return matches.map((m) => <RecipeCard key={m.recipeId} m={m} />);
}

export default function App() {
  const [pantry, setPantry] = useState([]);
  const [expiring, setExpiring] = useState([]);
  const [matches, setMatches] = useState([]);
  const [error, setError] = useState("");

  // Fetch everything from the backend
  const loadAll = async () => {
    try {
      const [p, e, m] = await Promise.all([
        fetch(`${API}/pantry`).then((r) => r.json()),
        fetch(`${API}/pantry/expiring`).then((r) => r.json()),
        fetch(`${API}/recipes/match`).then((r) => r.json()),
      ]);
      setPantry(p);
      setExpiring(e);
      setMatches(m);
      setError("");
    } catch {
      setError("Can't reach the backend. Is it running on port 8080?");
    }
  };

  // Runs once when the page first loads
  useEffect(() => {
    loadAll();
  }, []);

  const addItem = async (item) => {
    const res = await fetch(`${API}/pantry`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(item),
    });
    if (!res.ok) setError("Could not add that item. Check the values and try again.");
    await loadAll();
  };

  const deleteItem = async (id) => {
    await fetch(`${API}/pantry/${id}`, { method: "DELETE" });
    await loadAll();
  };

  return (
    <div className="app">
      <div className="header">
        <div className="title-box">
          <h1>Rasoi Jugaad</h1>
          <p className="tagline">Cook smart with what's already in your kitchen</p>
        </div>
        <AlertBell items={expiring} />
      </div>

      {error && <div className="error">{error}</div>}

      <div className="columns">
        <div className="card">
          <h2>My Pantry</h2>
          <PantryForm onAdd={addItem} />
          <PantryList items={pantry} onDelete={deleteItem} />
        </div>
        <div className="card">
          <h2>What can I cook?</h2>
          <MatchList matches={matches} />
        </div>
      </div>
    </div>
  );
}