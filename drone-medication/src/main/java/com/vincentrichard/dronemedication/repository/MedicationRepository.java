package com.vincentrichard.dronemedication.repository;

import com.vincentrichard.dronemedication.entity.Drone;
import com.vincentrichard.dronemedication.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication,Long> {
    List<Medication> findByDrone(Drone drone);
}
