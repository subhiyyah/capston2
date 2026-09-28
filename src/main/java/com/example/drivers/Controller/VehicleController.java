package com.example.drivers.Controller;

import com.example.drivers.ApiResponse.ApiResponse;
import com.example.drivers.Model.Vehicle;
import com.example.drivers.Service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.metamodel.mapping.internal.AbstractDomainPath;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor


public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping("/addVehicle")
    public ResponseEntity<?> addVehicle(@RequestBody @Valid Vehicle vehicle, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        vehicleService.addVehicle(vehicle);
        return ResponseEntity.status(200).body(new ApiResponse("Vehicle added successfully"));
    }

    @GetMapping("/getAllVehicles")
    public ResponseEntity<?> getAllVehicles() {
        return ResponseEntity.status(200).body(vehicleService.getAllVehicles());
    }

    @PutMapping("/updateVehicle/{id}")
    public ResponseEntity<?> updateVehicle(@PathVariable Integer id, @RequestBody @Valid Vehicle vehicle, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        if (!vehicleService.checkVehicleExists(id)) {
            return ResponseEntity.status(404).body(new ApiResponse("Vehicle not found"));
        }

       boolean isUpdated = vehicleService.updateVehicle(id, vehicle);
        if(!isUpdated) {
            return ResponseEntity.status(400).body(new ApiResponse("Vehicle could not be updated"));
        }


        return ResponseEntity.status(200).body(new ApiResponse("Vehicle updated successfully"));

    }

    @DeleteMapping("/deleteVehicle/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Integer id) {
        if (!vehicleService.checkVehicleExists(id)) {
            return ResponseEntity.status(404).body(new ApiResponse("Vehicle not found"));
        }
        boolean isDeleted = vehicleService.deleteVehicle(id);
        if (!isDeleted) {
            return ResponseEntity.status(400).body(new ApiResponse("Vehicle could not be deleted"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("Vehicle deleted successfully"));
    }
}