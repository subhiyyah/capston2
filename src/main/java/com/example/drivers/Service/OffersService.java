package com.example.drivers.Service;

import com.example.drivers.Model.Bid;
import com.example.drivers.Model.Offers;
import com.example.drivers.Model.User;
import com.example.drivers.Repository.OffersRepository;
import com.example.drivers.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OffersService {
    private final OffersRepository offersRepository;


    private final UserRepository userRepository;
    //add a method to create an offer for a user

//
//    public boolean createOffer(Integer userId, Offers offer) {
//        // Check if the user exists
//        if(!userRepository.existsById(userId)) {
//            return false; // User does not exist, cannot create offer
//        }
//        offer.setUserId(userId);
//        offersRepository.save(offer);
//        return true;
//
//
//    }

    public String createOffer(Integer userId, Offers offer) {
        // 1. التحقق من وجود المستخدم
        User user = userRepository.findUserById(userId);
        if (user == null) {
            return "User not found";
        }

        // 2. التحقق من وجود التواريخ
        if (offer.getStartDate() == null || offer.getEndDate() == null) {
            return "Start date and end date cannot be null";
        }

        // 3. التحقق من أن تاريخ النهاية بعد تاريخ البداية
        if (offer.getEndDate().isBefore(offer.getStartDate())) {
            return "End date cannot be before start date";
        }

        // 4. حساب عدد الأيام بين تاريخ البداية والنهاية
        long durationInDays = java.time.temporal.ChronoUnit.DAYS.between(offer.getStartDate(), offer.getEndDate());

        // الشرط: أكبر من 30 يوماً وأصغر من أو تساوي 90 يوماً
        if (durationInDays <= 30 || durationInDays > 90) {
            return "Offer duration must be greater than 30 days and less than or equal to 90 days";
        }

        // ربط العرض بـ userId والحفظ
        offer.setUserId(userId);

        offer.setStatus("pending");
        offersRepository.save(offer);

        return "Offer created successfully";
    }

    public List<Offers> getAllOffers() {
        return offersRepository.findAll();
    }

    //delete an offer by its id
    public boolean deleteOffer(Integer offerId) {
        if(!offersRepository.existsById(offerId)) {
            return false; // Offer does not exist, cannot delete
        }
        offersRepository.deleteById(offerId);
        return true;
    }

    //update an offer by its id
    public boolean updateOffer(Integer offerId, Offers offer) {

        if (!offersRepository.existsById(offerId)) {
            return false; // Offer does not exist, cannot update
        }
       Offers existingOffer = offersRepository.findOffersById(offerId);

        if (existingOffer!= null) {
           existingOffer.setRangePriceFrom(offer.getRangePriceFrom());
           existingOffer.setRangePriceTo(offer.getRangePriceTo());
           existingOffer.setLocationFrom(offer.getLocationFrom());
           existingOffer.setLocationTo(offer.getLocationTo());
           existingOffer.setDistance(offer.getDistance());
           existingOffer.setTripStartTime(offer.getTripStartTime());
           existingOffer.setTripArrivalTime(offer.getTripArrivalTime());
           existingOffer.setAcceptableDelay(offer.getAcceptableDelay());
           existingOffer.setStartDate(offer.getStartDate());
           existingOffer.setEndDate(offer.getEndDate());
           existingOffer.setWorkingDayPerWeek(offer.getWorkingDayPerWeek());
           existingOffer.setOffDays(offer.getOffDays());
           existingOffer.setStatus(offer.getStatus());

           offersRepository.save(existingOffer);


            return true;
        }
        return false; // Offer does not exist, cannot update
    }





    public List<Offers> getOffersByUserId(Integer userId) {
        return offersRepository.findOffersByUserId(userId);
    }


    public boolean existOffersById(Integer offersId){
        boolean isExist=offersRepository.existsById(offersId);
        if(isExist){
            return true;
        }
        return false;
    }

    public String getUserRateById(Integer userId){
        User user=userRepository.findUserById(userId);
        if(user==null){
            return "users id not foud";
        }
        String rating =" "+user.getRate();
        return rating;
    }

    }//end class