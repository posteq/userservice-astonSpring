package ru.acton.ivantkachuk.userservice.integration;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.acton.ivantkachuk.userservice.dto.UserRequestDto;
import ru.acton.ivantkachuk.userservice.dto.UserResponseDto;
import ru.acton.ivantkachuk.userservice.entity.User;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityFoundWithEmailException;
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
@Transactional
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
    }

    @BeforeAll
    static void runContainer() {
        container.start();
    }

    @AfterAll
    static void stopContainer() {
        container.stop();
    }

    @Test
    void shouldCreateUserSuccessfully() {
        //given
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .name("Test User")
                .age(25)
                .email("test@example.com")
                .build();

        //when
        UserResponseDto result = userService.create(userRequestDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getAge()).isEqualTo(25);

        Optional<User> savedUser = userRepository.findById(result.getId());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        //given
        User user = User.builder()
                .name("Test User 2")
                .email("test2@example.com")
                .age(23)
                .build();

        User savedUser = userRepository.save(user);

        //when
        UserResponseDto result = userService.getUserById(savedUser.getId());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getEmail()).isEqualTo("test2@example.com");
        assertThat(result.getName()).isEqualTo("Test User 2");
        assertThat(result.getAge()).isEqualTo(23);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserNotFoundById() {
        //given
        Long nonExistentId = 999L;

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(nonExistentId));
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        //given
        User user = User.builder()
                .name("Old Name")
                .email("old@example.com")
                .age(30)
                .build();

        User savedUser = userRepository.save(user);

        UserRequestDto updateDto = UserRequestDto.builder()
                .email("new@example.com")
                .build();


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
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentUser() {
        //given
        Long nonExistentId = 999L;
        UserRequestDto updateDto = UserRequestDto.builder()
                .name("New Name")
                .email("new@example.com")
                .age(40)
                .build();

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(nonExistentId, updateDto));
    }

    @Test
    void shouldGetAllUsersSuccessfully() {
        //given
        userRepository.deleteAll();

        User user1 = User.builder()
                .name("User One")
                .email("user1@example.com")
                .age(32)
                .build();

        User user2 = User.builder()
                .name("User Two")
                .email("user2@example.com")
                .age(37)
                .build();

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
    void shouldReturnEmptyListWhenNoUsersExist() {
        //given
        userRepository.deleteAll();

        //when
        List<UserResponseDto> result = userService.getAllUsers();

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetUserByEmailSuccessfully() {
        //given
        User user = User.builder()
                .name("Email Test User")
                .email("email-test@example.com")
                .age(27)
                .build();

        userRepository.save(user);

        //when
        UserResponseDto result = userService.getUserByEmail("email-test@example.com");

        //then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("email-test@example.com");
        assertThat(result.getName()).isEqualTo("Email Test User");
        assertThat(result.getAge()).isEqualTo(27);
    }

    @Test
    void shouldThrowEntityNotFoundWithEmailExceptionWhenUserNotFoundByEmail() {
        //given
        String nonExistentEmail = "nonexistent@example.com";

        //when & then
        assertThrows(EntityNotFoundWithEmailException.class,
                () -> userService.getUserByEmail(nonExistentEmail));
    }

    @Test
    void shouldDeleteUserByIdSuccessfully() {
        //given
        User user = User.builder()
                .name("Delete Test User")
                .email("delete-test@example.com")
                .age(30)
                .build();

        User savedUser = userRepository.save(user);

        //when
        userService.deleteUserById(savedUser.getId());

        //then
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void shouldHandleDeleteOfNonExistentUserGracefully() {
        //given
        Long nonExistentId = 999L;

        //when & then
        assertDoesNotThrow(() -> userService.deleteUserById(nonExistentId));
    }

    @Test
    void shouldHandleDuplicateEmailCreation() {
        //given
        User existingUser = User.builder()
                .name("Existing Use")
                .email("duplicate@example.com")
                .age(45)
                .build();

        userRepository.save(existingUser);

        UserRequestDto duplicateDto = UserRequestDto.builder()
                .name("Duplicate User")
                .email("duplicate@example.com")
                .age(55)
                .build();

        //when & then
        assertThrows(EntityFoundWithEmailException.class,
                () -> userService.create(duplicateDto));
    }
}
