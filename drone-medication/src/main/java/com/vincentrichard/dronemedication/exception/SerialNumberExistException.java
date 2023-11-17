package com.vincentrichard.dronemedication.exception;

public class SerialNumberExistException extends RuntimeException{
    public SerialNumberExistException(String message){
        super(message);
    }
}
