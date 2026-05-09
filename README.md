<div align="center">

# Property Management System

**小区物业管理系统**

A full-stack property management platform built with **Spring Boot 3** and **React 19**, featuring a Liquid Glass UI design language.

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=flat-square&logo=react&logoColor=black)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-6.0-3178C6?style=flat-square&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-8-646CFF?style=flat-square&logo=vite&logoColor=white)](https://vite.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-4-06B6D4?style=flat-square&logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](#license)

<br />

[Features](#features) · [Tech Stack](#tech-stack) · [Quick Start](#quick-start) · [Architecture](#architecture) · [API Reference](#api-reference) · [UI Design](#ui-design)

</div>

---

## Features

| Module | Description |
|:-------|:------------|
| **Dashboard** | Real-time data overview, quick actions, and system information |
| **Owner Management** | Full CRUD for owner profiles with search by name or phone |
| **Employee Management** | Staff records, positions, and contact information |
| **House Management** | Building / unit / room tracking with occupancy status (occupied / vacant / under renovation) |
| **Fee Management** | Billing records for property, water, electricity, and gas fees — with payment status tracking |
| **Parking Management** | Parking spot allocation, license plate binding, and availability status |
| **Complaint Handling** | Ticket submission and tri-state workflow (pending → in progress → resolved) |
| **Repair Tracking** | Device fault reporting and repair status lifecycle (pending → in progress → completed) |
| **Duty Scheduling** | Employee shift planning across morning / afternoon / night shifts |

## Tech Stack

<table>
<tr>
<td valign="top" width="50%">

**Backend**

| Layer | Technology |
|:------|:-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| ORM | MyBatis 3.0.3 |
| Database | MySQL 8.0 |
| Connection Pool | Alibaba Druid 1.2.21 |
| Auth | Spring Security + JWT (jjwt 0.12.5) |
| Build | Maven 3.x |

</td>
<td valign="top" width="50%">

**Frontend**

| Layer | Technology |
|:------|:-----------|
| Language | TypeScript 6.0 |
| Framework | React 19 |
| Build Tool | Vite 8 |
| Styling | Tailwind CSS 4 |
| Routing | React Router 7 |
| HTTP Client | Axios |
| Icons | Lucide React |

</td>
</tr>
</table>

## Quick Start

### Prerequisites

- **JDK 17+**
- **Maven 3.x**
- **MySQL 8.x** running on `localhost:3306`
- **Node.js 18+**

### 1. Initialize Database

```bash
mysql -u root -p < src/main/resources/property_management.sql
```

### 2. Configure Database Connection

Edit [`src/main/resources/application.yml`](src/main/resources/application.yml) and set your MySQL password:

```yaml
spring:
  datasource:
    password: your_password_here
```

### 3. Start Backend

```bash
mvn spring-boot:run
```

Backend starts at **http://localhost:8080**.

### 4. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend starts at **http://localhost:3000** — API requests to `/api` are proxied to the backend in dev mode.

### 5. Login

| Field | Value |
|:------|:------|
| URL | http://localhost:3000 |
| Username | `admin` |
| Password | `admin123` |

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (React)                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────┐  │
│  │  Layout   │  │  Pages   │  │ DataTable│  │ FormModal  │  │
│  │ (Sidebar) │  │ (×10)    │  │          │  │            │  │
│  └──────────┘  └──────────┘  └──────────┘  └────────────┘  │
│        │              │                                      │
│        └──────┬───────┘                                      │
│          api.ts (Axios + CRUD factory)                       │
└───────────────┼──────────────────────────────────────────────┘
                │  Authorization: Bearer <JWT>
                ▼
┌─────────────────────────────────────────────────────────────┐
│                    Backend (Spring Boot)                      │
│  ┌────────────┐   ┌────────────┐   ┌──────────────────────┐ │
│  │ Controller  │──▶│  Service   │──▶│  Mapper (MyBatis XML)│ │
│  │  (REST API) │   │ (Business) │   │       (SQL)          │ │
│  └────────────┘   └────────────┘   └──────────────────────┘ │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Security: JWT Filter → Spring Security → Controller   │  │
│  └────────────────────────────────────────────────────────┘  │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
                    ┌──────────────┐
                    │   MySQL 8.0  │
                    │  (9 tables)  │
                    └──────────────┘
```

### Project Structure

```
├── src/main/
│   ├── java/com/property/
│   │   ├── PropertyManagementApplication.java    # Entry point
│   │   ├── controller/       # REST controllers (8 modules + Auth)
│   │   ├── entity/           # Entity POJOs
│   │   ├── mapper/           # MyBatis mapper interfaces
│   │   ├── service/          # Business logic (interface + impl)
│   │   └── security/         # JWT utils, auth filter, security config
│   └── resources/
│       ├── application.yml                       # App configuration
│       ├── property_management.sql               # Schema + seed data
│       └── mapper/                               # MyBatis XML mappings
└── frontend/
    └── src/
        ├── api.ts             # Axios instance + generic CRUD factory
        ├── types.ts           # TypeScript interfaces
        ├── index.css          # Liquid Glass theme & animations
        ├── components/        # Reusable: Layout, DataTable, FormModal, ConfirmDialog
        └── pages/             # Route pages: Login, Dashboard, 8 modules
```

### Database Schema

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   sys_user   │     │    owner     │◀───▶│    house     │
│──────────────│     │──────────────│     │──────────────│
│ username     │     │ name         │     │ building     │
│ password     │     │ phone        │     │ unit         │
│ role         │     │ id_card      │     │ room_number  │
└──────────────┘     └──────┬───────┘     │ status       │
                            │             └──────────────┘
            ┌───────────────┼───────────────────┐
            ▼               ▼                   ▼
     ┌────────────┐  ┌────────────┐      ┌────────────┐
     │    fee     │  │  parking   │      │ complaint  │
     │────────────│  │────────────│      │────────────│
     │ fee_type   │  │ spot_number│      │ title      │
     │ amount     │  │ license    │      │ status     │
     │ status     │  │ status     │      └────────────┘
     └────────────┘  └────────────┘              │
                                            ┌────────────┐
┌──────────────┐     ┌──────────────┐       │   repair   │
│   employee   │◀───▶│     duty     │       │────────────│
│──────────────│     │──────────────│       │ device     │
│ name         │     │ duty_date    │       │ fault      │
│ position     │     │ shift        │       │ status     │
└──────────────┘     └──────────────┘       └────────────┘
```

## API Reference

All endpoints are prefixed with `/api`. Protected routes require the header:

```
Authorization: Bearer <token>
```

### Authentication

| Method | Endpoint | Description |
|:-------|:---------|:------------|
| `POST` | `/api/auth/login` | Login — returns JWT token + user info |
| `GET` | `/api/auth/info` | Get current authenticated user |

### Business Modules

> Modules: `owner` · `employee` · `house` · `fee` · `parking` · `complaint` · `repair` · `duty`

| Method | Endpoint | Description |
|:-------|:---------|:------------|
| `GET` | `/api/{module}/page?pageNum=&pageSize=` | Paginated list |
| `GET` | `/api/{module}/get/{id}` | Get by ID |
| `POST` | `/api/{module}/add` | Create record |
| `POST` | `/api/{module}/update` | Update record |
| `POST` | `/api/{module}/delete/{id}` | Delete record |

### Response Format

```jsonc
// Paginated response
{
  "list": [...],
  "total": 42,
  "pageNum": 1,
  "pageSize": 10,
  "totalPages": 5
}

// Mutation response
{ "code": 0, "msg": "操作成功" }   // success
{ "code": 1, "msg": "操作失败" }   // failure
```

## UI Design

The frontend uses a **dark theme** with **Liquid Glass** design language:

- **Ambient Orb** — animated background lighting that shifts color subtly
- **Glassmorphism** — frosted-glass icon containers with light refraction highlights
- **Mouse-tracking Glow** — interactive hover effects on dashboard stat cards
- **Staggered Animations** — smooth page-enter transitions with cascading reveals
- **Noise Texture Overlay** — subtle grain layer for added depth and materiality

## Environment Variables

Before deploying to production, update these values in [`application.yml`](src/main/resources/application.yml):

| Key | Location | What to Change |
|:----|:---------|:---------------|
| `spring.datasource.password` | `application.yml` | MySQL password |
| `jwt.secret` | `application.yml` | Base64-encoded JWT signing key |
| `jwt.expiration` | `application.yml` | Token TTL (default: 24h) |

## Deployment

### Backend

```bash
mvn clean package
java -jar target/property-management-2.0.0.jar
```

### Frontend

```bash
cd frontend
npm run build   # outputs to dist/
```

Serve `dist/` with Nginx or any static file server. Reverse-proxy `/api` to the backend at `:8080`.

```nginx
server {
    listen 80;

    location / {
        root /var/www/property-frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

## Roadmap

- [ ] Unit tests (backend JUnit + frontend Vitest)
- [ ] Global exception handler
- [ ] Role-based access control (admin vs. user)
- [ ] File upload (avatar, complaint photos)
- [ ] Dashboard charts and statistics
- [ ] Docker Compose deployment
- [ ] i18n (Chinese / English)

## License

This project is licensed under the **MIT License**.

---

<div align="center">

**Built with Spring Boot & React**

</div>
