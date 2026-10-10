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
