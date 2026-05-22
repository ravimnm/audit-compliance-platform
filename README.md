# Audit Compliance Platform

A secure, multi-tenant audit logging system focused on **log integrity verification using hash chaining under a defined threat model**.

This project demonstrates how backend systems can **detect tampering in audit logs**, rather than falsely claiming to prevent it.

---

## 🚀 Features

- JWT-based authentication
- Role-Based Access Control (RBAC)
- Multi-tenant isolation via context
- Audit log creation (server-controlled identity)
- Hash chaining for integrity linkage
- External integrity anchor (file-based)
- Tampering detection API
- Pagination, filtering, sorting
- CSV export
- Swagger + Postman testing

---

## 🧠 Core Concept: Integrity Model

Each log stores:

- hash
- previousHash

Hash computation:

H(n) = SHA256(H(n-1) + data)

Where data includes:

- actorId
- role
- tenantId
- action
- timestamp

---

## 🔗 Chain Behavior

Log1 → Log2 → Log3 → ...

If any log is modified:

- All subsequent hashes become invalid
- Verification fails deterministically

---

## 🔐 Threat Model

### System Detects:

- Application-level log modification
- Accidental corruption
- Database-level tampering (single record changes)

### System Does NOT Protect Against:

- Full database rewrite with recomputation
- Compromised application server
- Secret key compromise
- External anchor deletion or overwrite

---

## 🏗️ Architecture

Controller → DTO → Service → Repository → Database  
             ↓  
     Security (JWT + RBAC)  
             ↓  
     Tenant Context (ThreadLocal)

---

## ⚙️ Tech Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (jjwt)
- Swagger (OpenAPI)

---

## ⚙️ Setup

```bash
git clone https://github.com/ravimnm/audit-compliance-platform.git
cd audit-compliance-platform
```

---

## ⚙️ Configuration

Configure application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/audit_platform  
spring.datasource.username=postgres  
spring.datasource.password=yourpassword  
jwt.secret=your-secret-key  

---

## ▶️ Run

mvn spring-boot:run  

---

## 🔑 Authentication

Generate token:

GET /auth/token?userId=user1&role=ADMIN&tenantId=tenant_1  

Use in requests:

Authorization: Bearer <token>  

---

## 📌 APIs

### Create Audit Log

POST /audit/events  

Body:

{
  ""action"": ""USER_LOGIN""
}

---

### Get Logs

GET /audit/events  

Supports:

- actorId  
- action  
- date range  
- pagination  
- sorting  

---

### Verify Integrity

GET /audit/events/verify  

---

### Export Logs

GET /audit/events/export  

---

## 🧪 Tampering Simulation

1. Insert logs via API  
2. Modify DB manually:

UPDATE audit_logs SET action = 'HACKED' WHERE id = 1;  

3. Call:

GET /audit/events/verify  

Expected:

Tampering detected at log ID: 1  

---

## ⚠️ Limitations

- Not tamper-proof, only tamper-detectable  
- No protection against full chain recomputation  
- No digital signatures  
- No append-only enforcement at DB level  

---

## 📈 Future Improvements

- Append-only DB constraints (triggers)  
- Digital signature support  
- External hash anchoring (cloud/blockchain)  
- Async logging via queue  
- High-throughput ingestion optimization  

---

## 🧠 Key Takeaway

This system focuses on:

- Integrity verification  
- Failure detection  
- Clear trust boundaries  

Not on unrealistic guarantees of absolute security.  

---

## 👨‍💻 Author

Ravi Sankar Manem 

"@ | Add-Content -Path README.md
