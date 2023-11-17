package com.vincentrichard.dronemedication.service.serviceImpl;


import com.vincentrichard.dronemedication.entity.Drone;
import com.vincentrichard.dronemedication.entity.Medication;
import com.vincentrichard.dronemedication.exception.EmptyMedicationListException;
import com.vincentrichard.dronemedication.exception.ExcessWeightException;
import com.vincentrichard.dronemedication.exception.LowBatteryException;
import com.vincentrichard.dronemedication.exception.SerialNumberExistException;
import com.vincentrichard.dronemedication.model.enums.DroneState;
import com.vincentrichard.dronemedication.model.request.DroneRequest;
import com.vincentrichard.dronemedication.model.request.MedicationRequest;
import com.vincentrichard.dronemedication.model.response.DroneResponse;
import com.vincentrichard.dronemedication.model.response.MedicationResponse;
import com.vincentrichard.dronemedication.repository.DroneRepository;
import com.vincentrichard.dronemedication.repository.MedicationRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
class DroneServiceImplTest {
    @Mock
    private DroneRepository droneRepository;
    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private DroneServiceImpl droneService;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testRegisterDrone_SuccessfulRegistration(){
       DroneRequest droneRequest = new DroneRequest();
        droneRequest.setWeightLimit(100.0);
        droneRequest.setBatteryCapacity(80);
        when(droneRepository.existsBySerialNumber(any())).thenReturn(false);
        when(droneRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        DroneResponse droneResponse = droneService.registerDrone(droneRequest);
        assertEquals(DroneState.IDLE,droneResponse.getState());
        assertEquals(100.0,droneResponse.getWeightLimit(),0.01);
        assertEquals(80,droneResponse.getBatteryCapacity());
    }

    @Test
    public void givenInvalidBatteryCapacity_whenRegisterDrone_thenThrowException() {
        DroneRequest droneRequest = new DroneRequest();
        droneRequest.setWeightLimit(100.0);
        droneRequest.setBatteryCapacity(-20);

        assertThrows(ConstraintViolationException.class, () -> droneService.registerDrone(droneRequest));
    }
    @Test
    public void givenWeightLimit_whenRegisterDrone_thenThrowException() {
        DroneRequest droneRequest = new DroneRequest();
        droneRequest.setWeightLimit(501.0);
        droneRequest.setBatteryCapacity(50);

        assertThrows(ConstraintViolationException.class, () -> droneService.registerDrone(droneRequest));
    }
    @Test
    public void testRegisterDrone_whenSerialNumberExist_UnsuccessfulRegistration(){
        DroneRequest droneRequest = new DroneRequest();
        droneRequest.setWeightLimit(100.0);
        droneRequest.setBatteryCapacity(80);
        when(droneRepository.existsBySerialNumber(any())).thenReturn(true);
        assertThrows(SerialNumberExistException.class, () -> droneService.registerDrone(droneRequest));
    }
    @Test
    public void testDTOLoadDroneWithLowBattery(){
        Drone drone = new Drone();
        drone.setBatteryCapacity(10);
        when(droneRepository.findBySerialNumber(Mockito.anyString()))
                .thenReturn(java.util.Optional.of(drone));
        List<MedicationRequest> medicationRequests = new ArrayList<>();
        MedicationRequest medicationRequest = new MedicationRequest();
        medicationRequest.setCode("CITADEL");
        medicationRequest.setImageUrl("beautifulImage");
        medicationRequest.setName("MedicineA");
        medicationRequest.setWeight(150.0);
        medicationRequests.add(medicationRequest);

        LowBatteryException exception = assertThrows(LowBatteryException.class,
                ()->droneService.loadDrone("validSerialNumber",medicationRequests));
        assertEquals("BATTERY LEVEL MUST BE ABOVE 25% TO LOAD MEDICATION!",exception.getMessage());
    }

    @Test
    public void loadDroneWithMedication_EmptyMedication(){
        String serialNumber ="WORSHIP-YOUR-MAKER";
        List<MedicationRequest> emptyMedicationList = Collections.emptyList();

        Drone drone = new Drone();

        Mockito.when(droneRepository.findBySerialNumber(serialNumber)).thenReturn(java.util.Optional.of(drone));

        EmptyMedicationListException exception = assertThrows(EmptyMedicationListException.class, ()->
        {
            droneService.loadDrone(serialNumber,emptyMedicationList);
        });

        assertEquals(exception.getMessage(),"NO MEDICATION PROVIDED FOR LOADING.");
    }
    @Test
    public void givenValidWeightLimit_whenLoadDrone_thenProcessSuccessfully(){
        Drone drone = new Drone();
        drone.setBatteryCapacity(80);
        drone.setWeightLimit(300);

        when(droneRepository.findBySerialNumber(Mockito.anyString())).thenReturn(java.util.Optional.of(drone));
        List<MedicationRequest> medicationRequests = new ArrayList<>();
        MedicationRequest medicationRequest = new MedicationRequest();
        medicationRequest.setCode("CITADEL");
        medicationRequest.setImageUrl("beautifulImage");
        medicationRequest.setName("MedicineA");
        medicationRequest.setWeight(150.0);
        medicationRequests.add(medicationRequest);

        DroneResponse response = droneService.loadDrone("ValidSerialNumber",
                medicationRequests);
        assertEquals(DroneState.LOADING,response.getState());
    }

    @Test
    public void givenExcessMedicationWeight_whenLoadDrone_thenThrowException(){
        Drone drone = new Drone();
        drone.setBatteryCapacity(80);
        drone.setWeightLimit(300);

        when(droneRepository.findBySerialNumber(Mockito.anyString())).thenReturn(java.util.Optional.of(drone));
        List<MedicationRequest> medicationRequests = new ArrayList<>();
        MedicationRequest medicationRequest = new MedicationRequest();
        medicationRequest.setCode("CITADEL");
        medicationRequest.setImageUrl("beautifulImage");
        medicationRequest.setName("MedicineA");
        medicationRequest.setWeight(320.0);
        medicationRequests.add(medicationRequest);

        ExcessWeightException exception = assertThrows(ExcessWeightException.class,
                ()->droneService.loadDrone("validSerialNumber",medicationRequests));
        assertEquals("TOTAL WEIGHT OF MEDICATION MUST NOT EXCEED DRONE WEIGHT LIMIT!",exception.getMessage());
    }

    @Test
    public void testForLoadedMedication(){
    Drone drone = new Drone();
    drone.setBatteryCapacity(50);
    drone.setSerialNumber("validSerialNumber");


        List<Medication> medicationRequests = new ArrayList<>();

        Medication medicationRequest = new Medication();
        medicationRequest.setCode("CITADEL");
        medicationRequest.setImageUrl("beautifulImage");
        medicationRequest.setName("MedicineA");
        medicationRequest.setWeight(320.0);

        Medication medicationRequest1 = new Medication();
        medicationRequest1.setCode("BULGARIA");
        medicationRequest1.setImageUrl("AwesomeImage");
        medicationRequest1.setName("MedicineB");
        medicationRequest1.setWeight(450.0);

        medicationRequests.add(medicationRequest);
        medicationRequests.add(medicationRequest1);

        when(droneRepository.findBySerialNumber(Mockito.anyString())).thenReturn(java.util.Optional.of(drone));
        when(medicationRepository.findByDrone(Mockito.any())).thenReturn(medicationRequests);

        List<MedicationResponse> result = droneService.checkLoadedMedications("validSerialNumber");

        assertEquals(medicationRequests.size(), result.size());
        assertEquals("MedicineA",result.get(0).getName());
        assertEquals(2,result.size());

    }


    @Test
    public void testAvailableDroneForLoading(){

        Drone drone3 = new Drone();
        Drone drone4 = new Drone();
        Drone drone5 = new Drone();

        drone3.setState(DroneState.IDLE);
        drone3.setBatteryCapacity(45);
        drone3.setWeightLimit(150);

        drone4.setState(DroneState.IDLE);
        drone4.setWeightLimit(120);
        drone4.setBatteryCapacity(54);

        drone5.setState(DroneState.IDLE);
        drone5.setWeightLimit(120);
        drone5.setBatteryCapacity(54);

        List<Drone> droneList = new ArrayList<>();

        droneList.add(drone3);
        droneList.add(drone4);
        droneList.add(drone5);


        when(droneRepository.findAll()).thenReturn(droneList);

        List<DroneResponse> responses = droneService.checkAvailableDronesForLoading();

        assertEquals(droneList.size(), responses.size());
        assertEquals(DroneState.IDLE, responses.get(0).getState());
    }

    @Test
    public void testForAvailableDronesWithEmptyList(){
        List<Drone> emptyDroneList = new ArrayList<>();
        when(droneRepository.findAll()).thenReturn(emptyDroneList);

        List<DroneResponse> responseList = droneService.checkAvailableDronesForLoading();

        assertEquals(0,responseList.size());
    }

    @Test
    public void checkForDronesToWithNonAvailable(){
        Drone drone1 = new Drone();
        Drone drone2 = new Drone();

        drone1.setState(DroneState.IDLE);
        drone1.setWeightLimit(300);
        drone1.setBatteryCapacity(25);

        drone2.setState(DroneState.LOADED);
        drone2.setWeightLimit(400.0);
        drone2.setBatteryCapacity(60);

        List<Drone> droneList = new ArrayList<>();
        droneList.add(drone1);
        droneList.add(drone2);

        when(droneRepository.findAll()).thenReturn(droneList);

        List<DroneResponse> result = droneService.checkAvailableDronesForLoading();

        assertEquals(0,result.size());
    }


    @Test
    public void givenDroneWithHighBattery_whenCheckBatteryPercentage_thenReturnBatteryCapacity(){
        Drone drone = new Drone();
        drone.setSerialNumber("validSerialNumber");
        drone.setBatteryCapacity(76);

        when(droneRepository.findBySerialNumber(Mockito.anyString())).thenReturn(Optional.of(drone));
        Integer result = droneService.checkDroneBatteryLevel("ValidSerialNumber");

        assertEquals(drone.getBatteryCapacity(),result);
    }




}

