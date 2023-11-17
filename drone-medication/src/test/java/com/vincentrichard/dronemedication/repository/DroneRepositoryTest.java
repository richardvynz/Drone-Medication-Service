package com.vincentrichard.dronemedication.repository;

import com.vincentrichard.dronemedication.entity.Drone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
class DroneRepositoryTest {

    @MockBean
    private DroneRepository droneRepository;

    @MockBean
    private MedicationRepository medicationRepository;

    @BeforeEach
    void setUp() {
        // Define any necessary behavior or setup here
    }

    @Test
    void testFindDroneBySerialNumber() {

        String serialNumber = "ABC123";
        Drone drone = new Drone();
        drone.setSerialNumber(serialNumber);
        when(droneRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(drone));

        Optional<Drone> foundDrone = droneRepository.findBySerialNumber(serialNumber);

        assertTrue(foundDrone.isPresent());
        assertEquals(serialNumber, foundDrone.get().getSerialNumber());
    }

    @Test
    void testExistsBySerialNumber() {
        String serialNumber = "DEF456";
        when(droneRepository.existsBySerialNumber(serialNumber)).thenReturn(true);

        boolean exists = droneRepository.existsBySerialNumber(serialNumber);

        assertTrue(exists);
    }
}