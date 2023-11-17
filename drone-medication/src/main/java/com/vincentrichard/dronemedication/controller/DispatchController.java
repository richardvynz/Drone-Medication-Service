package com.vincentrichard.dronemedication.controller;

import com.vincentrichard.dronemedication.model.request.DroneRequest;
import com.vincentrichard.dronemedication.model.request.MedicationRequest;
import com.vincentrichard.dronemedication.model.response.DroneResponse;
import com.vincentrichard.dronemedication.model.response.MedicationResponse;
import com.vincentrichard.dronemedication.service.DroneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/drones")
@Validated
@Tag(name = "DRONE MANAGEMENT API")
public class DispatchController {
    private final DroneService droneService;

    @Operation(summary = "Registering a new drone, given a valid weight(gr) and valid battery percentage(%)")
    @ApiResponse(
            responseCode = "201",
            description = "Http status 201 for CREATED"
    )
    @PostMapping("/register")
    public ResponseEntity<DroneResponse>registerDrone(@Valid @RequestBody DroneRequest request){
        return new ResponseEntity<>(droneService.registerDrone(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Loading a drone with medication, given a valid Medication(s)")
    @ApiResponse(
            responseCode = "201",
            description = "Http status 201 for CREATED"
    )
    @PostMapping("/{serialNumber}/load-drone")
    public ResponseEntity<DroneResponse>loadDroneWithMedication(@Valid @PathVariable String serialNumber,
                                                                @RequestBody List<@Valid MedicationRequest> medications){
        return new ResponseEntity<>(droneService.loadDrone(serialNumber, medications),HttpStatus.CREATED);
    }

    @Operation(summary = "Given a valid Serial, retrieving the Loaded medications in a given drone")
    @ApiResponse(
            responseCode = "200",
            description = "Http status 200 for OK"
    )
    @GetMapping("/{serialNumber}")
    public ResponseEntity<List<MedicationResponse>> checkLoadedMedication(@PathVariable String serialNumber){
        return new ResponseEntity<>(droneService.checkLoadedMedications(serialNumber),HttpStatus.OK);
    }
    @Operation(summary = "Check for the available drone that can be loaded with medication ")
    @ApiResponse(
            responseCode = "200",
            description = "Http status 200 for OK"
    )
    @GetMapping("/check-available-drone-for-loading")
    public ResponseEntity<List<DroneResponse>> checkAvailableDronesForLoading(){
        return new ResponseEntity<>(droneService.checkAvailableDronesForLoading(),HttpStatus.OK);
    }

    @Operation(summary = "Given a valid Serial, retrieving the Battery percentage of the given drone")
    @ApiResponse(
            responseCode = "200",
            description = "Http status 200 for OK"
    )
    @GetMapping("/batteryPercentage/{serialNumber}")
    public ResponseEntity<Integer> checkDroneBatteryLevel(@PathVariable String serialNumber){
        return new ResponseEntity<>(droneService.checkDroneBatteryLevel(serialNumber),HttpStatus.OK);
    }
}
