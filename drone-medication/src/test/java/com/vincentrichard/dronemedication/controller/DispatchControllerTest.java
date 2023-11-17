package com.vincentrichard.dronemedication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincentrichard.dronemedication.entity.Drone;
import com.vincentrichard.dronemedication.entity.Medication;
import com.vincentrichard.dronemedication.exception.*;
import com.vincentrichard.dronemedication.model.enums.DroneModel;
import com.vincentrichard.dronemedication.model.enums.DroneState;
import com.vincentrichard.dronemedication.model.request.DroneRequest;
import com.vincentrichard.dronemedication.model.request.MedicationRequest;
import com.vincentrichard.dronemedication.model.response.DroneResponse;
import com.vincentrichard.dronemedication.model.response.MedicationResponse;
import com.vincentrichard.dronemedication.repository.DroneRepository;
import com.vincentrichard.dronemedication.repository.MedicationRepository;
import com.vincentrichard.dronemedication.service.DroneService;
import com.vincentrichard.dronemedication.service.serviceImpl.DroneServiceImpl;
import jakarta.validation.ConstraintViolationException;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.yaml.snakeyaml.events.Event;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DispatchController.class)
public class DispatchControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DroneService droneService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerDrone_validInput_returnSuccess(){
        DroneService droneService = mock(DroneService.class);
        DispatchController dispatchController = new DispatchController(droneService);

        DroneRequest droneRequest = new DroneRequest();
        droneRequest.setBatteryCapacity(480);
        droneRequest.setBatteryCapacity(90);

        DroneResponse droneResponse = new DroneResponse();
        droneResponse.setBatteryCapacity(90);
        droneResponse.setWeightLimit(480.00);
        droneResponse.setSerialNumber("WDSAGHUJHFOIUH");
        droneResponse.setState(DroneState.IDLE);
        droneResponse.setModel(DroneModel.HEAVYWEIGHT);

        when(droneService.registerDrone(droneRequest)).thenReturn(droneResponse);

        ResponseEntity<DroneResponse> response = dispatchController.registerDrone(droneRequest);

        verify(droneService, times(1)).registerDrone(droneRequest);

        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        assertEquals(droneResponse,response.getBody());
    }

    @Test
    public void registerDrone_inValidWeightInput_throwException(){
        DroneService droneService = mock(DroneService.class);
        DispatchController dispatchController = new DispatchController(droneService);

        DroneRequest droneRequest = new DroneRequest();
        droneRequest.setBatteryCapacity(120);
        droneRequest.setWeightLimit(700.00);

        when(droneService.registerDrone(droneRequest))
                .thenThrow(new ExcessWeightException("weight cannot be more than 500gr"));

        ExcessWeightException exception = assertThrows(ExcessWeightException.class, () ->
                dispatchController.registerDrone(droneRequest));

        assertEquals("weight cannot be more than 500gr",exception.getMessage());

    }


    @Test
    public void testRegisterDrone_InvalidBatteryInput() throws Exception {
        DroneRequest droneRequest = new DroneRequest();
        droneRequest.setWeightLimit(500.0);
        droneRequest.setBatteryCapacity(120);

        doThrow(new ExcessWeightException("Percentage can not be more ttthan 100")).when(droneService).registerDrone(any(DroneRequest.class));

        mockMvc.perform(post("/api/drones/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(droneRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loadDroneWithMedication_ValidInput_ReturnsResponseEntityWithHttpStatusCreated() {
        DroneServiceImpl droneService = mock(DroneServiceImpl.class);
        DispatchController yourController = new DispatchController(droneService);

        String serialNumber = "ABC123";
        MedicationRequest medication = new MedicationRequest();
        medication.setName("MedicineA");
        medication.setImageUrl("beautiful_image");
        medication.setCode("YOUAREAWESOME");
        medication.setWeight(30.0);

        List<MedicationRequest> medications = new ArrayList<>();
        medications.add(medication);
        DroneResponse expectedResponse = new DroneResponse(
                serialNumber,DroneModel.HEAVYWEIGHT,400.0,
                55,"Medication Loaded: ",DroneState.LOADING);

        when(droneService.loadDrone(serialNumber, medications)).thenReturn(expectedResponse);

        ResponseEntity<DroneResponse> responseEntity = yourController.loadDroneWithMedication(serialNumber, medications);

        verify(droneService, times(1)).loadDrone(serialNumber, medications);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    public void loadDroneWithMedication_EmptyMedicationList_ThrowsEmptyMedicationListException() {
        DroneServiceImpl droneService = mock(DroneServiceImpl.class);
        DispatchController yourController = new DispatchController(droneService);

        String serialNumber = "ABC123";
        List<MedicationRequest> emptyMedications = Collections.emptyList();

        when(droneService.loadDrone(anyString(), anyList())).thenThrow(new EmptyMedicationListException("NO MEDICATION PROVIDED FOR LOADING."));

        assertThrows(EmptyMedicationListException.class, () -> {
            yourController.loadDroneWithMedication(serialNumber, emptyMedications);
        });

        verify(droneService, times(1)).loadDrone(anyString(), anyList());
    }

    @Test
    public void loadDroneWithMedication_LowBatteryLevel_ThrowsLowBatteryException() {
        DroneServiceImpl droneService = mock(DroneServiceImpl.class);
        DispatchController dispatchController = new DispatchController(droneService);

        String serialNumber = "ABC123";

        MedicationRequest medication = new MedicationRequest();
        medication.setName("MedicineA");
        medication.setImageUrl("beautiful_image");
        medication.setCode("YOUAREAWESOME");
        medication.setWeight(30.0);
        List<MedicationRequest> medications = Collections.singletonList(medication);

        when(droneService.loadDrone(anyString(), anyList())).thenThrow(new LowBatteryException("BATTERY LEVEL MUST BE ABOVE 25% TO LOAD MEDICATION!"));

        assertThrows(LowBatteryException.class, () -> {
            dispatchController.loadDroneWithMedication(serialNumber, medications);
        });

        verify(droneService, times(1)).loadDrone(anyString(), anyList());
    }

    @Test
    public void loadDroneWithMedication_ExceedWeightLimit_ThrowsExcessWeightException() {
        DroneServiceImpl droneService = mock(DroneServiceImpl.class);
        DispatchController dispatchController = new DispatchController(droneService);

        String serialNumber = "ABC123";

        MedicationRequest medication = new MedicationRequest();
        medication.setName("MedicineA");
        medication.setImageUrl("beautiful_image");
        medication.setCode("YOUAREAWESOME");
        medication.setWeight(300.0);
        List<MedicationRequest> medications = Collections.singletonList(medication);

        when(droneService.loadDrone(anyString(), anyList())).thenThrow(new ExcessWeightException("TOTAL WEIGHT OF MEDICATION MUST NOT EXCEED DRONE WEIGHT LIMIT!"));

        assertThrows(ExcessWeightException.class, () -> {
            dispatchController.loadDroneWithMedication(serialNumber, medications);
        });
        verify(droneService, times(1)).loadDrone(anyString(), anyList());
    }

    @Test
    public void loadCheckLoadedMedication_ValidInput_ReturnListOfMedicationResponse(){
        DroneServiceImpl droneService = mock(DroneServiceImpl.class);
        DispatchController dispatchController = new DispatchController(droneService);

        String serialNumber = "ABC123";

        MedicationResponse response = new MedicationResponse();
        response.setName("MedicineA");
        response.setImageUrl("beautiful_image");
        response.setCode("YOUAREAWESOME");
        response.setWeight(300.0);

        List<MedicationResponse> responseList = new ArrayList<>();
        responseList.add(response);

        when(droneService.checkLoadedMedications(serialNumber)).thenReturn(responseList);

        ResponseEntity<List<MedicationResponse>> responseEntity =
                dispatchController.checkLoadedMedication(serialNumber);

        verify(droneService,times(1)).checkLoadedMedications(serialNumber);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertEquals(responseList,responseEntity.getBody());

    }

    @Test
    public void checkAvailableDronesForLoading_ValidInput_ReturnListOfDrones(){
        DroneService droneService = mock(DroneService.class);
        DispatchController dispatchController = new DispatchController(droneService);

        DroneResponse droneResponse = new DroneResponse();
        droneResponse.setSerialNumber("newserialNumbter");
        droneResponse.setBatteryCapacity(60);
        droneResponse.setLoadedMedication("LoadedMedication");
        droneResponse.setModel(DroneModel.CRUISERWEIGHT);
        droneResponse.setWeightLimit(450.0);
        droneResponse.setState(DroneState.IDLE);


        List<DroneResponse> expectedResponse = Collections.singletonList(droneResponse);


        when(droneService.checkAvailableDronesForLoading()).thenReturn(expectedResponse);

        ResponseEntity<List<DroneResponse>> response = dispatchController.checkAvailableDronesForLoading();

        verify(droneService,times(1)).checkAvailableDronesForLoading();

        assertEquals(expectedResponse,response.getBody());
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void checkDroneBatteryLevel_validInput_returnBatteryPercentage(){

        DroneService droneService = mock(DroneService.class);
        DispatchController dispatchController = new DispatchController(droneService);

        String serialNumber = "SUCH_A_WONDERFUL_DAY";
        DroneResponse droneResponse = new DroneResponse();
        droneResponse.setBatteryCapacity(75);

        when(droneService.checkDroneBatteryLevel(serialNumber)).thenReturn(droneResponse.getBatteryCapacity());

        ResponseEntity<Integer> batteryLevel = dispatchController.checkDroneBatteryLevel(serialNumber);

        verify(droneService,times(1)).checkDroneBatteryLevel(serialNumber);

        assertEquals(batteryLevel.getBody(),droneResponse.getBatteryCapacity());
        assertEquals(HttpStatus.OK,batteryLevel.getStatusCode());
    }

    @Test
    public void checkDroneBatteryLevel_inValidInput_returnException(){
        DroneService droneService =mock(DroneService.class);
        DispatchController dispatchController = new DispatchController(droneService);

        String serialNumber = "invalidSerialNumber";

        when(droneService.checkDroneBatteryLevel(serialNumber))
                .thenThrow(new WrongSerialNumberException("Serial number does not exist"));

        WrongSerialNumberException exception = assertThrows(WrongSerialNumberException.class, () ->
                dispatchController.checkDroneBatteryLevel(serialNumber));
        assertEquals("Serial number does not exist",exception.getMessage());
    }

}
