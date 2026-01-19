# 2. High-Level Design (HLD) & System Architecture

## 2.1 System Overview
The **Algorithmic Portfolio Manager (APM)** operates on a decoupled **Microservice Architecture**. This design choice ensures separation of concerns between the transactional ledger (Java) and the volatile market data processing (Python).

### Architectural Diagram
The following diagram illustrates the request flow and component interaction within the cloud environment (Render).

```mermaid
graph TD
    %% Actors
    User([User / Client])
    DevOps([DevOps Engineer])

    %% Access Points
    subgraph "External Access"
        LB[Render Load Balancer / API Gateway]
    end

    %% Cloud Infrastructure (Render)
    subgraph "Render Cloud Ecosystem"
        direction TB
        
        %% Service 1: Java Backend
        subgraph "Core Logic Layer"
            JavaApp[Java Spring Boot Service]
            note1[("Role: Ledger & Auth<br/>Port: 8080")]
        end

        %% Service 2: Python Engine
        subgraph "Market Intelligence Layer"
            PythonApp[Python Market Engine]
            note2[("Role: Data Fetcher<br/>Port: 5000")]
        end
    end

    %% Persistence Layer (Neon)
    subgraph "Persistence Layer"
        DB[(Neon PostgreSQL)]
    end

    %% External Data Sources
    subgraph "External World"
        Yahoo[Yahoo Finance / OpenBB API]
    end

    %% Flows
    User -->|HTTPS Request| LB
    LB -->|/api/*| JavaApp
    LB -->|/price/*| PythonApp
    
    %% Internal Service-to-Service Communication
    JavaApp --"REST (Internal HTTP)"--> PythonApp
    
    %% Database Connection
    JavaApp --"JDBC (SSL)"--> DB
    
    %% External API Call
    PythonApp --"Fetch Live Data"--> Yahoo
    
    %% CI/CD Flow
    DevOps --"Push Code"--> GitHub
    GitHub --"Trigger Action"--> DockerHub
    DockerHub --"Deploy Image"--> JavaApp