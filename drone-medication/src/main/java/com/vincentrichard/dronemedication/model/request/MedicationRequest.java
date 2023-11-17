package com.vincentrichard.dronemedication.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicationRequest {
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$",
            message = "Name should contain only letters, numbers, hyphens, and underscores.")
    private String name;
    @Min(value = 0,message = "weigh should not be less than zero")
    private double weight;

    @Pattern(regexp = "^[A-Z0-9_]+$",
            message = "Code should contain only upper case letters, underscores, and numbers.")
    private String code;
    @NotBlank(message = "ImageUrl is required")
    private String imageUrl;
}
