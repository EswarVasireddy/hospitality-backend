# Hospitality Backend

A Spring Boot backend for a hotel room booking system — JWT-based
authentication, room inventory management, and a booking workflow with
availability tracking.

## Features

- User signup and login (JWT-based authentication, BCrypt password hashing)
- Room inventory: list, filter by availability/type, add/update/remove (admin-only)
- Bookings: create a booking (checks the room is free), view your own booking
  history, cancel a booking (frees the room back up)
- Role-based access: public room browsing, authenticated booking, admin-only
  inventory management
- Stateless JWT auth via a custom `OncePerRequestFilter` — no server-side sessions

## Tech Stack

- Language / Framework: Java 17, Spring Boot 3.4
- Security: Spring Security, JJWT (JSON Web Tokens), BCrypt
- Persistence: Spring Data JPA, MySQL
- Build tool: Maven
- Containerization: Docker

## Project Structure

```
src/main/java/com/klu/hospitality/
├── HospitalityApplication.java     # Application entry point
├── entity/
│   ├── User.java                   # id, username, email, password, role
│   ├── Room.java                   # roomNumber, roomType, pricePerNight, available
│   └── Booking.java                # room, user, checkInDate, checkOutDate, status
├── repository/
│   ├── UserRepository.java
│   ├── RoomRepository.java
│   └── BookingRepository.java
├── security/
│   ├── JwtUtil.java                 # token generation/validation
│   ├── JwtAuthFilter.java           # reads the Authorization header per request
│   └── SecurityConfig.java          # route rules + filter chain
└── controller/
    ├── AuthController.java          # /auth/signup, /auth/login
    ├── RoomController.java          # /api/rooms (CRUD)
    └── BookingController.java       # /api/bookings (create/list/cancel)
```

## API Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/auth/signup` | Public | Register a new user |
| POST | `/auth/login` | Public | Authenticate and receive a JWT |
| GET | `/api/rooms` | Public | List rooms (`?availableOnly=true` to filter) |
| GET | `/api/rooms/{id}` | Public | Get a single room |
| POST | `/api/rooms` | Admin | Add a room to inventory |
| PUT | `/api/rooms/{id}` | Admin | Update a room |
| DELETE | `/api/rooms/{id}` | Admin | Remove a room |
| GET | `/api/bookings/me` | Authenticated | The current user's booking history |
| POST | `/api/bookings` | Authenticated | Book a room for a date range |
| DELETE | `/api/bookings/{id}` | Authenticated (owner) | Cancel a booking, free the room |

## Getting Started

### Prerequisites
- Java 17+
- Maven
- MySQL running locally (or update `spring.datasource.*` in `application.properties`)

### Run locally
```bash
mvn spring-boot:run
```
Runs on `http://localhost:8081` by default. Update the MySQL credentials and
`jwt.secret` in `src/main/resources/application.properties` before running —
the checked-in values are local-dev placeholders only.

### Run with Docker
```bash
docker build -t hospitality-backend .
docker run -p 8081:8081 hospitality-backend
```

## Status

Standalone backend service — no frontend yet. The booking flow (create →
list → cancel, with room availability kept in sync) is the core piece this
repo demonstrates.
