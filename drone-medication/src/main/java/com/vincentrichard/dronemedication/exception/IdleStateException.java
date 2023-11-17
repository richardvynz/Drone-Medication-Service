package com.vincentrichard.dronemedication.exception;

import lombok.Data;

@Data
public class IdleStateException extends RuntimeException{
    public IdleStateException(String message){
        super(message);
    }
}
