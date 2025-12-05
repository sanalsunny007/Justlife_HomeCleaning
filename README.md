# 🧹 JustLife Home Cleaning Service — Backend API

A Spring Boot–based backend system that manages cleaner availability, computes free working windows, and handles booking creation and updates with constraints such as working hours, breaks, and assigning cleaners from the same vehicle.

---

## 📑 Table of Contents

1. [Project Description](#project-description)
2. [Tech Stack](#tech-stack)
3. [Prerequisites](#prerequisites)
4. [Getting Started](#getting-started)
5. [Database & Initial Data](#database--initial-data)
6. [API Documentation & Postman Collection](#api-documentation--postman-collection)
7. [API Endpoints Overview](#api-endpoints-overview)
8. [Sample API Requests & Responses](#sample-api-requests--responses)

---

## 📘 Project Description

This backend service enables:

- Cleaner availability lookup
- Computing free time windows for each cleaner
- Booking creation with automatic cleaner allocation
- Booking updates while preserving cleaner assignments
- Enforced business rules (break buffers, working hours, non-working days, etc.)

The application follows clean architecture with modular components:  
**controllers → services → strategies → availability rules → repositories.**

---

## 🛠 Tech Stack

| Component | Technology |
|----------|------------|
| Language | **Java 21** |
| Framework | **Spring Boot 4.0.0** |
| Build Tool | **Maven** |
| ORM | **JPA + Hibernate** |
| DB | **MySQL** |
| Utilities | **Lombok** |
| Documentation | **Swagger (springdoc-openapi)** |

---

## 🧾 Prerequisites

Before running the project, ensure:

- **MySQL 8+** is installed and running on **port 3306**
- A database named `justlife` exists
- Java 21 installed

---

## 🚀 Getting Started

### 1️⃣ Clone the repository

```bash
git clone https://github.com/sanalsunny007/Justlife_HomeCleaning.git
cd Justlife_HomeCleaning
```
### 2️⃣ Configure database in `application.yml`

Update the datasource section to match your MySQL configuration:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/justlife
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: root
```
### 3️⃣ Build & Run the Application

#### 🏗 Build the project
```bash
./mvnw clean install
```
#### 🚀 Run the application
```bash
./mvnw spring-boot:run
```
#### 🌐 Access the application
http://localhost:8080

> ⚠️ **Note:** At startup, Spring Data will automatically create tables and 
> load initial data using a DataLoader(5 vehicles + 25 cleaners).  
> Scripts are located in `src/main/resources/db/migration`.


## 📘 API Documentation (Swagger UI)

Once the application is running:

- **Swagger UI:**  
  http://localhost:8080/swagger-ui.html

- **OpenAPI JSON:**  
  http://localhost:8080/v3/api-docs

- **OpenAPI YAML:**  
  http://localhost:8080/v3/api-docs.yaml

You may import the YAML file directly into Postman.

## 🗄 Database Initialization

The system preloads:

- **5 Vehicles**
- **25 Cleaners** (5 cleaners per vehicle)

Data is inserted via DataLoader.Scripts are kept in path
```bash
src/main/resources/db/migration
```
## 🛎 API Endpoints

All endpoints are versioned as follows:
```bash
/api/v1/availability
/api/v1/bookings
```
## 📆 Availability API

### ✔ GET `/api/v1/availability`
Returns all cleaners with their available time windows for a specific date.

### ✔ GET `/api/v1/availability/slot`
Checks availability for a specific date, start time, and duration.

## 📦 Booking API

### ✔ POST `/api/v1/bookings`
Creates a new booking and assigns available cleaners.

### ✔ PUT `/api/v1/bookings/{id}`
Updates an existing booking (start time, duration, etc.).

### ✔ GET `/api/v1/bookings/{id}`
Fetches details of a specific booking.

## 🧪 Sample Requests & Responses

---

### ✅ 1. Check Availability for a Date

**Request**
```json
GET /api/v1/availability?date=2025-12-05
```

**Response**
```json
[
  {
    "cleanerId": 1,
    "cleanerName": "Cleaner-1-1",
    "vehicleId": 1,
    "vehicleName": "Vehicle-1",
    "availableWindows": [
      { "start": "08:00", "end": "10:00" },
      { "start": "12:00", "end": "22:00" }
    ]
  }
]
```
### ✅ 2. Check Cleaners for a Timeslot

**Request**
```json
GET /api/v1/availability/slot?date=2025-12-05&start=10:00&durationHours=2
```
**Response**
```json
[
  {
    "cleanerId": 2,
    "cleanerName": "Cleaner-1-2",
    "vehicleId": 1,
    "vehicleName": "Vehicle-1"
  }
]
```
### 🆕 3. Create Booking
```json
POST /api/v1/bookings
```
**Request**

```json

{
  "customerName": "John Doe",
  "startDateTime": "2025-12-05T10:00",
  "durationHours": 2,
  "cleanerCount": 2
}
```
**Response**
```json
{
  "cleanerIds": [2, 3],
  "customerName": "John Doe",
  "durationHours": 2,
  "endDateTime": "2025-12-05T12:00:00",
  "id": 101,
  "requiredCleanerCount": 2,
  "startDateTime": "2025-12-05T10:00:00",
  "status": "CONFIRMED",
  "vehicleId": 1
}
```
### 🔄 4. Update Booking
```json
PUT /api/v1/bookings/101
```
**Request**
```json
{
  "newStartDateTime": "2025-12-05T14:00"
}
```
**Response**
```json
{
  "cleanerIds": [2, 3],
  "customerName": "John Doe",
  "durationHours": 2,
  "endDateTime": "2025-12-06T16:00:00",
  "id": 101,
  "requiredCleanerCount": 2,
  "startDateTime": "2025-12-06T14:00:00",
  "status": "CONFIRMED",
  "vehicleId": 1
}
```

### 📄 5. Get Booking Details

**Request**
```json
GET /api/v1/bookings/101
```

**Response**
```json
{
  "cleanerIds": [2, 3],
  "customerName": "John Doe",
  "durationHours": 2,
  "endDateTime": "2025-12-06T20:00:00",
  "id": 101,
  "requiredCleanerCount": 2,
  "startDateTime": "2025-12-06T16:00:00",
  "status": "CONFIRMED",
  "vehicleId": 1
}
```
