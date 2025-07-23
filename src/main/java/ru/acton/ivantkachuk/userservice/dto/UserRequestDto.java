package ru.acton.ivantkachuk.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotNull
    @Positive
    private Integer age;
}
