# 4. OpenBB Market Engine Service

## 4.1 Overview
The **Market Engine** is a dedicated microservice responsible for interacting with external financial markets. It acts as a "Data Oracle" for the internal ecosystem, abstracting the complexity of third-party APIs (Yahoo Finance, OpenBB SDK) into a simple, standardized internal REST interface.

### Why a Separate Microservice?
1.  **Isolation of Dependencies:** Financial data libraries (like `pandas`, `numpy`, `openbb`) are heavy (hundreds of MBs). By isolating them in a Python service, we keep the main Java application lightweight (~150MB) and fast to startup.
2.  **Polyglot Advantage:** Python is the native language of quantitative finance. It allows us to leverage powerful libraries that don't exist in Java, while still keeping the core transaction logic in the type-safe Java environment.
3.  **Independent Scaling:** If we need to fetch data for thousands of tickers, we can scale this service horizontally (e.g., spin up 5 Python containers) without touching the Java ledger service.

## 4.2 Technical Stack
* **Runtime:** Python 3.9 Slim (Optimized for Docker)
* **Framework:** Flask (Lightweight REST API)
* **Data Source:** `yfinance` (A robust, open-source wrapper for Yahoo Finance API, used here as a lightweight alternative to the full OpenBB SDK for cloud deployment efficiency).
* **Server:** Gunicorn (Production-grade WSGI HTTP Server).

## 4.3 API Contract

### Get Stock Price
* **Endpoint:** `GET /price/<ticker>`
* **Description:** Fetches the real-time (or latest available) closing price for a given stock symbol.
* **Method:** `GET`
* **URL Params:** `ticker` (String, e.g., `AAPL`, `BTC-USD`)

#### Success Response (200 OK)
```json
{
  "ticker": "AAPL",
  "price": 185.92,
  "currency": "USD",
  "timestamp": "2024-01-17T10:00:00Z"
}

```

#### Error Response (404 Not Found)
**Condition:** Ticker is invalid or delisted.

```json
{
  "error": "Ticker 'XYZ' not found or API unavailable"
}
```

## 4.4 Implementation Logic
The service implements a robust retry mechanism to handle network jitters.

```python
@app.route('/price/<ticker>')
def get_price(ticker):
    try:
        # Fetch data using yfinance
        stock = yf.Ticker(ticker)
        
        # Get the fast 'history' metadata (1 day)
        data = stock.history(period="1d")
        
        if data.empty:
            return jsonify({"error": "Ticker not found"}), 404
            
        # Extract latest Close price
        latest_price = round(data['Close'].iloc[-1], 2)
        
        return jsonify({
            "ticker": ticker.upper(),
            "price": latest_price,
            "currency": "USD"
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500
```