# 🎫 TicketFlow — Support Ticket Management System

A production-grade, cloud-native support ticket platform built with **Spring Boot 3.5** and **Spring Cloud**. 
Features full microservices architecture, JWT-based security, async event messaging, distributed tracing, and more — all deployable with a single command.

---

## 🏗️ Architecture Overview

```
[Client]
    ↓
[API Gateway :8080]  ── JWT validation · routing · CORS · rate limiting
    ↓
┌─────────────────┬──────────────────┬─────────────────────┬──────────────────┐
│  user-service   │  ticket-service  │ notification-service│ document-service │
│     :8081       │      :8082       │       :8083         │      :8084       │
└─────────────────┴──────────────────┴─────────────────────┴──────────────────┘
        ↕ Kafka (async events)            ↕ FeignClient (sync calls)
┌──────────────────────────────────────────────────────────────────────────────┐
│  PostgreSQL × 4  │  Keycloak :8180  │  MinIO :9000  │  Redis :6379          │
│  Kafka :9092     │  Eureka :8761    │  Zipkin :9411 │  Prometheus :9090      │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## 📋 Prerequisites

| Tool | Minimum Version |
|---|---|
| Docker | 24.x |
| Docker Compose | 2.x |
| Java | 17 |
| Maven | 3.9.x |

---

## ⚡ Quick Start

```bash
# Clone the repository
git clone https://github.com/asohyannick/ticketflow.git
cd ticketflow

# Build all services
mvn clean package -DskipTests

# Start everything
docker-compose up --build
```

> All services start automatically in the correct order. The Keycloak realm is imported automatically on first startup — no manual configuration needed.

---

## 🌐 Service URLs

| Service | URL | Purpose |
|---|---|---|
| API Gateway | http://localhost:8080 | Single entry point for all API calls |
| Eureka Dashboard | http://localhost:8761 | Service registry — verify all services are UP |
| Config Server | http://localhost:8888 | Centralized configuration |
| Keycloak Admin | http://localhost:8180/admin | Identity provider (`admin` / `admin`) |
| Swagger UI | http://localhost:8080/swagger-ui.html | Aggregated API docs |
| Zipkin | http://localhost:9411 | Distributed tracing |
| Prometheus | http://localhost:9090 | Metrics |
| MinIO Console | http://localhost:9001 | File storage (`minioadmin` / `minioadmin123`) |
| Kafka UI | http://localhost:8090 | Kafka topic browser |

---

## 🔐 Authentication

All API endpoints require a valid JWT from Keycloak.

### Step 1 — Get a token

```bash
# Admin user (full access)
curl -X POST http://localhost:8180/realms/ticketflow/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=ticketflow-api" \
  -d "client_secret=ticketflow-secret-2024" \
  -d "username=admin-user" \
  -d "password=admin123"

# Agent user
curl -X POST http://localhost:8180/realms/ticketflow/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=ticketflow-api" \
  -d "client_secret=ticketflow-secret-2024" \
  -d "username=agent-user" \
  -d "password=agent123"

# Regular user
curl -X POST http://localhost:8180/realms/ticketflow/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=ticketflow-api" \
  -d "client_secret=ticketflow-secret-2024" \
  -d "username=regular-user" \
  -d "password=user123"
```

### Step 2 — Export the token

```bash
export TOKEN="<paste access_token here>"
```

### Pre-configured test users

| Username | Password | Role | Key Permissions |
|---|---|---|---|
| `admin-user` | `admin123` | ADMIN | All permissions |
| `agent-user` | `agent123` | AGENT | `ticket:*`, `document:*`, `notification:read` |
| `regular-user` | `user123` | USER | `ticket:create/read/comment`, `document:*` |

---

## 📡 API Reference

All endpoints are accessed via the Gateway at `http://localhost:8080`.

### User Service

```bash
# Create user (requires user:create)
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "firstName": "John", "lastName": "Doe", "email": "john.doe@example.com", "password": "password123" }'

# Get user by ID (requires user:read)
curl http://localhost:8080/api/users/{id} \
  -H "Authorization: Bearer $TOKEN"

# Assign roles to user (requires user:manage-roles)
curl -X PUT http://localhost:8080/api/users/{id}/roles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "roleNames": ["AGENT"] }'

# Create a role (requires user:manage-roles)
curl -X POST http://localhost:8080/api/roles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "name": "SUPERVISOR", "description": "Supervisor role" }'

# Assign permissions to a role (requires user:manage-roles)
curl -X PUT http://localhost:8080/api/roles/{id}/permissions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "permissionNames": ["ticket:read", "ticket:update"] }'

# List all roles (requires user:read)
curl http://localhost:8080/api/roles -H "Authorization: Bearer $TOKEN"

# List all permissions (requires user:read)
curl http://localhost:8080/api/permissions -H "Authorization: Bearer $TOKEN"
```

### Ticket Service

```bash
# Create ticket (requires ticket:create)
curl -X POST http://localhost:8080/api/tickets \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "title": "Cannot login to portal", "description": "Getting 401 error on every login attempt", "priority": "HIGH", "assigneeId": "optional-user-id" }'

# List tickets paginated (requires ticket:read)
curl "http://localhost:8080/api/tickets?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"

# Get ticket by ID (requires ticket:read)
curl http://localhost:8080/api/tickets/{id} \
  -H "Authorization: Bearer $TOKEN"

# Change ticket status (requires ticket:update)
# Valid transitions: OPEN → IN_PROGRESS → RESOLVED → CLOSED
curl -X PATCH http://localhost:8080/api/tickets/{id}/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "status": "IN_PROGRESS" }'

# Add comment (requires ticket:comment)
curl -X POST http://localhost:8080/api/tickets/{id}/comments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "content": "Looking into this issue now." }'
```

### Document Service

```bash
# Upload a file (requires document:upload)
curl -X POST http://localhost:8080/api/documents/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/your/file.pdf" \
  -F "ticketId=optional-ticket-id"

# Get document metadata (requires document:read)
curl http://localhost:8080/api/documents/{id} \
  -H "Authorization: Bearer $TOKEN"

# Get pre-signed download URL (requires document:download)
# Returns a URL valid for 5 minutes — usable in browser without a token
curl http://localhost:8080/api/documents/{id}/download \
  -H "Authorization: Bearer $TOKEN"
```

### Notification Service

```bash
# Get notification history (requires notification:read)
curl "http://localhost:8080/api/notifications/history?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"

# Send manual notification (requires notification:send)
curl -X POST http://localhost:8080/api/notifications/send \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "recipient": "test@example.com", "subject": "Test notification", "body": "This is a test notification from TicketFlow." }'
```

---

## 🛡️ Security Model (RBAC + ABAC)

This system uses a hybrid security model: users are assigned **roles** (ADMIN, AGENT, USER), and every endpoint is protected by a specific **permission scope** — never by role directly.

```java
// Every endpoint uses this pattern — NEVER hasRole()
@PreAuthorize("hasAuthority('ticket:create')")
```

### Permission Matrix

| Permission | ADMIN | AGENT | USER |
|---|:---:|:---:|:---:|
| `user:create` | ✅ | ❌ | ❌ |
| `user:read` | ✅ | ❌ | ❌ |
| `user:update` | ✅ | ❌ | ❌ |
| `user:delete` | ✅ | ❌ | ❌ |
| `user:manage-roles` | ✅ | ❌ | ❌ |
| `ticket:create` | ✅ | ✅ | ✅ |
| `ticket:read` | ✅ | ✅ | ✅ |
| `ticket:update` | ✅ | ✅ | ❌ |
| `ticket:delete` | ✅ | ❌ | ❌ |
| `ticket:comment` | ✅ | ✅ | ✅ |
| `document:upload` | ✅ | ✅ | ✅ |
| `document:read` | ✅ | ✅ | ✅ |
| `document:download` | ✅ | ✅ | ✅ |
| `notification:read` | ✅ | ✅ | ❌ |
| `notification:send` | ✅ | ❌ | ❌ |

---

## 📨 Kafka Events

| Topic | Producer | Consumer | Trigger |
|---|---|---|---|
| `user.created` | user-service | notification-service | Welcome email |
| `ticket.created` | ticket-service | notification-service | Support alert |
| `ticket.status.changed` | ticket-service | notification-service | Status update email |
| `document.uploaded` | document-service | ticket-service | Auto-link document to ticket |

---

## 🔁 Fault Tolerance (Resilience4j)

The `ticket-service` wraps all calls to `user-service` with:

- **Retry** — 3 attempts with 500ms wait between each
- **Circuit Breaker** — opens after 50% failure rate over 10 requests
- **Fallback** — returns a safe degraded response when the circuit is open

**Testing the circuit breaker:**

```bash
# Stop user-service container
docker stop user-service

# Make 5+ ticket requests — watch the circuit open in logs
curl -X POST http://localhost:8080/api/tickets \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "title": "Test", "assigneeId": "any-id" }'
```

---

## 🔍 Distributed Tracing

Every request receives a `traceId` that propagates across all services. Search for it in Zipkin after any request:

```bash
open http://localhost:9411
```

---

## 📊 Metrics

All services expose metrics at `/actuator/prometheus`.

```bash
# Check metrics for user-service directly
curl http://localhost:8081/actuator/prometheus

# View all scrape targets in Prometheus
open http://localhost:9090/targets
```

---

## 📁 Project Structure

```
ticketflow/
├── docker-compose.yml           # Start everything with one command
├── prometheus/
│   └── prometheus.yml           # Scrape config for all services
├── keycloak/
│   └── ticketflow-realm.json    # Auto-imported Keycloak realm
├── pom.xml                      # Parent Maven POM
├── eureka-server/               # Service registry        — :8761
├── config-server/               # Centralized config      — :8888
│   └── src/main/resources/
│       └── config-repo/         # Per-service YAML files
├── api-gateway/                 # Entry point             — :8080
├── user-service/                # Users / roles / perms   — :8081
├── ticket-service/              # Tickets / comments      — :8082
├── notification-service/        # Kafka consumers         — :8083
└── document-service/            # MinIO file storage      — :8084
```

---

## 🧰 Technology Stack

| Technology                  | Purpose |
|-----------------------------|---|
| Spring Boot 3.5             | Service framework |
| Spring Cloud Gateway        | API Gateway — routing, JWT validation |
| Spring Security + OAuth2    | JWT validation, scope-based access control |
| Keycloak 23                 | Identity provider — realm, roles, scopes |
| Spring Cloud Netflix Eureka | Service discovery and registration |
| Spring Cloud Config         | Centralized configuration |
| Apache Kafka                | Async event messaging |
| Spring Cloud OpenFeign      | Declarative inter-service REST calls |
| Resilience4j                | Circuit breaker, retry, fallback |
| MinIO                       | S3-compatible file storage |
| PostgreSQL 18               | Relational database (one per service) |
| Redis 7                     | Rate limiting backend |
| Micrometer + Zipkin         | Distributed tracing |
| Prometheus                  | Metrics collection |
| Springdoc OpenAPI           | Swagger UI and API documentation |
| Docker Compose              | Infrastructure orchestration |

---

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run tests for a specific service
mvn test -pl user-service

# Build without tests (faster)
mvn clean package -DskipTests
```

---

## 🛠️ Troubleshooting

<details>
<summary><strong>Services not registering with Eureka</strong></summary>

```bash
# Check Eureka dashboard
open http://localhost:8761

# Check individual service health
curl http://localhost:8081/actuator/health
```
</details>

<details>
<summary><strong>JWT token invalid</strong></summary>

```bash
# Verify Keycloak is running and realm is imported
open http://localhost:8180/admin
# Login: admin / admin
# The "ticketflow" realm should be visible
```
</details>

<details>
<summary><strong>Kafka topics not created</strong></summary>

```bash
# Check Kafka UI
open http://localhost:8090

# Or inspect broker logs directly
docker logs kafka
```
</details>

<details>
<summary><strong>MinIO bucket missing</strong></summary>

```bash
# Open MinIO console
open http://localhost:9001
# Login: minioadmin / minioadmin123
# Bucket "ticketflow-bucket" should exist
# If missing, restart the init container:
docker restart minio-init
```
</details>

<details>
<summary><strong>Config server not serving properties</strong></summary>

```bash
curl http://localhost:8888/user-service/default
# Should return JSON with user-service properties
```
</details>

---

## 📬 Contact

For questions about this implementation: **keepcoding200@gmail.com**



