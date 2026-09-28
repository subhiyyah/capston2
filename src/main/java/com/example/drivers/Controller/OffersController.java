package com.example.drivers.Controller;

import com.example.drivers.ApiResponse.ApiResponse;
import com.example.drivers.Model.Offers;
import com.example.drivers.Service.OffersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class OffersController {
    private final OffersService offersService;


    @PostMapping("/createOffer/{userId}")
    public ResponseEntity<?> createOffer(@PathVariable Integer userId, @Valid @RequestBody Offers offers, Errors errors){
        if(errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        String result=offersService.createOffer(userId, offers);
        if(result.equalsIgnoreCase("Offer created successfull")){
            return ResponseEntity.status(200).body(new ApiResponse("Offer created successfully"));
        }
        else{
            return ResponseEntity.status(400).body(new ApiResponse( result));
        }
    }

    @GetMapping("/getAllOffers")
    public ResponseEntity<?> getAllOffers(){
        return ResponseEntity.status(200).body(offersService.getAllOffers());
}

    @DeleteMapping("/deleteOffer/{offerId}")
    public ResponseEntity<?> deleteOffer(@PathVariable Integer offerId){
        boolean isDeleted=offersService.deleteOffer(offerId);
        if(!isDeleted){
            return ResponseEntity.status(400).body(new ApiResponse( "Offer does not exist"));
        }
        else{
            return ResponseEntity.status(200).body(new ApiResponse("Offer deleted successfully"));
        }
    }

    @PutMapping("/updateOffer/{offerId}")
    public ResponseEntity<?> updateOffer(@PathVariable Integer offerId, @Valid @RequestBody Offers offers, Errors errors){
        if(errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        boolean isUpdated=offersService.updateOffer(offerId, offers);
        if(!isUpdated){
            return ResponseEntity.status(400).body(new ApiResponse( "Offer does not exist"));
        }
        else{
            return ResponseEntity.status(200).body(new ApiResponse("Offer updated successfully"));
        }
    }






}//end of class