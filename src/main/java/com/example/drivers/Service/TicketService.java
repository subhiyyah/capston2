package com.example.drivers.Service;

import com.example.drivers.Model.*;
import com.example.drivers.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final BidRepository bidRepository;
    private final OffersRepository offersRepository;
    private final UserRepository userRepository;
    private final DriversRepository driversRepository;

    private final EmailService emailService;


    public boolean checkTicketExists(Integer ticketId) {
        return ticketRepository.existsById(ticketId);
    }

    public String createTicket(Integer userId, Integer bidId) {
        Bid bid = bidRepository.findById(bidId).orElse(null);
        if (bid == null) {
            return "Bid does not exist";
        }

        if (!"accepted".equalsIgnoreCase(bid.getStatus())) {
            return "Bid is not accepted";
        }

        Offers offer = offersRepository.findById(bid.getOfferId()).orElse(null);
        if (offer == null) {
            return "Offer does not exist";
        }

        User user = userRepository.findUserById(userId);
        if (user == null) {
            return "User does not exist";
        }

        if (!userId.equals(offer.getUserId())) {
            return "This offer does not belong to this user";
        }

        Drivers driver = driversRepository.findById(bid.getDriverId()).orElse(null);
        if (driver == null) {
            return "Driver does not exist";
        }

        if (ticketRepository.existsByBidId(bidId)) {
            return "Ticket has already been booked for this bid";
        }

        // ==========================================
        // 1. التحقق من المدة والتواريخ (30 - 90 يوماً)
        // ==========================================
        if (offer.getStartDate() == null || offer.getEndDate() == null) {
            return "Offer dates (start or end) cannot be null";
        }

        if (offer.getEndDate().isBefore(offer.getStartDate())) {
            return "End date cannot be before Start date";
        }

        long durationInDays = java.time.temporal.ChronoUnit.DAYS.between(offer.getStartDate(), offer.getEndDate());

        if (durationInDays < 30) {
            return "Booking duration must be at least 30 days";
        }

        if (durationInDays > 90) {
            return "Booking duration cannot exceed 90 days";
        }

        // ==========================================
        // 2. التحقق من السعر والرصيد
        // ==========================================
        Double price = bid.getPrice();
        if (price == null || !Double.isFinite(price) || price <= 0) {
            return "Bid price is invalid";
        }

        if (user.getBalance() == null || user.getBalance() < price) {
            return "User balance is insufficient";
        }

        // خصم المبلغ وإضافة الأرباح للسائق
        user.setBalance(user.getBalance() - price);

        if (driver.getTotalProfits() == null) {
            driver.setTotalProfits(0.0);
        }
        driver.setTotalProfits(driver.getTotalProfits() + price);
        driver.setNumberOfTrips(driver.getNumberOfTrips()+1);
        userRepository.save(user);
        driversRepository.save(driver);

        // ==========================================
        // 3. إنشاء كائن التذكرة وحفظه
        // ==========================================
        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setDriverId(driver.getId());
        ticket.setBidId(bidId);
        ticket.setFinalPrice(price);
        ticket.setStartDate(offer.getStartDate());
        ticket.setEndDate(offer.getEndDate());
        ticket.setStatus("completed");
        ticket.setBookingDate(java.time.LocalDateTime.now());//الوقت الحالي

        ticketRepository.save(ticket);
        emailService.sendEmail(user.getEmail(),"trip ticket add successfully","nice tripe with us");

        return "Ticket booked successfully";
    }

    public boolean deleteTicket(Integer ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            return false;
        }
        ticketRepository.deleteById(ticketId);
        return true;
    }

    public boolean updateTicket(Integer ticketId, Ticket ticket) {
        if (!ticketRepository.existsById(ticketId)) {
            return false;
        }
        Ticket existingTicket = ticketRepository.findById(ticketId).orElse(null);
        if (existingTicket != null) {
            existingTicket.setBidId(ticket.getBidId());
            existingTicket.setStatus(ticket.getStatus());
            existingTicket.setFinalPrice(ticket.getFinalPrice());
            ticketRepository.save(existingTicket);
            return true;
        }
        return false;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }


}//end of class