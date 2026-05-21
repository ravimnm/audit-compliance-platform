# Audit Compliance Platform

A secure, multi-tenant audit logging system built to track user actions with strong guarantees of integrity and isolation.

Unlike basic logging systems, this platform ensures that audit logs cannot be modified silently using a hash chaining mechanism.

---

## What Problem This Solves

In real systems:
- Logs can be altered by admins or attackers
- No guarantee of integrity
- Difficult to trace actions across tenants

This system solves:
- Tampering detection
- Secure user attribution
- Tenant isolation

---

## Core Features

- JWT Authentication (stateless security)
- Role-Based Access Control (ADMIN / USER)
- Multi-tenant isolation (each tenant sees only their data)
- Audit log creation for every action
- Filtering (actor, action, date range)
- Pagination and sorting
- CSV export
- Hash chaining for tamper detection
- Integrity verification API
- Swagger for API testing

---

## Key Concept: Hash Chaining

Each log stores:

- hash (current record hash)
- previousHash (hash of previous record)

Flow:

Log1 -> Log2 -> Log3 -> Log4

Each hash depends on previous:

hash(n) = SHA256(previousHash + data)

---

## Why This Matters

If someone modifies a record:

Example:

Log2 is changed

Then:

- hash(Log2) changes
- Log3.previousHash no longer matches
- Entire chain breaks

System detects:

Tampering detected

---

## Architecture

Controller -> DTO -> Service -> Specification -> Repository -> Database  
                ↓  
         Security Layer (JWT + RBAC)  
                ↓  
         Tenant Context  

---

## Security Design

- User identity comes from JWT (not request body)
- Tenant is derived internally (cannot be spoofed)
- Role is embedded in token

Prevents:
- Fake user injection
- Cross-tenant data access
- Privilege escalation

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA (Hibernate)
- PostgreSQL
- JWT (jjwt)
- Swagger (OpenAPI)

---

## Setup Instructions

1. Clone project

git clone https://github.com/ravimnm/audit-compliance-platform.git  
cd audit-compliance-platform  

---

2. Configure database

Edit application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/audit_platform  
spring.datasource.username=postgres  
spring.datasource.password=yourpassword  

---

3. Run application

mvn spring-boot:run  

---

4. Open Swagger

http://localhost:8080/swagger-ui/index.html  

---

## Authentication Flow

1. Generate token

GET /auth/token?userId=user1  

2. Use token in headers

Authorization: Bearer <token>

---

## API Endpoints

Create Audit Log  
POST /audit/events  

Get Logs  
GET /audit/events  

Verify Integrity  
GET /audit/events/verify  

Export CSV  
GET /audit/events/export  

---

## Example Flow

1. User logs in
2. Token generated
3. User performs action
4. Audit log created
5. Hash linked with previous log
6. Chain grows
7. Any modification -> detected

---

## Testing Strategy

Test using:
- Swagger
- Postman

Validate:
- Authentication works
- Logs are created correctly
- Filters return correct data
- Pagination works
- Hash chain remains valid
- Tampering is detected

---

## Limitations

- Single node system (not distributed)
- No async processing
- Basic RBAC (can be extended)

---

## Future Improvements

- Async log processing (Kafka / queues)
- Distributed audit system
- Fraud detection layer
- Advanced analytics

---

## Author

https://github.com/ravimnm/
