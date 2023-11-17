package com.vincentrichard.dronemedication.service.serviceImpl;

import com.vincentrichard.dronemedication.entity.Drone;
import com.vincentrichard.dronemedication.entity.Medication;
import com.vincentrichard.dronemedication.exception.*;
import com.vincentrichard.dronemedication.model.enums.DroneState;
import com.vincentrichard.dronemedication.model.request.DroneRequest;
import com.vincentrichard.dronemedication.model.request.MedicationRequest;
import com.vincentrichard.dronemedication.model.response.DroneResponse;
import com.vincentrichard.dronemedication.model.response.MedicationResponse;
import com.vincentrichard.dronemedication.model.util.ConstraintValidationUtil;
import com.vincentrichard.dronemedication.model.util.DroneModelUtil;
import com.vincentrichard.dronemedication.model.util.GenerateSerialNumberUtil;
import com.vincentrichard.dronemedication.repository.DroneRepository;
import com.vincentrichard.dronemedication.repository.MedicationRepository;
import com.vincentrichard.dronemedication.service.DroneService;
import jakarta.transaction.Transactional;
import jakarta.validation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
@NoArgsConstructor
public class DroneServiceImpl implements DroneService {
    @Autowired
    private  DroneRepository droneRepository;
    @Autowired
    private MedicationRepository medicationRepository;

    public DroneServiceImpl(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    public DroneServiceImpl(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    public DroneServiceImpl(DroneRepository droneRepository, MedicationRepository medicationRepository) {
        this.droneRepository = droneRepository;
        this.medicationRepository = medicationRepository;
    }

    private static final int MIN_BATTERY_LEVEL = 25;
    private static final int MAX_SERIAL_NUMBER_LENGTH = 100;

    @Override
    @Transactional
    public DroneResponse registerDrone(DroneRequest request) {


        ConstraintValidationUtil.validateRequest(request);

        Drone drone = new Drone();
        if(droneRepository.existsBySerialNumber(drone.getSerialNumber())){
            throw new SerialNumberExistException("DRONE WITH SERIAL NUMBER ALREADY EXIST");
        }
        drone.setWeightLimit(request.getWeightLimit());
        drone.setBatteryCapacity(request.getBatteryCapacity());
        drone.setState(DroneState.IDLE);
        drone.setModel(DroneModelUtil.determineModel(request.getWeightLimit()));
        drone.setSerialNumber(GenerateSerialNumberUtil.generateSerialNumber());

        droneRepository.save(drone);
        return mapDroneToDroneResponse(drone);
    }


    @Override
    @Transactional
    public DroneResponse loadDrone(String serialNumber, List<MedicationRequest> medicationLists) {
        if (medicationLists.isEmpty()) {
            throw new EmptyMedicationListException("NO MEDICATION PROVIDED FOR LOADING.");
        }

        Drone drone = getDroneBySerialNumber(serialNumber);

        validateBatteryLevelForLoading(drone);

        List<Medication> medications = createMedications(medicationLists);
        Double totalWeightLoaded = calculateTotalWeightLoaded(drone);

        loadMedicationsOntoDrone(drone, medications, totalWeightLoaded);

        updateDroneStateAfterLoading(drone, totalWeightLoaded);

        return constructDroneResponse(drone, medications);
    }


    private void validateBatteryLevelForLoading(Drone drone) {
        if (drone.getBatteryCapacity() <= MIN_BATTERY_LEVEL) {
            throw new LowBatteryException("BATTERY LEVEL MUST BE ABOVE 25% TO LOAD MEDICATION!");
        }
    }

    private List<Medication> createMedications(List<MedicationRequest> medicationRequests) {
        return medicationRequests.stream()
                .map(this::mapToMedication)
                .collect(Collectors.toList());
    }

    private Medication mapToMedication(MedicationRequest medicationRequest) {

        Medication medication = new Medication();
        medication.setName(medicationRequest.getName());
        medication.setWeight(medicationRequest.getWeight());
        medication.setCode(medicationRequest.getCode());
        medication.setImageUrl(medicationRequest.getImageUrl());

        return medication;
    }

    private void loadMedicationsOntoDrone(Drone drone, List<Medication> medications, Double totalWeightLoaded) {
        for (Medication medication : medications) {
            validateWeightForLoading(drone, totalWeightLoaded, medication);
            assignDroneToMedication(drone, medication);
            medicationRepository.save(medication);
            totalWeightLoaded += medication.getWeight();
        }
    }

    private void validateWeightForLoading(Drone drone, Double totalWeightLoaded, Medication medication) {
        if (medication.getWeight() + totalWeightLoaded > drone.getWeightLimit()) {
            throw new ExcessWeightException("TOTAL WEIGHT OF MEDICATION MUST NOT EXCEED DRONE WEIGHT LIMIT!");
        }
    }

    private void assignDroneToMedication(Drone drone, Medication medication) {
        medication.setDrone(drone);
    }

    private void updateDroneStateAfterLoading(Drone drone, Double totalWeightLoaded) {
        if (totalWeightLoaded == drone.getWeightLimit()) {
            drone.setState(DroneState.LOADED);
        } else {
            drone.setState(DroneState.LOADING);
        }
        droneRepository.save(drone);
    }

    private DroneResponse constructDroneResponse(Drone drone, List<Medication> medications) {
         DroneResponse droneResponse = mapDroneToDroneResponse(drone);
            droneResponse.setLoadedMedication("Medications: " + medications.stream().map(Medication::getName).toList() +
                        " were loaded successfully!");

                return droneResponse;
    }


    private MedicationResponse mapToMedicationResponse(Medication medication) {
    return new MedicationResponse(medication.getName(), medication.getWeight(),
            medication.getCode(), medication.getImageUrl(),
            medication.getCreatedAt(),medication.getUpdatedAt()
    );
}

    @Override
    public List<MedicationResponse> checkLoadedMedications(String serialNumber) {
        Drone drone = getDroneBySerialNumber(serialNumber);

        List<Medication> medicationList = medicationRepository.findByDrone(drone);

        return medicationList.stream()
                .map(this::mapToMedicationResponse)
                .collect(Collectors.toList());
    }



    @Override
    public List<DroneResponse> checkAvailableDronesForLoading() {

        List<Drone> allDrones = droneRepository.findAll();

        List<DroneResponse> listOfDrones = new ArrayList<>();

        for(Drone drone : allDrones){
            if((drone.getState()==DroneState.IDLE)&&(drone.getBatteryCapacity() > MIN_BATTERY_LEVEL)) {
                DroneResponse droneResponse = mapDroneToDroneResponse(drone);
                    listOfDrones.add(droneResponse);
            }
        }
        return listOfDrones;
    }

    @Override
    public Integer checkDroneBatteryLevel(String serialNumber) {
        return getDroneBySerialNumber(serialNumber).getBatteryCapacity();
    }
    @Override
    @Scheduled(fixedRate = 75000)
    public void performPeriodicTask() {
        List<Drone> allDrones = droneRepository.findAll();
        for (Drone drone : allDrones) {
            if (drone.getBatteryCapacity() <= MIN_BATTERY_LEVEL) {
                logLowBatteryEvent(drone);
            }
        }
    }

   private void logLowBatteryEvent(Drone drone) {
        log.warn("Low battery event for Drone with Serial Number: {} - Battery level is {}%.",
                drone.getSerialNumber(), drone.getBatteryCapacity());
    }

    private Drone getDroneBySerialNumber(String serialNumber) {
        validateSerialNumber(serialNumber);
        return droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(()
                        -> new WrongSerialNumberException("DRONE WITH THIS SERIAL NUMBER DOES NOT EXIST: " + serialNumber));
    }
    private Double calculateTotalWeightLoaded(Drone drone) {
        return medicationRepository.findByDrone(drone)
                .stream()
                .mapToDouble(Medication::getWeight)
                .sum();
    }

    private void validateSerialNumber(String serialNumber){
        if(serialNumber.length()>MAX_SERIAL_NUMBER_LENGTH){
            throw new SerialNumberLengthException("SERIAL NUMBER LENGTH CAN'T BE MORE THAN 100");
        }
    }

    private DroneResponse mapDroneToDroneResponse(Drone drone){
        DroneResponse droneResponse = new DroneResponse();
        droneResponse.setModel(drone.getModel());
        droneResponse.setSerialNumber(drone.getSerialNumber());
        droneResponse.setState(drone.getState());
        droneResponse.setBatteryCapacity(drone.getBatteryCapacity());
        droneResponse.setWeightLimit(drone.getWeightLimit());
        droneResponse.setCreatedAt(LocalDate.now());
        droneResponse.setUpdatedAt(LocalDate.now());
        return droneResponse;
    }
}