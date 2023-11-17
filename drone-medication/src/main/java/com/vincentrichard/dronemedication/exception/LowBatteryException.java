package com.vincentrichard.dronemedication.exception;

import lombok.Data;

@Data
public class LowBatteryException extends RuntimeException{
    public LowBatteryException(String message){
        super(message);
    }

}
