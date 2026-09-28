package com.example.drivers.Controller;

import com.example.drivers.ApiResponse.ApiResponse;
import com.example.drivers.Model.Bid;
import com.example.drivers.Model.Drivers;
import com.example.drivers.Model.Offers;
import com.example.drivers.Service.BidService;
import com.example.drivers.Service.DriversService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor

public class DriversController {

    private final DriversService driversService;
    private final BidService bidService;


    @GetMapping("/getAllDrivers")
    public ResponseEntity<?> getAllDrivers() {
        return ResponseEntity.status(200).body(driversService.getAllDrivers());
    }

    @GetMapping("/getDriverById/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(driversService.getDriverById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDriver(@RequestBody @Valid Drivers driver, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }
        driversService.addDrivers(driver);
        return ResponseEntity.status(200).body(new ApiResponse("Driver added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable Integer id, @RequestBody @Valid Drivers driver, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }
        boolean isUpdated = driversService.updateDrivers(id, driver);
        if (!isUpdated) {
            return ResponseEntity.status(404).body(new ApiResponse("Driver not found"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("Driver updated"));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Integer id) {
        boolean isDeleted = driversService.deleteDrivers(id);
        if (!isDeleted) {
            return ResponseEntity.status(404).body(new ApiResponse("Driver not found"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("Driver deleted"));
    }



    @GetMapping("/show-offers")
    public ResponseEntity<?> showOffers() {
        List<Offers> offersList = driversService.showOffers();
        if (offersList.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("there is no offers"));
        }
        return ResponseEntity.status(200).body(offersList);
    }


    @PostMapping("/add-bids/{driverId}")
    public ResponseEntity<?> addBids(@PathVariable Integer driverId, @RequestBody @Valid Bid bid, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        String  result = driversService.addBids(driverId, bid);
        if ("Bid added successfully".equalsIgnoreCase(result)) {
            return ResponseEntity.status(200).body(new ApiResponse("add bid successfully"));

        }
        return ResponseEntity.status(400).body(new ApiResponse(result));
    }

    @GetMapping("/show-accepted-bids-by-driverid/{driverId}")
    public ResponseEntity<?> showAcceptedBidsByDriverId(@PathVariable Integer driverId) {
        List<Bid> bidList = bidService.showAcceptedBidsByDriverId(driverId);
        if (bidList.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("dont have any bid acceptable"));
        }
        if(bidList==null){
            return ResponseEntity.status(400).body(new ApiResponse("it is a null bid"));

        }
        return ResponseEntity.status(200).body(bidList);
    }

     @PutMapping("/cancel-ticket-by-driver/{driverId}/{ticketId}")
    public ResponseEntity<?> cancelTicketByDriver(@PathVariable Integer driverId, @PathVariable Integer ticketId) {
        String result = driversService.cancelTicketByDriver(driverId, ticketId);
        if (result.equalsIgnoreCase("Ticket cancelled by driver ,The amount has been refunded to the user.")) {
            return ResponseEntity.status(200).body(new ApiResponse(result));
        }
        return ResponseEntity.status(400).body(new ApiResponse(result));
    }



    @PutMapping("/rate-user/{driverId}/{ticketId}/{rating}/{comment}")
    public ResponseEntity<?> addUserRating(@PathVariable Integer driverId, @PathVariable Integer ticketId, @PathVariable Double rating, @PathVariable String comment) {

        // 1. التحقق المبدئي من نطاق التقييم
        if (rating == null || rating < 1.0 || rating > 5.0) {
            return ResponseEntity.status(400).body(new ApiResponse("Rating must be between 1.0 and 5.0"));
        }

        // 2. استدعاء ميثود الـ Service
        String result = driversService.addUserRating(driverId, ticketId, rating, comment);

        // 3. معالجة الاستجابة
        if ("User rated successfully".equalsIgnoreCase(result)) {
            return ResponseEntity.status(200).body(new ApiResponse(result));
        }

        return ResponseEntity.status(400).body(new ApiResponse(result));
    }

    @PostMapping("/add-complaint-by-driver/{driverId}/{ticketId}/{description}")
    public ResponseEntity<?> addComplaintByDriver(
            @PathVariable Integer driverId,
            @PathVariable Integer ticketId,
            @PathVariable String description) {

        if (description == null || description.trim().isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("Description cannot be empty"));
        }

        String result = driversService.addComplaintByDriver(driverId, ticketId, description);

        if ("Complaint submitted successfully by driver".equalsIgnoreCase(result)) {
            return ResponseEntity.status(201).body(new ApiResponse(result));
        }

        return ResponseEntity.status(400).body(new ApiResponse(result));
    }

    @GetMapping("/get-driver-rate-by-id/{driverId}")
    public ResponseEntity<?>getDriverRateById(@PathVariable Integer driverId){
        String result=driversService.getDriverRateById(driverId);
        if(result.equalsIgnoreCase("driver id not found")){
            return ResponseEntity.status(400).body(new ApiResponse(" driver id not found"));
        }
        return ResponseEntity.status(200).body(new ApiResponse(result));
    }

    @GetMapping("/get-user-rate-by-id/{userId}")
    public ResponseEntity<?>getUserRateById(@PathVariable Integer userId){
        String result=driversService.getUserRateById(userId);
        if(result.equalsIgnoreCase("users id not foud")){
            return ResponseEntity.status(400).body(new ApiResponse(" users id not foud"));
        }
        return ResponseEntity.status(200).body(new ApiResponse(result));
    }

//getUserRateById
}