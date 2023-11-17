package com.vincentrichard.dronemedication.exception;



import com.vincentrichard.dronemedication.model.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(WrongSerialNumberException.class)
    public ResponseEntity <ErrorResponse>handleWrongSerialNumberException(WrongSerialNumberException exception){
      return new ResponseEntity<>(ErrorResponse.builder()
              .errorMessage(exception.getMessage())
              .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LowBatteryException.class)
    public ResponseEntity <ErrorResponse>handleLowBatteryException(LowBatteryException exception){
        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(exception.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IdleStateException.class)
    public ResponseEntity <ErrorResponse>handleIdleStateException(IdleStateException exception){
        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(exception.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().iterator().next().getMessage();
        String customErrorMessage = "Error Message: " + errorMessage;

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customErrorMessage);
    }

    @ExceptionHandler(ExcessWeightException.class)
    public ResponseEntity <ErrorResponse>handleExcessWeightException(ExcessWeightException exception){
        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(exception.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SerialNumberExistException.class)
    public ResponseEntity <ErrorResponse>handleSerialNumberExistException(SerialNumberExistException exception) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(exception.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }
        @ExceptionHandler(SerialNumberLengthException.class)
    public ResponseEntity <ErrorResponse>handleSerialNumberLengthException(SerialNumberLengthException exception){
        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(exception.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EmptyMedicationListException.class)
    public ResponseEntity <ErrorResponse>handleEmptyMedicationListException(EmptyMedicationListException exception){
        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(exception.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(IncompleteMedicationDetailsException.class)
    public ResponseEntity <ErrorResponse>handleIncompleteMedicationDetailsException(IncompleteMedicationDetailsException exception){
        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage(exception.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }
}