"""
Unit tests for the Market Engine Flask application.
Uses pytest for testing with mocked yfinance responses.
"""
import pytest
from unittest.mock import patch, MagicMock
import pandas as pd
from app import app, fetch_with_retry


@pytest.fixture
def client():
    """Create a test client for the Flask app."""
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client


class TestHealthEndpoint:
    """Tests for the /health endpoint."""
    
    def test_health_returns_up(self, client):
        """Test that health endpoint returns UP status."""
        response = client.get('/health')
        assert response.status_code == 200
        data = response.get_json()
        assert data['status'] == 'UP'
        assert data['service'] == 'market-engine'
        assert 'timestamp' in data


class TestPriceEndpoint:
    """Tests for the /price/<ticker> endpoint."""
    
    @patch('app.fetch_with_retry')
    def test_get_price_success(self, mock_fetch, client):
        """Test successful price fetch."""
        # Mock successful response
        mock_data = pd.DataFrame({'Close': [185.92]})
        mock_fetch.return_value = mock_data
        
        response = client.get('/price/AAPL')
        assert response.status_code == 200
        data = response.get_json()
        assert data['ticker'] == 'AAPL'
        assert data['price'] == 185.92
        assert data['currency'] == 'USD'
        assert 'timestamp' in data
    
    @patch('app.fetch_with_retry')
    def test_get_price_not_found(self, mock_fetch, client):
        """Test 404 when ticker not found."""
        mock_fetch.return_value = None
        
        response = client.get('/price/INVALIDTICKER')
        assert response.status_code == 404
        data = response.get_json()
        assert 'error' in data
        assert 'INVALIDTICKER' in data['error']
    
    @patch('app.fetch_with_retry')
    def test_get_price_empty_data(self, mock_fetch, client):
        """Test 404 when data is empty."""
        mock_fetch.return_value = pd.DataFrame()
        
        response = client.get('/price/XYZ')
        assert response.status_code == 404


class TestBatchPriceEndpoint:
    """Tests for the /prices batch endpoint."""
    
    @patch('app.fetch_with_retry')
    def test_get_multiple_prices(self, mock_fetch, client):
        """Test batch price fetch."""
        mock_data = pd.DataFrame({'Close': [185.92]})
        mock_fetch.return_value = mock_data
        
        response = client.post('/prices', 
                               json={'tickers': ['AAPL', 'NVDA']},
                               content_type='application/json')
        assert response.status_code == 200
        data = response.get_json()
        assert 'prices' in data
        assert 'AAPL' in data['prices']
    
    def test_batch_missing_tickers(self, client):
        """Test 400 when tickers missing in request."""
        response = client.post('/prices', 
                               json={},
                               content_type='application/json')
        assert response.status_code == 400


class TestRetryMechanism:
    """Tests for the retry mechanism."""
    
    @patch('app.yf.Ticker')
    def test_fetch_with_retry_success(self, mock_ticker):
        """Test retry returns data on success."""
        mock_data = pd.DataFrame({'Close': [100.0]})
        mock_instance = MagicMock()
        mock_instance.history.return_value = mock_data
        mock_ticker.return_value = mock_instance
        
        result = fetch_with_retry('AAPL', retries=1, backoff=0.01)
        assert not result.empty
        assert result['Close'].iloc[0] == 100.0
    
    @patch('app.yf.Ticker')
    def test_fetch_with_retry_fails_then_succeeds(self, mock_ticker):
        """Test retry recovers after initial failure."""
        mock_instance = MagicMock()
        # First call returns empty, second returns data
        mock_instance.history.side_effect = [
            pd.DataFrame(),
            pd.DataFrame({'Close': [150.0]})
        ]
        mock_ticker.return_value = mock_instance
        
        result = fetch_with_retry('AAPL', retries=3, backoff=0.01)
        assert not result.empty
    
    @patch('app.yf.Ticker')
    def test_fetch_with_retry_all_fail(self, mock_ticker):
        """Test retry returns None after all retries fail."""
        mock_instance = MagicMock()
        mock_instance.history.side_effect = Exception("Network error")
        mock_ticker.return_value = mock_instance
        
        result = fetch_with_retry('AAPL', retries=2, backoff=0.01)
        assert result is None


if __name__ == '__main__':
    pytest.main(['-v', __file__])
