package com.vincentrichard.dronemedication.exception;

import lombok.Data;

@Data
public class IncompleteMedicationDetailsException extends RuntimeException {
    public IncompleteMedicationDetailsException(String message){
        super(message);
    }
}
