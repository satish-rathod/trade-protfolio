# 6. Execution & Deployment Guide

## 6.1 Prerequisites
Before starting, ensure you have the following installed:
* **Docker Desktop** (Running and logged in)
* **Java 17 JDK**
* **Maven** (v3.8+)
* **Python 3.9+** (Optional, if running manually)

---

## 6.2 Quick Start: Docker Compose (Recommended)
The easiest way to run the entire stack (Backend + Market Engine + Local DB) is via Docker Compose.

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd <repo-name>
   ```

2. **Start the Services:**
   This command builds the images locally and orchestrates the containers.
   ```bash
   docker-compose up --build
   ```

3. **Verify Application Status:**
   * **Java Backend Health:** [http://localhost:8080/api/health](http://localhost:8080/api/health) (Should return `{"status":"UP"}`)
   * **Market Engine Test:** [http://localhost:5000/price/AAPL](http://localhost:5000/price/AAPL) (Should return JSON with price)

4. **Stop the Services:**
   ```bash
   docker-compose down
   ```

---

## 6.3 Option 2: Running Manually (Development Mode)
If you need to debug specific services.

### Step 1: Start the Market Engine (Python)
```bash
cd market-engine
pip install -r requirements.txt
python app.py
# Server runs on http://localhost:5000
```

### Step 2: Start the Portfolio Backend (Java)
Open a new terminal window:
```bash
cd backend-java
# Set Environment Variables (Mac/Linux)
export MARKET_SERVICE_URL=http://localhost:5000
export DB_URL=jdbc:h2:mem:testdb  # Use In-Memory DB for local dev

# Run the App
mvn spring-boot:run
```

---

## 6.4 Simulating the CI Pipeline
To prove the pipeline works before pushing to GitHub, you can run the checks locally:

### 1. Run Linting (Checkstyle)
Checks if your code adheres to Google Java Standards.
```bash
cd backend-java
mvn checkstyle:check
```

### 2. Run Security Scan (Dependency Check)
Scans for vulnerable libraries (Generates `target/dependency-check-report.html`).
```bash
mvn org.owasp:dependency-check-maven:check
```

### 3. Run Unit Tests
Executes JUnit tests using the `MockStockService` (No internet required).
```bash
mvn test
```

---

## 6.5 Cloud Deployment Steps (Render + Neon)

### Phase 1: Database Setup
1. Go to [Neon.tech](https://neon.tech) and create a project.
2. Copy the Connection String (e.g., `postgres://user:pass@endpoint/neondb...`).

### Phase 2: GitHub Secrets
Go to your GitHub Repo -> **Settings** -> **Secrets and variables** -> **Actions** and add:
* `DOCKERHUB_USERNAME`: Your Docker ID.
* `DOCKERHUB_TOKEN`: Your Docker Access Token.
* `RENDER_DEPLOY_HOOK`: (Get this from Render after creating the service).

### Phase 3: Render Configuration
**Create Market Service:**
* **Type:** Web Service
* **Source:** Connect GitHub Repo -> Select `market-engine` folder.
* **Runtime:** Python.

**Create Backend Service:**
* **Type:** Web Service
* **Source:** Docker Hub (Image: `yourname/portfolio-manager:latest`).
* **Environment Variables:**
    * `DB_URL`: (Paste Neon Connection String)
    * `DB_USERNAME`: (Neon User)
    * `DB_PASSWORD`: (Neon Pass)
    * `MARKET_SERVICE_URL`: (The URL of your Render Python Service, e.g., `https://market-xyz.onrender.com`)

---

### **🚀 Final Check!**
You now have:
1. **The Code:** A robust Java microservice setup.
2. **The Pipeline:** A `ci.yml` that covers all grading criteria (Lint, Test, SAST, Scan, Push).
3. **The Documentation:** A complete `docs/` folder that answers every Viva question.