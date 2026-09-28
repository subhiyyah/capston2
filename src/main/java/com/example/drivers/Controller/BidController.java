package com.example.drivers.Controller;

import com.example.drivers.ApiResponse.ApiResponse;
import com.example.drivers.Model.Bid;
import com.example.drivers.Service.BidService;
import com.example.drivers.Service.DriversService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final DriversService driversService;


    @PostMapping("/createBid")
    public ResponseEntity<?> addBid(@PathVariable Integer offerId, @RequestBody @Valid Bid bid, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        String result = bidService.addBid(offerId, bid);
        if (result.equalsIgnoreCase("Driver does not exist")) {
            return ResponseEntity.status(400).body(new ApiResponse(result));
        }
        if (result.equalsIgnoreCase("Offer does not exist")) {
            return ResponseEntity.status(400).body(new ApiResponse(result));
        }
        return ResponseEntity.status(200).body(new ApiResponse("Bid created successfully"));

    }

    @DeleteMapping("/deleteBid/{bidId}")
    public ResponseEntity<?> deleteBid(@PathVariable Integer bidId) {
        boolean result = bidService.deleteBid(bidId);
        if (!result) {
            return ResponseEntity.status(400).body(new ApiResponse("Bid does not exist"));
        }else
            return ResponseEntity.status(200).body(new ApiResponse("Bid deleted successfully"));
    }

    @PutMapping("/updateBid/{bidId}")
    public ResponseEntity<?> updateBid(@PathVariable Integer bidId, @RequestBody @Valid Bid bid, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        boolean result = bidService.updateBid(bidId, bid);
        if (!result) {
            return ResponseEntity.status(400).body(new ApiResponse("Bid does not exist"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("Bid updated successfully"));
    }


    @GetMapping("/getAllBids")
    public ResponseEntity<?> getAllBids() {
        return ResponseEntity.status(200).body(bidService.getAllBids());
    }



}//end class