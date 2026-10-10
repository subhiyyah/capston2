<div align="center">

# 🚗 RideBids - Ride-Hailing Bidding System API

<p align="center">
  <b>A robust, scalable backend RESTful API platform for a competitive ride-hailing and bidding service.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Node.js-339933?style=flat-square&logo=nodedotjs&logoColor=white" alt="Node.js">
  <img src="https://img.shields.io/badge/Express.js-000000?style=flat-square&logo=express&logoColor=white" alt="Express.js">
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Socket.io-010101?style=flat-square&logo=socketdotjs&logoColor=white" alt="Socket.io">
  <img src="https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white" alt="JWT">
</p>

</div>

---

## 🌟 Key Features

* **Dynamic Bidding Model:** Passengers post ride offers; Driver network competes by bidding prices and ETAs.
* **Automated Vehicle Rating:** Vehicles are assigned a system rating (1–5 stars) based on manufacturing year upon registration.
* **Pessimistic Concurrency Locking:** Prevents double-booking race conditions during the ticket reservation flow.
* **Automated Cancellation & Penalty Engine:** Financial protection rules enforcing a 100% passenger penalty on late cancellation vs. 100% full refund on driver cancellations.
* **Real-time Event Dispatch:** WebSockets (Socket.io) integration for instant bidding updates and ticket confirmations.
* **Admin Governance Suite:** Dispute resolution endpoints allowing administrators to review complaints and issue forced ticket cancellations/refunds.
* **Bi-directional Rating System:** Passengers rate drivers and drivers rate passengers to maintain network safety.

---

## 🛠️ Tech Stack

* **Runtime Environment:** Node.js
* **Framework:** Express.js
* **Database:** PostgreSQL (Relational DB with Transactional Isolation)
* **Authentication:** JSON Web Tokens (JWT) & bcrypt hashing
* **Real-time Communication:** Socket.io

---

## 📐 End-to-End System Workflow

<div align="center">

```text
[Passenger] ---> Posts Offer (POST /offers) 
     |
[Drivers] <--- Views Active Offers (GET /offers) 
     |
[Drivers] ---> Submits Price Bids (POST /bids) 
     |
[Passenger] ---> Selects Winning Bid (POST /choose-bid) 
     |
[Passenger] ---> Confirms & Books Ticket (POST /tickets/book) -- [DB Lock]--> [Ticket Issued] 
     |
[Trip] ---> [Completed] OR [Cancelled with Rules (POST /tickets/cancel)] 
     |
[Review] ---> Mutual Ratings (POST /ratings/*) & Complaints (POST /complaints)



---

## 🚗 Automatic Vehicle Rating Algorithm

When a driver registers a vehicle via `POST /vehicles`, the backend automatically calculates the vehicle star rating based on model year without administrative manual overhead:

<table>
  <thead>
    <tr>
      <th>Manufacturing Year</th>
      <th>Assigned Rating</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><b>Smarter / Newer than 2024</b></td>
      <td>5 Stars (⭐⭐⭐⭐⭐)</td>
    </tr>
    <tr>
      <td><b>Equal to 2024</b></td>
      <td>4 Stars (⭐⭐⭐⭐☆)</td>
    </tr>
    <tr>
      <td><b>Older than 2024</b></td>
      <td>3 Stars (⭐⭐⭐☆☆)</td>
    </tr>
  </tbody>
</table>

---

## 📋 Cancellation Policy (`POST /tickets/cancel`)

* **Passenger-Initiated Cancellation:**
  * 100% cancellation fee retained.
  * Remaining 100% refunded to passenger wallet.
  * Retained portion remitted to compensate driver time.

* **Driver-Initiated Cancellation:**
  * 100% full refund issued to passenger.
  * Penalty flag applied to driver rating history.

---

## ⚖️ Ratings & Dispute Governance (`/ratings`, `/complaints`, `/admin`)

* `POST /complaints` — File a complaint against a ride session (`PENDING` state).
* `POST /ratings/rate-driver` — Passenger submits driver review.
* `POST /ratings/rate-user` — Driver submits passenger review.
* `GET /admin/complaints/pending` — Admin reviews unresolved disputes.
* `DELETE /admin/tickets/cancel` — Admin forced ticket cancellation & dispute settlement.
