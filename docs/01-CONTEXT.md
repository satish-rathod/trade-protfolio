# 1. Project Context & Strategic Motivation

## 1.1 Problem Background
Retail investors and algorithmic traders face significant challenges when attempting to build personalized trading dashboards or automated strategies. The current landscape is fragmented:

1.  **Data Latency & Reliability:** Free public APIs (like AlphaVantage or standard Yahoo Finance endpoints) suffer from severe rate limiting (e.g., 5 calls/minute) and high latency. This makes them unsuitable for real-time portfolio tracking or high-frequency analysis.
2.  **Privacy & Security Risks:** Most portfolio tracking tools are SaaS-based, requiring users to upload sensitive financial history and trade logs to third-party servers. This creates a single point of failure and potential data leakage risks.
3.  **Cost Prohibitive:** Professional-grade terminals (e.g., Bloomberg, Refinitiv) or premium API subscriptions (e.g., Polygon.io) are prohibitively expensive for individual developers and students.

## 1.2 Proposed Solution: Algorithmic Portfolio Manager (APM)
The **Algorithmic Portfolio Manager (APM)** is a self-hosted, microservice-based platform designed to democratize financial data access.

* **Architecture:** It decouples the *Market Data Engine* from the *Portfolio Ledger*.
* **Core Innovation:** Instead of relying on external SaaS, APM hosts its own "Oracle" service (Python-based) that wraps open-source financial libraries (OpenBB/Yfinance). This allows the internal Java backend to query market data with near-zero latency within the internal Docker network.
* **Privacy Model:** All user trade data is stored in a private, encrypted PostgreSQL database (Neon), ensuring that sensitive financial ledgers never leave the user's control.

## 1.3 DevOps Relevance (Why this project?)
This application was specifically architected to demonstrate advanced DevOps and CI/CD competencies. It is not just a CRUD app; it is a distributed system that requires:

* **Polyglot Build Pipelines:** Handling both Java (Maven) and Python (Pip) ecosystems in a single workflow.
* **Orchestration:** Managing dependencies between the Backend, Market Engine, and Database services.
* **Shift-Left Security:** Since the app handles financial data, the pipeline integrates rigorous SAST (Static Application Security Testing) and SCA (Software Composition Analysis) to prevent vulnerabilities like SQL Injection or Log4j exploits before deployment.
* **Immutable Infrastructure:** The use of Docker ensures that the complex dependency chain of financial libraries is packaged reliably, eliminating "it works on my machine" issues.