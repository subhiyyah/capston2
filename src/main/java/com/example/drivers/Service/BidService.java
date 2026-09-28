package com.example.drivers.Service;

import com.example.drivers.Model.Bid;
import com.example.drivers.Model.Offers;
import com.example.drivers.Model.Ticket;
import com.example.drivers.Model.User;
import com.example.drivers.Repository.BidRepository;
import com.example.drivers.Repository.DriversRepository;
import com.example.drivers.Repository.OffersRepository;
import com.example.drivers.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final DriversRepository driversRepository;
    private final OffersRepository offerRepository;
    private final UserRepository userRepository;

    public String addBid( Integer offerId, Bid bid) {
        boolean driverExists = driversRepository.existsById(bid.getDriverId());
        if (!driverExists) {
            return "Driver does not exist";
        }
        boolean offerExists = offerRepository.existsById(offerId);
        if (!offerExists) {
            return "Offer does not exist";
        }
        bid.setDriverId(bid.getDriverId());
        bid.setStatus("pending");
        bidRepository.save(bid);
        return "Bid added successfully";
    }

    public boolean deleteBid(Integer id) {
        Bid oldBid = bidRepository.findById(id).orElse(null);
        if (oldBid == null) {
            return false;
        }
        bidRepository.delete(oldBid);
        return true;
    }

    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    public Bid getBidByBidId(Integer bidId){
       return bidRepository.findBidById(bidId);
    }

    public boolean updateBid(Integer id, Bid bid) {
        Bid oldBid = bidRepository.findById(id).orElse(null);
        if (oldBid == null) {
            return false;
        }

        oldBid.setDriverId(bid.getDriverId());
        oldBid.setOfferId(bid.getOfferId());
        oldBid.setStatus(bid.getStatus());
        oldBid.setPrice(bid.getPrice());
        bidRepository.save(oldBid);
        return true;
    }


    public List<Bid> getBidsByOfferId(Integer offerId) {
        if(!offerRepository.existsById(offerId)) {
            return null; // Offer does not exist
        }
        return bidRepository.findByOfferId(offerId);
    }


    public List<Bid> showAcceptedBidsByDriverId(Integer driverId) {
        if(!driversRepository.existsById(driverId)) {
            return null; // Driver does not exist
        }
        return bidRepository.findByDriverIdAndStatus(driverId, "accepted");
    }



    public String ChoseBidByBidId( Integer bidId){
       Bid bid1=getBidByBidId(bidId);
       if(bid1==null){
           return "bid id not found";
       }
       if(bid1.getStatus().equalsIgnoreCase("accepted")){
           return " bid already accepted";
       }
            List<Bid> bids= getBidsByOfferId(bid1.getOfferId());
            for (Bid bid : bids) {
                if(bidId.equals(bid.getId())){
                    bid.setStatus("accepted");
                }
                else{
                bid.setStatus("rejected");

            }

        }bidRepository.saveAll(bids);
          return "bid chose successfully";
    }

    public Bid showAcceptBidByOfferId(Integer offerId){
        if(offerRepository.existsById(offerId)){
            List<Bid> bidList=getBidsByOfferId(offerId);
          for(int i = 0 ; i<bidList.size() ; i++){
              if(bidList.get(i).getStatus().equalsIgnoreCase("accepted")){
                  return bidList.get(i);
              }
          }
        }
        return null;
    }


    public String getUserRateById(Integer userId){
        User user=userRepository.findUserById(userId);
        if(user==null){
            return "users id not foud";
        }
        String rating =" "+user.getRate();
        return rating;
    }







}//end of class