package com.vincentrichard.dronemedication.model.response;

import com.vincentrichard.dronemedication.model.enums.DroneModel;
import com.vincentrichard.dronemedication.model.enums.DroneState;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneResponse {
    private String serialNumber;

    private DroneModel model;

    private Double weightLimit;

    private Integer batteryCapacity;
    private  String loadedMedication;
    private DroneState state;
    @CreationTimestamp
    private LocalDate createdAt;
    @UpdateTimestamp
    private LocalDate updatedAt;


    public DroneResponse(String serialNumber, DroneModel model, Double weightLimit, Integer batteryCapacity, String loadedMedication, DroneState state) {
        this.serialNumber = serialNumber;
        this.model = model;
        this.weightLimit = weightLimit;
        this.batteryCapacity = batteryCapacity;
        this.loadedMedication = loadedMedication;
        this.state = state;
        this.updatedAt = LocalDate.now();
        this.createdAt = LocalDate.now();
    }
}
