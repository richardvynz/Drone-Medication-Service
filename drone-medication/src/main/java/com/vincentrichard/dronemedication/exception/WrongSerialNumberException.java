package com.vincentrichard.dronemedication.exception;

import lombok.Data;

@Data
public class WrongSerialNumberException extends RuntimeException{
    public WrongSerialNumberException(String message){
        super(message);
    }
}
