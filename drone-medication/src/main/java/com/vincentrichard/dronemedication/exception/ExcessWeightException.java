package com.vincentrichard.dronemedication.exception;

import lombok.Data;

@Data
public class ExcessWeightException extends RuntimeException{
    public ExcessWeightException(String message){
        super(message);
    }
}
