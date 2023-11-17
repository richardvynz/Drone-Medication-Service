package com.vincentrichard.dronemedication.exception;

import lombok.Data;

@Data
public class SerialNumberLengthException extends RuntimeException{
    public SerialNumberLengthException(String message){
        super(message);
    }
}
