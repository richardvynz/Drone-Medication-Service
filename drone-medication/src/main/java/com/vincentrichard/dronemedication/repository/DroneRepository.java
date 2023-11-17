package com.vincentrichard.dronemedication.repository;

import com.vincentrichard.dronemedication.entity.Drone;
import com.vincentrichard.dronemedication.model.enums.DroneState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroneRepository extends JpaRepository<Drone,Long> {
    Optional<Drone> findBySerialNumber(String serialNumber);
    Boolean existsBySerialNumber(String serialNumber);
}
