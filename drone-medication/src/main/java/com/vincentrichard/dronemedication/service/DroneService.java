package com.vincentrichard.dronemedication.service;

import com.vincentrichard.dronemedication.entity.Medication;
import com.vincentrichard.dronemedication.model.request.DroneRequest;
import com.vincentrichard.dronemedication.model.request.MedicationRequest;
import com.vincentrichard.dronemedication.model.response.DroneResponse;
import com.vincentrichard.dronemedication.model.response.MedicationResponse;

import java.util.List;

public interface DroneService {
    DroneResponse registerDrone(DroneRequest request);

    DroneResponse loadDrone(String serialNumber, List<MedicationRequest> medicationLists);

    List<MedicationResponse> checkLoadedMedications(String serialNumber);

    List<DroneResponse> checkAvailableDronesForLoading();

    Integer checkDroneBatteryLevel(String serialNumber);

    void performPeriodicTask();

}
