# 3. Backend Low-Level Design (LLD)

## 3.1 Database Schema (PostgreSQL)
The database uses a relational model normalized to the Third Normal Form (3NF) to ensure data integrity.

### Entity Relationship Diagram (ERD) Description
* **Users:** Stores account credentials and profile info.
* **Trades:** Stores individual transaction records.
* **Relationship:** One-to-Many (1:N) between `Users` and `Trades`.

### Table Specifications

#### Table: `users`
| Column | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PK, Not Null | Unique User Identifier |
| `email` | VARCHAR(255) | Unique, Not Null | User login email |
| `password_hash` | VARCHAR(255) | Not Null | BCrypt hashed password |
| `created_at` | TIMESTAMP | Default NOW() | Account creation date |

#### Table: `trades`
| Column | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PK, Auto Increment | Unique Trade Identifier |
| `user_id` | UUID | FK -> users(id) | Owner of the trade |
| `ticker` | VARCHAR(10) | Not Null, Index | Stock Symbol (e.g., AAPL) |
| `type` | VARCHAR(4) | ENUM('BUY', 'SELL') | Direction of trade |
| `quantity` | INTEGER | Check > 0 | Number of units traded |
| `price` | DECIMAL(10, 2) | Not Null | Price per unit at execution |
| `timestamp` | TIMESTAMP | Not Null | Time of trade execution |

---

## 3.2 API Interface Specifications
The Backend exposes a RESTful API compliant with Level 2 Maturity Model (HTTP Verbs).

### 1. Record New Trade
* **Endpoint:** `POST /api/v1/trades`
* **Description:** Logs a new buy/sell order. If the price is omitted, it triggers a real-time fetch.
* **Request Body:**
    ```json
    {
      "ticker": "NVDA",
      "type": "BUY",
      "quantity": 10,
      "price": 0.0  // Optional: 0.0 triggers auto-fetch
    }
    ```
* **Processing Logic:**
    1.  Validate `quantity > 0`.
    2.  If `price == 0.0`: Call `StockService.getCurrentPrice("NVDA")`.
    3.  Save entity to DB.
* **Response (201 Created):**
    ```json
    {
      "id": 101,
      "status": "CONFIRMED",
      "execution_price": 485.50
    }
    ```

### 2. Get Portfolio Summary
* **Endpoint:** `GET /api/v1/portfolio`
* **Description:** Returns the aggregated current value of the user's holdings.
* **Processing Logic:**
    1.  Fetch all trades for `current_user`.
    2.  Calculate Net Quantity per Ticker.
    3.  Fetch real-time price for each Ticker via Market Engine.
    4.  Compute `(Net Qty * Current Price)` for each.
    5.  Sum total value.

---

## 3.3 Internal Class Design (Java Spring Boot)

The application follows the standard **Controller-Service-Repository** pattern.

### Core Classes

1.  **`TradeController`**
    * **Role:** Handles HTTP requests, parses JSON, and performs input validation.
    * **Annotation:** `@RestController`, `@RequestMapping("/api")`

2.  **`PortfolioService` (The Business Logic)**
    * **Role:** Orchestrates the flow between Data and Market Service.
    * **Key Method:** `calculateTotalValue()`
        * *Logic:* Iterates strictly through valid trades and handles exceptions if the Market Service is down (Fallback mechanism).

3.  **`StockService` (Interface)**
    * **Role:** Abstraction layer for price fetching. Allows us to swap implementations for Testing vs. Production.
    * **Implementations:**
        * `RealStockService`: Uses `RestTemplate` to call Python Service.
        * `MockStockService`: Returns static data (Used in CI/CD Unit Tests).

4.  **`TradeRepository`**
    * **Role:** Direct interface to PostgreSQL.
    * **Annotation:** `@Repository`, extends `JpaRepository<Trade, Long>`.
    