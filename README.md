RideBids (I received it) - Ride-Hailing Bidding System API
RideBids (My Connection) is a robust, scalable backend RESTful API platform for a competitive ride-hailing and bidding service. Unlike traditional taxi platforms with fixed pricing, RideBids empowers passengers to broadcast ride requests and allows drivers to submit dynamic real-time price bids.
🌟Key Features
Dynamic Bidding Model: Passengers post ride offers; Driver network competes by bidding prices and ETAs.
Automated Vehicle Rating: Vehicles are assigned a system rating (1–5 stars) based on manufacturing year upon registration.
Pessimistic Concurrency Locking: Prevents double-booking race conditions during the ticket reservation flow.
Automated Cancellation & Penalty Engine: Financial protection rules enforcing a 100% passenger penalty on late cancellation vs. 100% full refund on driver cancellations.
Real-time Event Dispatch: WebSockets (Socket.io) integration for instant bidding updates and ticket confirmations.
Admin Governance Suite: Dispute resolution endpoints allowing administrators to review complaints and issue forced ticket cancellations/refunds.
Bi-directional Rating System: Passengers rate drivers and drivers rate passengers to maintain network safety.
🛠️Tech Stack
Runtime Environment: Node.js
Framework: Express.js
Database: PostgreSQL (Relational DB with Transactional Isolation)
Authentication: JSON Web Tokens (JWT) & bcrypt hashing

📐 End-to-End System Workflow
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


🚗 Automatic Vehicle Rating Algorithm
When a driver registers a vehicle via POST /vehicles, the backend automatically calculates the vehicle star rating based on model year without administrative manual overhead:
Manufacturing Year
AssignedRating
Smarter / Newer than 2024
5 Stars (★★★★★)
Equal to 2024
4 Stars (★★★★☆)
Older than 2024
3 Stars (★★★☆☆)


4. Cancellation Policy (POST /tickets/cancel)
Passenger-Initiated Cancellation:
100% cancellation fee retained.
Remaining 100% refunded to passenger wallet.
Retained portion remitted to compensate driver time.
Driver-Initiated Cancellation:
100% full refund issued to passenger.
Penalty flag applied to driver rating history.
5. Ratings & Dispute Governance (/ratings, /complaints, /admin)
POST /complaints - File a complaint against a ride session (PENDING state).
POST /ratings/rate-driver - Passenger submits driver review.
POST /ratings/rate-user - Driver submits passenger review.
GET /admin/complaints/pending - Admin reviews unresolved disputes.
DELETE /admin/tickets/cancel - Admin forced ticket cancellation & dispute settlement.
