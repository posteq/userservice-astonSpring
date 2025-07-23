package ru.acton.ivantkachuk.userservice.exception.impl;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Long id) {
        super("Entity not found with id :" + id);
    }

}
