# Rasoi Jugaad 🍌

**Cook smart with what's already in your kitchen.**

Rasoi Jugaad is a full-stack web app that works backwards from your pantry. Instead of picking a recipe and then shopping for it, you log what you already have, and the app tells you what you can cook, what you're missing, and which recipes use ingredients that are about to expire.

## Problem Statement
Households waste food because they forget what's in their pantry, and most recipe apps start from the recipe rather than from what you own. Rasoi Jugaad starts from the pantry and prioritises ingredients close to expiry, to reduce food waste.

## Features
- **Pantry manager:** add and delete items with quantity, unit (including *katori*) and expiry date
- **Recipe matching:** every recipe gets a match percentage, with lists of ingredients you have and are missing
- **Expiry-aware ranking:** recipes that use ingredients expiring within 2 days rank higher (banana bread surfaces when bananas are overripe)
- **Smart Alerts:** a bell icon with a badge count showing items expiring soon, computed live
- **Seeded recipe catalogue** with Indian-leaning recipes

## Tech Stack
| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot (Web, Data JPA, Validation) |
| Database | PostgreSQL |
| Frontend | React (Vite), plain CSS |

## Architecture
React frontend → REST API (Spring Boot) → PostgreSQL

The backend follows a layered design: **Controller → Service → Repository → Database**.

## Database Design
- `pantry_item`: id, name, quantity, unit, expiry_date
- `recipe`: id, title, instructions
- `recipe_ingredient`: id, ingredient_name, quantity, unit, recipe_id (foreign key)

`Recipe` to `RecipeIngredient` is a **one-to-many** relationship.

## Matching Algorithm
1. Normalise names (lowercase, trim, strip plurals) so "Tomatoes" matches "tomato"
2. Ignore pantry items that are already expired
3. For each recipe, compare its ingredients with the pantry to get `have`, `missing` and `usesExpiring` lists
4. Match % = ingredients owned ÷ total ingredients
5. Rank by match % plus a 15-point bonus per ingredient that expires within 2 days

## API Endpoints
| Method | URL | Purpose |
|---|---|---|
| GET | `/api/pantry` | List pantry items |
| POST | `/api/pantry` | Add an item |
| DELETE | `/api/pantry/{id}` | Remove an item |
| GET | `/api/pantry/expiring` | Items expiring within 2 days |
| GET | `/api/recipes` | List all recipes |
| GET | `/api/recipes/match` | Ranked recipe matches for the current pantry |

## Run Locally
**Prerequisites:** Java 21+, Maven, Node.js, PostgreSQL

1. Create a database: `CREATE DATABASE rasoi_jugaad;`
2. In `backend/`, create a `.env` file containing `DB_PASSWORD=your_password`. Adjust the port in `application.properties` if your Postgres isn't on 7777.
3. Start the backend:
```
   cd backend
   mvn spring-boot:run
```
4. Start the frontend:
```
   cd frontend
   npm install
   npm run dev
```
5. Open http://localhost:5173

## Future Enhancements
- User login with JWT authentication
- Scheduled background job and push notifications for expiring items
- Fuzzy and synonym matching (e.g. *tamatar* = tomato) and full unit conversion
- Cook mode with step-by-step timers
