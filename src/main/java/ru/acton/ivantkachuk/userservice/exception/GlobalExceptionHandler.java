package ru.acton.ivantkachuk.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityFoundWithEmailException;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityNotFoundException;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityNotFoundWithEmailException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class, EntityNotFoundWithEmailException.class, EntityFoundWithEmailException.class})
    public ResponseEntity<String> handleEntityNotFound(Exception ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
