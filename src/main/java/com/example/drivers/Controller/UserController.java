package com.example.drivers.Controller;

import com.example.drivers.ApiResponse.ApiResponse;
import com.example.drivers.Model.Bid;
import com.example.drivers.Model.Offers;
import com.example.drivers.Model.Ticket;
import com.example.drivers.Model.User;
import com.example.drivers.Service.BidService;
import com.example.drivers.Service.OffersService;
import com.example.drivers.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
 public class UserController {
    private final UserService userService;
    private final BidService bidService;
    private final OffersService offersService;


    @GetMapping("/getAllUser")
    public ResponseEntity<?> getAllUser(){
        return ResponseEntity.status(200).body(new ApiResponse(""+userService.getAllUsers()));
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody @Valid User user , Errors errors){
        if(errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }
        userService.addUser(user);
        return ResponseEntity.status(200).body(new ApiResponse("User added"));
    }



    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody @Valid User user, Errors errors){
        if(errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);        }

        boolean isUpdated = userService.updateUser(id, user);
        if(isUpdated){
            return ResponseEntity.status(200).body(new ApiResponse("User updated"));
        }
        return ResponseEntity.status(404).body(new ApiResponse("User not found"));
    }


@DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id){
        boolean isDeleted = userService.deletUser(id);
        if(isDeleted){
            return ResponseEntity.status(200).body(new ApiResponse("User deleted"));
        }
        return ResponseEntity.status(404).body(new ApiResponse("User not found"));
    }



@GetMapping("/checkaUserExists/{id}")
    public ResponseEntity<?> checkUserExists(@PathVariable Integer id){

        boolean exists = userService.checkUserExists(id);
        if(exists){
            return ResponseEntity.status(200).body(new ApiResponse("User exists"));
        }
        return ResponseEntity.status(404).body(new ApiResponse("User not found"));
    }

    @GetMapping("/find-all-offers-by-userid/{userId}")
    public ResponseEntity<?>findAllOffersByUserId(@PathVariable Integer userId){
     List<Offers> offersList= userService.findAllOffersByUserId(userId);
     if(userId==null){
         return ResponseEntity.status(400).body(new ApiResponse("user id not found or dont have offers in this id"));

     }
     return ResponseEntity.status(200).body(offersList);
    }

    @DeleteMapping("/delete-offers-by-userid-offersid/{offersId}/{userId}")
    public ResponseEntity<?>deleteOffersByUserId(@PathVariable Integer offersId, @PathVariable Integer userId){
        if(!userService.checkUserExists(userId)){
            return ResponseEntity.status(400).body(new ApiResponse("user id not exists"));
        }
        if(!offersService.existOffersById(offersId)){
            return ResponseEntity.status(400).body(new ApiResponse("offers id not exists"));
        }
        offersService.deleteOffer(offersId);
        return ResponseEntity.status(200).body(new ApiResponse("offers deleted"));
    }

    @PostMapping("/add-offers-by-userid/{userId}")
    public ResponseEntity<?>addOffersByUserId(@PathVariable Integer userId,@RequestBody @Valid Offers offers, Errors errors){
        if(errors.hasErrors()){
            String message=errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        String result=offersService.createOffer(userId,offers);
        if(result.equalsIgnoreCase("Offer created successfully")){
            return ResponseEntity.status(200).body(new ApiResponse(result));
        }
        return ResponseEntity.status(400).body(new ApiResponse(result));
    }



    @GetMapping("/show-bids-by-offerid/{offersId}")
 public ResponseEntity<?>ShowBidsByOffersId(@PathVariable Integer offersId){
        List<Bid> bidList=bidService.getBidsByOfferId(offersId);
        if (bidList == null) {
            return ResponseEntity.status(404).body(new ApiResponse("Offer ID not found"));
        }
        if(bidList.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("bids not found in this offer id"));
        }
        return ResponseEntity.status(200).body(new ApiResponse(bidList.toString()));
 }

 @GetMapping("/chose-bid/{bidId}")
 public ResponseEntity<?>ChoseBidByBidId( @PathVariable Integer bidId){
        String result=bidService.ChoseBidByBidId(bidId);
        if(result.equalsIgnoreCase("bid chose successfully")){
            return ResponseEntity.status(200).body(new ApiResponse(result));
        }
        return ResponseEntity.status(400).body(new ApiResponse(result));
 }



    @PutMapping("/book-ticket/{userId}/{bidId}")
    public ResponseEntity<?> bookTicket(@PathVariable Integer userId, @PathVariable Integer bidId) {
        String result = userService.bookTicket(userId, bidId);

        if ("Ticket booked successfully".equalsIgnoreCase(result)) {
            return ResponseEntity.status(200).body(new ApiResponse(result));
        }

        return ResponseEntity.status(400).body(new ApiResponse(result));
    }

    @PutMapping("/cancel-ticket-by-userid/{userId}/{ticketId}")
    public ResponseEntity<?>cancelTicketByUser(@PathVariable Integer userId,@PathVariable Integer ticketId){
        String result=userService.cancelTicketByUser(userId,ticketId);
        if("Ticket cancelled by user successfully".equalsIgnoreCase(result)){
            return ResponseEntity.status(200).body(new ApiResponse(result));
        }
        return ResponseEntity.status(400).body(new ApiResponse(result));
    }



    @PutMapping("/rate-driver/{userId}/{ticketId}/{rating}/{comment}")
    public ResponseEntity <?>addDriverRating(
            @PathVariable Integer userId, @PathVariable Integer ticketId, @PathVariable Double rating, @PathVariable String comment) {

        // 1. التحقق المبدئي من نطاق التقييم
        if (rating == null || rating < 1.0 || rating > 5.0) {
            return ResponseEntity.status(400).body(new ApiResponse("Rating must be between 1.0 and 5.0"));
        }

        // 2. استدعاء ميثود الـ Service
        String result = userService.addDriverRating(userId, ticketId, rating, comment);

        // 3. معالجة الاستجابة
        if ("Driver rated successfully".equalsIgnoreCase(result)) {
            return ResponseEntity.status(200).body(new ApiResponse(result));
        }

        return ResponseEntity.status(400).body(new ApiResponse(result));
    }


    @PostMapping("/add-complaint-by-user/{userId}/{ticketId}/{description}")
    public ResponseEntity <?>addComplaintByUser(
            @PathVariable Integer userId,
            @PathVariable Integer ticketId,
            @PathVariable String description) {

        if (description == null || description.trim().isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("Description cannot be empty"));
        }

        String result = userService.addComplaintByUser(userId, ticketId, description);

        if ("Complaint submitted successfully by user".equalsIgnoreCase(result)) {
            return ResponseEntity.status(201).body(new ApiResponse(result));
        }

        return ResponseEntity.status(400).body(new ApiResponse(result));
    }


    @GetMapping("/get-user-rate-by-id/{userId}")
    public ResponseEntity<?>getUserRateById(@PathVariable Integer userId){
        String result=userService.getUserRateById(userId);
        if(result.equalsIgnoreCase("users id not foud")){
            return ResponseEntity.status(400).body(new ApiResponse(" users id not foud"));
        }
        return ResponseEntity.status(200).body(new ApiResponse(result));
    }


    @GetMapping("/get-driver-rate-by-id/{driverId}")
    public ResponseEntity<?>getDriverRateById(@PathVariable Integer driverId){
        String result=userService.getDriverRateById(driverId);
        if(result.equalsIgnoreCase("driver id not found")){
            return ResponseEntity.status(400).body(new ApiResponse(" driver id not found"));
        }
        return ResponseEntity.status(200).body(new ApiResponse(result));
    }
    @GetMapping("/get-all-ticket-by-user-id/{userId}")
    public ResponseEntity<?>getAllTicketByUserId(@PathVariable Integer userId ){
        List<Ticket> tickets=userService.getAllTicketByUserId(userId);
        if(tickets.isEmpty() || tickets==null){
            return ResponseEntity.status(400).body(new ApiResponse("there are no ticket in this id"));
        }
        return ResponseEntity.status(200).body(new ApiResponse(" "+tickets));
    }
//    @PutMapping("/complaint-handling/{userId}/{complaintId}")
//    public ResponseEntity<?>complaintHandling(@PathVariable Integer userId, @PathVariable Integer complaintId){
//        String result=userService.complaintHandling(userId,complaintId);
//        if(result.equalsIgnoreCase("there are no user in this id")){
//            ResponseEntity.status(400).body(new ApiResponse(result));
//        }
//        if(result.equalsIgnoreCase("This user does not have the authority to process the complaint")){
//            ResponseEntity.status(400).body(new ApiResponse(result));
//        }
//        if(result.equalsIgnoreCase("there are no complaint in this id")){
//            ResponseEntity.status(400).body(new ApiResponse(result));
//        }
//        return ResponseEntity.status(200).body(new ApiResponse(result));
//    }



}//end class