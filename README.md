# Algorithmic Portfolio Manager (APM)

A self-hosted, microservice-based platform for portfolio management and algorithmic trading. Designed to democratize financial data access while ensuring privacy and security.

## 🏗️ Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Java Backend  │────▶│  Market Engine  │────▶│  Yahoo Finance  │
│  (Spring Boot)  │     │    (Python)     │     │      API        │
└────────┬────────┘     └─────────────────┘     └─────────────────┘
         │
         ▼
┌─────────────────┐
│   PostgreSQL    │
│    Database     │
└─────────────────┘
```

- **Backend Service**: Java Spring Boot for transaction ledger and API
- **Market Engine**: Python Flask service wrapping yfinance for stock data
- **Database**: PostgreSQL (Neon for cloud, H2 for local dev)

## 🚀 Quick Start

### Docker Compose (Recommended)

```bash
# Clone the repository
git clone <your-repo-url>
cd trade-portfolio

# Start all services
docker-compose up --build
```

### Verify Services

| Service | URL | Expected Response |
|---------|-----|-------------------|
| Backend Health | http://localhost:8080/api/health | `{"status":"UP"}` |
| Market Engine | http://localhost:5000/price/AAPL | Stock price JSON |

## 📚 API Endpoints

### Record Trade
```bash
POST /api/v1/trades
{
  "ticker": "NVDA",
  "type": "BUY",
  "quantity": 10,
  "price": 0.0  # 0 = auto-fetch price
}
```

### Get Portfolio
```bash
GET /api/v1/portfolio
```

### Get All Trades
```bash
GET /api/v1/trades
```

## 🛠️ Development

### Manual Setup

**Market Engine:**
```bash
cd market-engine
pip install -r requirements.txt
python app.py
```

**Backend:**
```bash
cd backend-java
export MARKET_SERVICE_URL=http://localhost:5000
mvn spring-boot:run
```

### Run Tests
```bash
cd backend-java
mvn test
```

### Run Linting
```bash
mvn checkstyle:check
```

## 🔒 CI/CD Pipeline

The GitHub Actions workflow includes:
1. **Checkstyle** - Code style enforcement
2. **CodeQL** - SAST security scanning
3. **Dependency Check** - SCA for vulnerable dependencies
4. **Unit Tests** - JUnit with Mockito
5. **Docker Build** - Multi-stage build
6. **Trivy Scan** - Container image vulnerability scan
7. **Deploy** - Render webhook trigger

## 📁 Project Structure

```
trade-portfolio/
├── backend-java/           # Java Spring Boot service
│   ├── src/main/java/com/apm/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access
│   │   ├── model/          # Entities
│   │   └── dto/            # Data transfer objects
│   ├── Dockerfile
│   └── pom.xml
├── market-engine/          # Python Flask service
│   ├── app.py
│   ├── requirements.txt
│   └── Dockerfile
├── docs/                   # Documentation
├── docker-compose.yml
└── .github/workflows/      # CI/CD
```

## 📖 Documentation

See the `/docs` folder for detailed documentation:
- [01-CONTEXT.md](docs/01-CONTEXT.md) - Project background
- [02-ARCHITECTURE_HLD.md](docs/02-ARCHITECTURE_HLD.md) - High-level design
- [03-BACKEND_LLD.md](docs/03-BACKEND_LLD.md) - Backend low-level design
- [04-OPENBB_SERVICE.md](docs/04-OPENBB_SERVICE.md) - Market engine details
- [05-ORCHESTRATION.md](docs/05-ORCHESTRATION.md) - CI/CD strategy
- [06-EXECUTION.md](docs/06-EXECUTION.md) - Deployment guide

## 📄 License

MIT License
