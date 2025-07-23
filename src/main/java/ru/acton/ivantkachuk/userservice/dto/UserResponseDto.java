package ru.acton.ivantkachuk.userservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@Builder
@ToString
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private Date createdAt;
}
