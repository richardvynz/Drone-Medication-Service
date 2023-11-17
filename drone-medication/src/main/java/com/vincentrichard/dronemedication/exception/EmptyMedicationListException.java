package com.vincentrichard.dronemedication.exception;

import lombok.Data;

@Data
public class EmptyMedicationListException extends RuntimeException{
    public EmptyMedicationListException(String message){
        super(message);
    }
}
