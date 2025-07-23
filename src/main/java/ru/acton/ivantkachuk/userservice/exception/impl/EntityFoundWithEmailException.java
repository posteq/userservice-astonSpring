package ru.acton.ivantkachuk.userservice.exception.impl;

public class EntityFoundWithEmailException extends RuntimeException {
    public EntityFoundWithEmailException(String email) {
        super("Email already exists: " + email);
    }
}
