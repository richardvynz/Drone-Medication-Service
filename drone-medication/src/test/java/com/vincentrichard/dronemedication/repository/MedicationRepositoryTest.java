package com.vincentrichard.dronemedication.repository;

import com.vincentrichard.dronemedication.entity.Drone;
import com.vincentrichard.dronemedication.entity.Medication;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class MedicationRepositoryTest {

    @Autowired
    private MedicationRepository medicationRepository;
    @Autowired
    private DroneRepository droneRepository;


    @Test
    @Rollback
    void testFindByDrone() {
        Drone drone = new Drone();
        drone.setSerialNumber("DRONE_SERIAL_NUMBER");

        droneRepository.save(drone);

        Medication medication = new Medication();
        medication.setName("Medication Name");
        medication.setDrone(drone);

        medicationRepository.save(medication);

        List<Medication> medicationsFound = medicationRepository.findByDrone(drone);

        assertNotNull(medicationsFound);
    }
}