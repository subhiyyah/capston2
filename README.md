# 🚖 RideBids – Ride-Hailing Bidding System API

RideBids is a robust, scalable backend RESTful API for a competitive ride-hailing service. Unlike traditional taxi platforms with fixed pricing, RideBids lets passengers broadcast ride requests while drivers compete by submitting real-time price bids.

---

## 📑 Table of Contents

- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [System Workflow](#-system-workflow)
- [Automatic Vehicle Rating](#-automatic-vehicle-rating)
- [Cancellation Policy](#-cancellation-policy)
- [Ratings & Dispute Governance](#-ratings--dispute-governance)
- [API Endpoints Summary](#-api-endpoints-summary)

---

## 🌟 Key Features

- **Dynamic Bidding Model** – Passengers post ride offers; drivers compete by bidding prices and ETAs.
- **Automated Vehicle Rating** – Vehicles receive a system rating (1–5 stars) based on manufacturing year at registration.
- **Pessimistic Concurrency Locking** – Prevents double-booking race conditions during ticket reservation.
- **Automated Cancellation & Penalty Engine** – Financial protection rules: a 100% passenger penalty on late cancellation vs. a 100% full refund on driver cancellation.
- **Real-time Event Dispatch** – WebSockets (Socket.io) for instant bidding updates and ticket confirmations.
- **Admin Governance Suite** – Dispute resolution endpoints to review complaints and issue forced cancellations/refunds.
- **Bi-directional Rating System** – Passengers rate drivers and drivers rate passengers to maintain network safety.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Runtime | Node.js |
| Framework | Express.js |
| Database | PostgreSQL (relational, with transactional isolation) |
| Authentication | JSON Web Tokens (JWT) & bcrypt hashing |
| Real-time | Socket.io |

---

## 📐 System Workflow

```text
[Passenger] ---> Posts Offer            (POST /offers)
     |
[Drivers]   <--- Views Active Offers    (GET  /offers)
     |
[Drivers]   ---> Submits Price Bids     (POST /bids)
     |
[Passenger] ---> Selects Winning Bid    (POST /choose-bid)
     |
[Passenger] ---> Confirms & Books Ticket (POST /tickets/book) -- [DB Lock] --> [Ticket Issued]
     |
[Trip]      ---> [Completed]  OR  [Cancelled with Rules] (POST /tickets/cancel)
     |
[Review]    ---> Mutual Ratings (POST /ratings/*) & Complaints (POST /complaints)
```

---

## 🚗 Automatic Vehicle Rating

When a driver registers a vehicle via `POST /vehicles`, the backend automatically calculates the star rating from the model year, with no manual admin work.

| Manufacturing Year | Assigned Rating |
|--------------------|-----------------|
| Newer than 2024    | 5 Stars ★★★★★ |
| Equal to 2024      | 4 Stars ★★★★☆ |
| Older than 2024    | 3 Stars ★★★☆☆ |

---

## 💸 Cancellation Policy

Endpoint: `POST /tickets/cancel`

### Passenger-Initiated Cancellation
- 100% cancellation fee is retained.
- The retained amount is remitted to the driver as compensation for their time.

### Driver-Initiated Cancellation
- 100% full refund is issued to the passenger's wallet.
- A penalty flag is applied to the driver's rating history.

---

## ⭐ Ratings & Dispute Governance

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/complaints` | File a complaint against a ride session (starts in `PENDING` state) |
| `POST` | `/ratings/rate-driver` | Passenger submits a driver review |
| `POST` | `/ratings/rate-user` | Driver submits a passenger review |
| `GET` | `/admin/complaints/pending` | Admin reviews unresolved disputes |
| `DELETE` | `/admin/tickets/cancel` | Admin forced ticket cancellation & dispute settlement |

---

## 🔌 API Endpoints Summary

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| `POST` | `/offers` | Passenger | Post a ride offer |
| `GET` | `/offers` | Driver | View active offers |
| `POST` | `/bids` | Driver | Submit a price bid |
| `POST` | `/choose-bid` | Passenger | Select the winning bid |
| `POST` | `/tickets/book` | Passenger | Confirm & book ticket (DB lock) |
| `POST` | `/tickets/cancel` | Passenger / Driver | Cancel ticket with penalty rules |
| `POST` | `/vehicles` | Driver | Register a vehicle (auto-rated) |
| `POST` | `/complaints` | User | File a complaint |
| `POST` | `/ratings/rate-driver` | Passenger | Rate a driver |
| `POST` | `/ratings/rate-user` | Driver | Rate a passenger |
| `GET` | `/admin/complaints/pending` | Admin | List pending complaints |
| `DELETE` | `/admin/tickets/cancel` | Admin | Force-cancel a ticket |
