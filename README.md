# Picture-publishing-service
Pictures Publishing Service built with Spring Boot and PostgreSQL, featuring JWT-based authentication, high-performance caching with Redis, and database versioning using Liquibase. The project follows feature-based architecture.

---

## 🛠 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Framework** | Spring Boot 3.x, Java 17+ |
| **Database** | PostgreSQL |
| **Caching** | Redis |
| **Security** | Spring Security, JWT |
| **Database Migration** | Liquibase |
| **Build Tool** | Maven |

---

## 🏗️ System Architecture & Data Model

### 1. High-Level Architecture Diagram
This diagram illustrates the request flow, security filtering, business logic processing, caching strategy, and database persistence.

```mermaid
graph TD
    Client[Client: Browser / Postman] -->|HTTP Requests| Security[Spring Security Layer + JWT Filter]
    Security -->|Authorized| Controller[REST Controller / API Layer]
    Controller -->|DTOs| Service[Service Layer: Business Logic]
    
    Service -->|1. Save Metadata| JPA[Spring Data JPA]
    JPA -->|Write to DB| Postgres[(PostgreSQL Database)]
        
    Service -.->|2. Cache Landing Page| Redis[(Redis Cache)]

style Client fill:#f9f,stroke:#333,stroke-width:2px,color:#000
style Security fill:#ff9,stroke:#333,stroke-width:2px,color:#000
style Postgres fill:#9f9,stroke:#333,stroke-width:2px,color:#000
style Redis fill:#f99,stroke:#333,stroke-width:2px,color:#000
```
### 2. Entity Relationship Diagram (ERD)
The database structure consists of two main tables with a One-to-Many relationship between Users and Pictures, strictly managed via Liquibase migrations.
```mermaid

erDiagram
    USERS {
        bigint id PK
        varchar email UK
        varchar password
        varchar role
    }
    PICTURES {
        bigint id PK
        bigint user_id FK
        text description
        varchar category
        varchar file_name
        bigint file_size
        int width
        int height
        varchar status
        varchar generated_url
    }

    USERS ||--o{ PICTURES : ""
```
---
## ⚙️ Prerequisites
Ensure you have the following installed on your machine:

Java 17 or higher

Docker (to run PostgreSQL and Redis containers)

Maven

🚀 How to Run
---
Start Infrastructure:
Use Docker Compose to launch the database and cache containers:
```
docker-compose up -d
```
---
### Check Swagger for API Documentation
