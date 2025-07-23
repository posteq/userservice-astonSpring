package ru.acton.ivantkachuk.userservice.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.acton.ivantkachuk.userservice.dto.UserRequestDto;
import ru.acton.ivantkachuk.userservice.dto.UserResponseDto;
import ru.acton.ivantkachuk.userservice.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable @NotNull Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable @NotNull Long userId) {
        return ResponseEntity.ok()
                .body(userService.getUserById(userId));
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto){
        return ResponseEntity.ok()
                .body(userService.create(userRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok()
                        .body(userService.getAllUsers());
    }
    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable @NotNull String email) {
        return ResponseEntity.ok()
                .body(userService.getUserByEmail(email));
    }
}
