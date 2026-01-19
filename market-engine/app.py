"""
OpenBB Market Engine Service
A Flask-based microservice for fetching real-time stock prices using yfinance.
Includes retry mechanism with exponential backoff for network resilience.
"""
from flask import Flask, jsonify
from datetime import datetime, timezone
import yfinance as yf
import time
import logging

app = Flask(__name__)

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Retry configuration
MAX_RETRIES = 3
INITIAL_BACKOFF = 0.5  # seconds


def fetch_with_retry(ticker, retries=MAX_RETRIES, backoff=INITIAL_BACKOFF):
    """
    Fetch stock data with exponential backoff retry mechanism.
    
    Args:
        ticker: Stock symbol
        retries: Number of retry attempts
        backoff: Initial backoff time in seconds
    
    Returns:
        DataFrame with stock history or None if all retries fail
    """
    for attempt in range(retries):
        try:
            stock = yf.Ticker(ticker)
            data = stock.history(period="1d")
            
            if not data.empty:
                return data
            
            logger.warning(f"Empty data for {ticker}, attempt {attempt + 1}/{retries}")
            
        except Exception as e:
            logger.warning(f"Fetch failed for {ticker}, attempt {attempt + 1}/{retries}: {e}")
        
        if attempt < retries - 1:
            sleep_time = backoff * (2 ** attempt)
            logger.info(f"Retrying in {sleep_time}s...")
            time.sleep(sleep_time)
    
    return None


@app.route('/health')
def health():
    """Health check endpoint."""
    return jsonify({
        "status": "UP",
        "service": "market-engine",
        "timestamp": datetime.now(timezone.utc).isoformat()
    })


@app.route('/price/<ticker>')
def get_price(ticker):
    """
    Fetch the real-time (or latest available) closing price for a given stock symbol.
    Implements retry mechanism with exponential backoff.
    
    Args:
        ticker: Stock symbol (e.g., AAPL, NVDA, BTC-USD)
    
    Returns:
        JSON with ticker, price, currency, and timestamp
    """
    logger.info(f"Fetching price for ticker: {ticker}")
    
    try:
        # Fetch data with retry mechanism
        data = fetch_with_retry(ticker.upper())
        
        if data is None or data.empty:
            logger.warning(f"Ticker not found: {ticker}")
            return jsonify({
                "error": f"Ticker '{ticker}' not found or API unavailable"
            }), 404
        
        # Extract latest Close price
        latest_price = round(data['Close'].iloc[-1], 2)
        
        logger.info(f"Successfully fetched {ticker}: ${latest_price}")
        
        return jsonify({
            "ticker": ticker.upper(),
            "price": latest_price,
            "currency": "USD",
            "timestamp": datetime.now(timezone.utc).isoformat()
        })
        
    except Exception as e:
        logger.error(f"Error fetching {ticker}: {e}")
        return jsonify({"error": str(e)}), 500


@app.route('/prices', methods=['POST'])
def get_multiple_prices():
    """
    Fetch prices for multiple tickers in one request.
    
    Request Body:
        {"tickers": ["AAPL", "NVDA", "GOOGL"]}
    
    Returns:
        JSON with prices for each ticker
    """
    from flask import request
    
    data = request.get_json()
    if not data or 'tickers' not in data:
        return jsonify({"error": "Missing 'tickers' in request body"}), 400
    
    tickers = data['tickers']
    results = {}
    
    for ticker in tickers:
        try:
            stock_data = fetch_with_retry(ticker.upper())
            if stock_data is not None and not stock_data.empty:
                results[ticker.upper()] = {
                    "price": round(stock_data['Close'].iloc[-1], 2),
                    "currency": "USD"
                }
            else:
                results[ticker.upper()] = {"error": "Not found"}
        except Exception as e:
            results[ticker.upper()] = {"error": str(e)}
    
    return jsonify({
        "prices": results,
        "timestamp": datetime.now(timezone.utc).isoformat()
    })


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
