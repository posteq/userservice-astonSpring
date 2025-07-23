package ru.acton.ivantkachuk.userservice.exception.impl;

public class EntityNotFoundWithEmailException extends RuntimeException {
    public EntityNotFoundWithEmailException(String email) {
        super("Entity not found with email :" + email);
    }
}
