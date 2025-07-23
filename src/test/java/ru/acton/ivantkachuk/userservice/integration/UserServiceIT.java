package ru.acton.ivantkachuk.userservice.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.acton.ivantkachuk.userservice.dto.UserRequestDto;
import ru.acton.ivantkachuk.userservice.dto.UserResponseDto;
import ru.acton.ivantkachuk.userservice.entity.User;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityNotFoundException;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityNotFoundWithEmailException;
import ru.acton.ivantkachuk.userservice.repository.UserRepository;
import ru.acton.ivantkachuk.userservice.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Sql({
        "classpass:sql/data.sql"
})
public class UserServiceIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        //given
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("test@example.com");
        userRequestDto.setName("Test User");

        //when
        UserResponseDto result = userService.create(userRequestDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");

        Optional<User> savedUser = userRepository.findById(result.getId());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() {
        //given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        User savedUser = userRepository.save(user);

        //when
        UserResponseDto result = userService.getUserById(savedUser.getId());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user not found by ID")
    void shouldThrowEntityNotFoundExceptionWhenUserNotFoundById() {
        //given
        Long nonExistentId = 999L;

        //when
        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(nonExistentId));
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        //given
        User user = new User();
        user.setEmail("old@example.com");
        user.setName("Old Name");
        User savedUser = userRepository.save(user);

        UserRequestDto updateDto = new UserRequestDto();
        updateDto.setEmail("new@example.com");
        updateDto.setName("New Name");

        //when
        UserResponseDto result = userService.updateUser(savedUser.getId(), updateDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getEmail()).isEqualTo("new@example.com");

        Optional<User> updatedUser = userRepository.findById(savedUser.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent user")
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentUser() {
        //given
        Long nonExistentId = 999L;
        UserRequestDto updateDto = new UserRequestDto();
        updateDto.setEmail("new@example.com");

        //when
        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(nonExistentId, updateDto));
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        //given
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setName("User One");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setName("User Two");

        userRepository.saveAll(List.of(user1, user2));

        //when
        List<UserResponseDto> result = userService.getAllUsers();

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(UserResponseDto::getEmail)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com");
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsersExist() {
        //when
        List<UserResponseDto> result = userService.getAllUsers();

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void shouldGetUserByEmailSuccessfully() {
        //given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userRepository.save(user);

        //when
        UserResponseDto result = userService.getUserByEmail("test@example.com");

        //then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundWithEmailException when user not found by email")
    void shouldThrowEntityNotFoundWithEmailExceptionWhenUserNotFoundByEmail() {
        //given
        String nonExistentEmail = "nonexistent@example.com";

        //when
        assertThrows(EntityNotFoundWithEmailException.class,
                () -> userService.getUserByEmail(nonExistentEmail));
    }

    @Test
    @DisplayName("Should delete user by ID successfully")
    void shouldDeleteUserByIdSuccessfully() {
        //given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        User savedUser = userRepository.save(user);

        //when
        userService.deleteUserById(savedUser.getId());

        //then
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("Should handle delete of non-existent user gracefully")
    void shouldHandleDeleteOfNonExistentUserGracefully() {
        //given
        Long nonExistentId = 999L;
        
        assertDoesNotThrow(() -> userService.deleteUserById(nonExistentId));
    }

    @Test
    @DisplayName("Should handle duplicate email creation")
    void shouldHandleDuplicateEmailCreation() {
        //given
        User existingUser = new User();
        existingUser.setEmail("duplicate@example.com");
        existingUser.setName("Existing User");
        userRepository.save(existingUser);

        UserRequestDto duplicateDto = new UserRequestDto();
        duplicateDto.setEmail("duplicate@example.com");
        duplicateDto.setName("Duplicate User");

        //when
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.create(duplicateDto));
    }
    
}
