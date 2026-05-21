# GymPro - Gym Management System

A complete, production-ready Gym Management System with modern UI/UX and full end-to-end functionality.

## Tech Stack

- **Backend:** Java 17 + Spring Boot 3.2
- **Frontend:** Vue 3 (Composition API) + Vite
- **Database:** PostgreSQL
- **ORM:** JPA/Hibernate
- **Auth:** JWT (JSON Web Tokens)
- **UI:** Tailwind CSS
- **State:** Pinia
- **HTTP:** Axios
- **Charts:** Chart.js + vue-chartjs

---

## Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven 3.8+

---

## Quick Start

### 1. Database Setup

```sql
-- Connect to PostgreSQL and create the database
CREATE DATABASE gym_management;
```

Then run the schema:

```bash
psql -U postgres -d gym_management -f gym-backend/src/main/resources/schema.sql
```

### 2. Backend Setup

```bash
cd gym-backend

# Set environment variables (optional - defaults are provided)
# export DB_USERNAME=postgres
# export DB_PASSWORD=postgres
# export JWT_SECRET=your-base64-secret

# Build and run
mvn clean install
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`

### 3. Frontend Setup

```bash
cd gym-frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`

### 4. Default Login

- **Username:** `admin`
- **Password:** `admin123`

---

## Project Structure

```
My_Gym/
├── gym-backend/                    # Spring Boot Backend
│   ├── src/main/java/com/gym/
│   │   ├── config/                 # Security, CORS config
│   │   ├── controller/             # REST Controllers
│   │   ├── dto/                    # Data Transfer Objects
│   │   ├── entity/                 # JPA Entities
│   │   ├── exception/              # Global Exception Handler
│   │   ├── repository/             # JPA Repositories
│   │   ├── security/               # JWT Auth components
│   │   └── service/                # Business Logic
│   └── src/main/resources/
│       ├── application.yml         # App configuration
│       └── schema.sql              # Database schema
│
├── gym-frontend/                   # Vue 3 Frontend
│   ├── src/
│   │   ├── api/                    # Axios config
│   │   ├── assets/                 # CSS styles
│   │   ├── components/             # Reusable components
│   │   ├── layouts/                # Layout components
│   │   ├── router/                 # Vue Router
│   │   ├── stores/                 # Pinia stores
│   │   └── views/                  # Page views
│   ├── index.html
│   ├── tailwind.config.js
│   └── vite.config.js
```

---

## Features

### Member Management

- Full CRUD operations with search & filter
- Profile view with payment history
- QR code for each member
- Membership auto-expiry tracking

### Membership Plans

- Monthly, Quarterly, Yearly plans
- Auto-calculate membership expiry dates
- Plan assignment linked to payments

### Trainer Management

- Add/Edit/Delete trainers
- Workload tracking (member count)
- Assign trainers to members

### Payments System

- Record payments (Cash, Card, UPI, Bank Transfer)
- Auto-activate membership on payment
- Payment status tracking (Paid/Pending)
- Payment history per member

### Attendance Tracking

- Manual check-in/check-out
- QR code check-in support
- Active membership validation
- Duplicate check-in prevention

### Dashboard

- Real-time statistics
- Revenue trends chart
- Membership growth chart
- Daily attendance chart
- Membership status breakdown

### Auth & Roles

- JWT authentication
- Role-based access: Admin, Trainer, Receptionist
- Protected API endpoints

### UI/UX

- Dark mode support
- Responsive design
- Loading states & empty states
- Toast notifications
- Clean, modern SaaS-style design

---

## API Endpoints

| Method     | Endpoint                        | Description          |
| ---------- | ------------------------------- | -------------------- |
| POST       | `/api/auth/login`               | Login                |
| POST       | `/api/auth/register`            | Register             |
| GET        | `/api/dashboard`                | Dashboard data       |
| GET/POST   | `/api/members`                  | List/Create members  |
| PUT/DELETE | `/api/members/{id}`             | Update/Delete member |
| GET/POST   | `/api/trainers`                 | List/Create trainers |
| GET/POST   | `/api/plans`                    | List/Create plans    |
| GET/POST   | `/api/payments`                 | List/Create payments |
| POST       | `/api/attendance/checkin/{id}`  | Check in             |
| POST       | `/api/attendance/checkout/{id}` | Check out            |
| POST       | `/api/attendance/checkin/qr`    | QR check-in          |
