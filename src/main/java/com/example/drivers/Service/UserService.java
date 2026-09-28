package com.example.drivers.Service;

import com.example.drivers.Model.*;
import com.example.drivers.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Driver;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TicketService ticketService;

    private final UserRepository userRepository;
    private final OffersRepository offersRepository;
    private final BidRepository bidRepository;
    private final DriversRepository driversRepository;
    private final TicketRepository ticketRepository;

    private final ComplaintRepsitory complaintRepsitory;
;



    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void addUser(User user) {
        user.setName(user.getName());
        user.setAge(user.getAge());
        user.setPhone(user.getPhone());
        user.setEmail(user.getEmail());
        user.setBalance(user.getBalance());

        user.setRate(0.0);
        user.setTotalPayment(0.0);
        user.setNumberOfRate(0);
        user.setDeposit(0.0);
        user.setNumberOfCancellation(0);

        userRepository.save(user);
    }

    public boolean updateUser(Integer id, User user) {
        User oldUser = userRepository.findUserById(id);
        if (oldUser == null) {
            return false;
        } else {
            oldUser.setName(user.getName());
            oldUser.setAge(user.getAge());
            oldUser.setPhone(user.getPhone());
            oldUser.setRate(user.getRate());
//            oldUser.setRole(user.getRole());
            oldUser.setActive(user.getActive());
            oldUser.setBalance(user.getBalance());
            oldUser.setDeposit(user.getDeposit());
            oldUser.setNumberOfCancellation(user.getNumberOfCancellation());
            oldUser.setTotalPayment(user.getTotalPayment());
            userRepository.save(oldUser);
            return true;
        }

    }

    public boolean deletUser(Integer id) {
        User oldUser = userRepository.findUserById(id);
        if (oldUser == null) {
            return false;
        } else {
            userRepository.delete(oldUser);
            return true;
        }
    }

    public boolean setBlance(Integer userId,double balance){
      boolean isExiset=  userRepository.existsById(userId);
     User user=userRepository.findUserById(userId);
     if(isExiset){
         user.setBalance(user.getBalance()+balance);
         return true;
      }
     return false;
    }



    public boolean checkUserExists(Integer id) {
        return userRepository.existsById(id);
    }


    public List<Offers> findAllOffersByUserId(Integer userId){
        if(userRepository.existsById(userId)){
            return offersRepository.findAllOffersByUserId(userId);

        }
        return null;
    }


    public String bookTicket(Integer userId , Integer bidId){

        return ticketService.createTicket(userId,bidId);
    }


    public List<Ticket>getAllTicketByUserId(Integer userId ){

        User user=userRepository.findUserById(userId);
        if(user==null){
            return null;
        }

        List<Ticket> tickets=ticketRepository.findByUserId(userId);
        if(tickets==null || tickets.isEmpty()){
            return null;
        }
        return tickets;

    }

    public String cancelTicketByUser(Integer userId, Integer ticketId) {
        Ticket ticket = ticketRepository.findTicketById(ticketId);

        if (ticket == null) {
            return "Ticket not found";
        }

        if (ticket.getUserId() == null || !ticket.getUserId().equals(userId)) {
            return "Unauthorized action for this ticket";
        }

        if ("CANCELLED".equalsIgnoreCase(ticket.getStatus())) {
            return "Ticket is already cancelled";
        }

        User user = userRepository.findUserById(userId);
        if (user == null) {
            return "User not found";
        }

        Drivers driver = driversRepository.findDriversById(ticket.getDriverId());
        if (driver == null) {
            return "Driver not found";
        }

        // حساب عدد الأيام المنقضية من تاريخ الحجز حتى الآن
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(ticket.getBookingDate(), java.time.LocalDateTime.now());

        double originalPrice = ticket.getFinalPrice();

        if (daysBetween <= 3) {
            // الإلغاء خلال أول 3 أيام: إرجاع 80% للرصيد الأساسي و 20% لخانة العربون
            double refund80 = originalPrice * 0.80;
            double deposit20 = originalPrice * 0.20;

            user.setBalance(user.getBalance() + refund80);
            user.setDeposit(user.getDeposit() + deposit20);
            driver.setTotalProfits(driver.getTotalProfits()-refund80);
            driver.setCompensation(driver.getCompensation()+deposit20);

        } else {
            // الإلغاء بعد 3 أيام: لا يعود أي مبلغ مالي للمستخدم
            // يبقى البلنس والعربون بدون زيادة
        }

        // زيادة عدد مرات الإلغاء للمستخدم بـ 1
        user.setNumberOfCancellation(user.getNumberOfCancellation() + 1);

        // تحديث حالة التذكرة
        ticket.setStatus("CANCELLED");

        userRepository.save(user);
        ticketRepository.save(ticket);
        driversRepository.save(driver);

        return "Ticket cancelled by user successfully";
    }



    public String addDriverRating(Integer userId, Integer ticketId, Double rating, String comment) {
        // 1. التحقق من وجود التذكرة
        Ticket ticket = ticketRepository.findTicketById(ticketId);
        if (ticket == null) {
            return "Ticket not found";
        }


        // 2. التحقق من أن التذكرة تابعة للمستخدم
        if (ticket.getUserId() == null || !ticket.getUserId().equals(userId)) {
            return "Unauthorized action for this ticket";
        }


        // 3. التحقق من عدم تقييم التذكرة سابقاً
        if (ticket.getUserRatingForDriver() != null) {
            return "You have already rated the driver for this ticket";
        }

        // 4. حفظ تقييم التذكرة
        ticket.setUserRatingForDriver(rating);
        ticket.setUserCommentForDriver(comment);
        ticketRepository.save(ticket);

        // 5. جلب السائق وتحديث التقييم بالمعادلة البسيطة
        Drivers driver = driversRepository.findDriversById(ticket.getDriverId());
        if (driver == null) {
            return "Driver not found";
        }
        Integer previousRatings = driver.getNumberOfRate();
        if (previousRatings == null) {
            previousRatings = 0;
        }

        double currentRating = 0.0;
        if (driver.getRate() != null) {
            currentRating = driver.getRate();
        }
        double newAverageRating =
                ((currentRating * previousRatings) + rating) / (previousRatings + 1);

        driver.setRate(newAverageRating);
        driver.setNumberOfRate(previousRatings + 1);

        driversRepository.save(driver);

        return "Driver rated successfully";
    }

    public String addComplaintByUser(Integer userId, Integer ticketId, String description) {
        // 1. التحقق من وجود التذكرة
        Ticket ticket = ticketRepository.findTicketById(ticketId);
        if (ticket == null) {
            return "Ticket not found";
        }

        // 2. التحقق من أن التذكرة تابعة للمستخدم
        if (ticket.getUserId() == null || !ticket.getUserId().equals(userId)) {
            return "Unauthorized action for this ticket";
        }

        // 3. إنشاء كيان الشكوى وحفظه
        Complaint complaint = new Complaint();
        complaint.setTicketId(ticketId);
        complaint.setRole("user");
        complaint.setDescription(description);
        complaint.setStatus("pending");

    complaintRepsitory.save(complaint);

        return "Complaint submitted successfully by user";
    }

    public String getDriverRateById(Integer driverId){
        Drivers driver=driversRepository.findDriversById(driverId);
        if(driver==null){
            return "drivere id not found";
        }
        String rating=" " +driver.getRate();
        return rating;
    }

    public String getUserRateById(Integer userId){
        User user=userRepository.findUserById(userId);
        if(user==null){
            return "users id not foud";
        }
        String rating =" "+user.getRate();
        return rating;
    }
//    public String complaintHandling(Integer userId, Integer complaintId){
//        User user=userRepository.findUserById(userId);
//        if(user==null){
//            return "there are no user in this id";
//        }
//
//        //============================================================
//        //اخليه يستقبل الادمن اي دي مو اليوزر
//        if(!user.getRole().equalsIgnoreCase("ADMIN")){
//            return "This user does not have the authority to process the complaint.";
//        }
//        Complaint complaint=complaintRepsitory.findComplaintById(complaintId);
//        if(complaint==null){
//            return "there are no complaint in this id";
//        }
//        Ticket ticket=ticketRepository.findTicketById(complaint.getTicketId());
//        double finalprice=ticket.getFinalPrice();
//
//        Drivers drivers=driversRepository.findDriversById(ticket.getDriverId());
//
////        if(complaint.getDescription().contains("assault, hit, struck, punched, slapped, violence, fight, brawl, threat, threatened, weapon, knife, gun, kidnap, abduction, harassment, harassed, touched, sexual, suggestive, filmed, stalked, profanity, cursed, swore, insulted, abuse, reckless, speeding, red light, drunk, intoxicated, high, drugs, alcohol, accident, crashed, stole, theft, robbery, extortion, demanded cash, overcharged, fraud, scam, racist, racism, discrimination, slurs")) {
//        if (complaint.getDescription() != null && complaint.getDescription().toLowerCase().contains("violence") || complaint.getDescription().toLowerCase().contains("harassment") || complaint.getDescription().toLowerCase().contains("racism") || complaint.getDescription().toLowerCase().contains("extortion")) {
//
//            if (complaint.getRole().equalsIgnoreCase("user")) {
//
//                drivers.setTotalProfits(drivers.getTotalProfits() - finalprice);
//                drivers.setNumberOfTrips(drivers.getNumberOfTrips() - 1);
//                user.setBalance(user.getBalance() + finalprice);
//                user.setTotalPayment(user.getTotalPayment() - finalprice);
//
//                userRepository.save(user);
//                driversRepository.save(drivers);
//                ticketService.deleteTicket(ticket.getId());
//
//                complaint.setStatus("resolve");
//                complaintRepsitory.save(complaint);
//
//                return "The issue has been resolved, the ticket cancelled, and the full amount refunded to the customer.";
//
//            }
//            if (complaint.getRole().equalsIgnoreCase("driver")) {
//                drivers.setCompensation(finalprice+drivers.getTotalProfits());
//                driversRepository.save(drivers);
//                ticketService.deleteTicket(ticket.getId());
//                complaint.setStatus("resolve");
//                complaintRepsitory.save(complaint);
//
//                return "The issue has been resolved and the ticket cancelled; the ticket amount will be issued to you as compensation.";
//
//
//            }
//        }else {
//            complaint.setStatus("closed");
//            complaintRepsitory.save(complaint);
//            return "The ticket has been closed; there is no need to cancel the reservation.";
//        }
//
//       return "\"The complaint has been closed without action; no policy violations were detected.";
//    }




//    public String bookTicket(Integer userId, Integer bidId) {
//        Bid bid = bidRepository.findById(bidId).orElse(null);
//        if (bid == null) {
//            return "Bid does not exist";
//        }
//
//        if (!"accepted".equalsIgnoreCase(bid.getStatus())) {
//            return "Bid is not accepted";
//        }
//
//        Offers offer = offersRepository.findById(bid.getOfferId()).orElse(null);
//        if (offer == null) {
//            return "Offer does not exist";
//        }
//
//        User user = userRepository.findBymeId(userId);
//        if (user == null) {
//            return "User does not exist";
//        }
//
//        if (!userId.equals(offer.getUserId())) {
//            return "This offer does not belong to this user";
//        }
//
//        Drivers driver = driversRepository.findById(bid.getDriverId()).orElse(null);
//        if (driver == null) {
//            return "Driver does not exist";
//        }
//
//        if (ticketRepository.existsByBidId(bidId)) {
//            return "Ticket has already been booked for this bid";
//        }
//
//        // ==========================================
//        // 1. التحقق من المدة والتواريخ (30 - 90 يوماً)
//        // ==========================================
//        if (offer.getStartDate() == null || offer.getEndDate() == null) {
//            return "Offer dates (start or end) cannot be null";
//        }
//
//        if (offer.getEndDate().isBefore(offer.getStartDate())) {
//            return "End date cannot be before Start date";
//        }
//
//        long durationInDays = java.time.temporal.ChronoUnit.DAYS.between(offer.getStartDate(), offer.getEndDate());
//
//        if (durationInDays < 30) {
//            return "Booking duration must be at least 30 days";
//        }
//
//        if (durationInDays > 90) {
//            return "Booking duration cannot exceed 90 days";
//        }
//
//        // ==========================================
//        // 2. التحقق من السعر والرصيد
//        // ==========================================
//        Double price = bid.getPrice();
//        if (price == null || !Double.isFinite(price) || price <= 0) {
//            return "Bid price is invalid";
//        }
//
//        if (user.getBalance() == null || user.getBalance() < price) {
//            return "User balance is insufficient";
//        }
//
//        // خصم المبلغ وإضافة الأرباح للسائق
//        user.setBalance(user.getBalance() - price);
//
//        if (driver.getTotalProfits() == null) {
//            driver.setTotalProfits(0.0);
//        }
//        driver.setTotalProfits(driver.getTotalProfits() + price);
//
//        userRepository.save(user);
//        driversRepository.save(driver);
//
//        // ==========================================
//        // 3. إنشاء كائن التذكرة وحفظه
//        // ==========================================
//        Ticket ticket = new Ticket();
//        ticket.setUserId(userId);
//        ticket.setDriverId(driver.getId());
//        ticket.setBidId(bidId);
//        ticket.setFinalPrice(price);
//        ticket.setStartDate(offer.getStartDate());
//        ticket.setEndDate(offer.getEndDate());
//        ticket.setStatus("CONFIRMED");
//        ticket.setBookingDate(java.time.LocalDateTime.now());//الوقت الحالي
//
//        ticketRepository.save(ticket);
//
//        return "Ticket booked successfully";
//    }




//    public String bookTicket(Integer userId, Integer bidId) {
//        Bid bid = bidRepository.findById(bidId).orElse(null);
//        if (bid == null) {
//            return "Bid does not exist";
//        }
//
//        if (!"accepted".equals(bid.getStatus())) {
//            return "Bid is not accepted";
//        }
//
//        Offers offer = offersRepository.findById(bid.getOfferId()).orElse(null);
//        if (offer == null) {
//            return "Offer does not exist";
//        }
//
//        User user = userRepository.findBymeId(userId);
//        if (user == null) {
//            return "User does not exist";
//        }
//
//        if (!userId.equals(offer.getUserId())) {
//            return "This offer does not belong to this user";
//        }
//
//        Drivers driver = driversRepository.findById(bid.getDriverId()).orElse(null);
//        if (driver == null) {
//            return "Driver does not exist";
//        }
//
//        if (ticketRepository.existsByBidId(bidId)) {
//            return "Ticket has already been booked for this bid";
//        }
//
//        Double price = bid.getPrice();
//        if (price == null || !Double.isFinite(price) || price <= 0) {
//            return "Bid price is invalid";
//        }
//
//        if (user.getBalance() == null || user.getBalance() < price) {
//            return "User balance is insufficient";
//        }
//
//        user.setBalance(user.getBalance() - price);
//
//        if (driver.getTotalProfits() == null) {
//            driver.setTotalProfits(0.0);
//        }//لو كانت نلل نخليها صفر عشان نجمع عليها
//
//        driver.setTotalProfits(driver.getTotalProfits() + price);
//
//        userRepository.save(user);
//        driversRepository.save(driver);
//
//        Ticket ticket = null;
//        ticket.setBidId(bidId);
//        ticket.setStatus("completed");
//        ticket.setFinalPrice(bid.getPrice());
//
//        ticketRepository.save(ticket);
//
//        return "Ticket booked successfully";
//    }
//


}//end class