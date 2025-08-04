package ru.acton.ivantkachuk.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;


@RestController
@RequestMapping("/users")
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
    public EntityModel<UserResponseDto> getUserById(@PathVariable @NotNull Long userId) {
        return EntityModel.of(userService.getUserById(userId))
                .add(linkTo(methodOn(UserController.class).getUserById(userId)).withSelfRel());
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

    @Operation(summary = "Get user by his email address")
    @GetMapping("/by-email/{email}")
    public EntityModel<UserResponseDto> getUserByEmail(@PathVariable @NotNull String email) {
        UserResponseDto user = userService.getUserByEmail(email);

        return EntityModel.of(user)
                .add(linkTo(methodOn(UserController.class).getUserByEmail(email)).withSelfRel());
//        return ResponseEntity.ok()
//                .body(userService.getUserByEmail(email));
    }
}
