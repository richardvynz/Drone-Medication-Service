package com.vincentrichard.dronemedication.model.response;

import com.vincentrichard.dronemedication.entity.Drone;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicationResponse {

    private String name;

    private Double weight;

    private String code;

    private String imageUrl;
    @CreationTimestamp
    private LocalDate createdAt;
    @UpdateTimestamp
    private LocalDate updatedAt;
}
