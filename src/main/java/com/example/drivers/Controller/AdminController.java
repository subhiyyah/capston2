package com.example.drivers.Controller;

import com.example.drivers.ApiResponse.ApiResponse;
import com.example.drivers.Model.Admin;
import com.example.drivers.Service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @GetMapping("/getAllAdmin")
    public ResponseEntity<?> getAllAdmin(){
        return ResponseEntity.status(200).body(new ApiResponse(""+ adminService.getAllAdmin()));
    }

    @PostMapping("/addAdmin")
    public ResponseEntity<?> addAdmin(@RequestBody @Valid Admin admin , Errors errors){
        if(errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        adminService.addAdmin(admin);
        return ResponseEntity.status(200).body(new ApiResponse("add admin successfully"));
    }
    @PutMapping("/updatedAdmin/{adminId}")
    public ResponseEntity<?>updateAdmin(@PathVariable Integer adminId ,@RequestBody @Valid Admin admin , Errors errors){
        if(errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }
        boolean isUpdated=adminService.updateAdmin(adminId,admin);
        if(!isUpdated){
            return ResponseEntity.status(400).body(new ApiResponse("can not found the id "));
        }
        return ResponseEntity.status(200).body(new ApiResponse("updated done "));
    }
    @DeleteMapping("/deleteAdmin/{id}")

    public ResponseEntity<?>deleteAdmin(@PathVariable Integer adminId){
        boolean isDeleted=adminService.deleteAdmin(adminId);
        if(isDeleted){
            return ResponseEntity.status(200).body(new ApiResponse("Admin deleted"));
        }
        return ResponseEntity.status(404).body(new ApiResponse("Admin not found"));
    }

    @PutMapping("/complaint-handling/{AdminId}/{complaintId}")
    public ResponseEntity<?>complaintHandling(@PathVariable Integer AdminId, @PathVariable Integer complaintId){
        String result=adminService.complaintHandling(AdminId,complaintId);
        if(result.equalsIgnoreCase("there are no user in this id")){
            ResponseEntity.status(400).body(new ApiResponse(result));
        }
        if(result.equalsIgnoreCase("This user does not have the authority to process the complaint")){
            ResponseEntity.status(400).body(new ApiResponse(result));
        }
        if(result.equalsIgnoreCase("there are no complaint in this id")){
            ResponseEntity.status(400).body(new ApiResponse(result));
        }
        return ResponseEntity.status(200).body(new ApiResponse(result));
    }

}