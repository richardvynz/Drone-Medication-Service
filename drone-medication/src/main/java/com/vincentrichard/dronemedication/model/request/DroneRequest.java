package com.vincentrichard.dronemedication.model.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DroneRequest {
    @Min(value = 100,message = "minimum weight should be 100gr and above")
    @Max(value = 500, message = "the maximum weight can not be more than 500grt")
    private Double weightLimit;
    @Min(value = 0, message = "battery percentage can not be less than zero")
    @Max(value = 100, message = "battery percentage cannot be more than 100")
    private Integer batteryCapacity;
}
