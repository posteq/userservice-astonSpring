package ru.acton.ivantkachuk.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.acton.ivantkachuk.userservice.dto.UserRequestDto;
import ru.acton.ivantkachuk.userservice.dto.UserResponseDto;
import ru.acton.ivantkachuk.userservice.entity.User;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityNotFoundException;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityNotFoundWithEmailException;
import ru.acton.ivantkachuk.userservice.mapper.UserMapper;
import ru.acton.ivantkachuk.userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    private UserRequestDto testUserRequestDto;
    private UserResponseDto testUserResponseDto;
    private User testUser;
    private User testUser2;
    private UserResponseDto testUserResponseDto2;


    static Long ID = 1L;
    static String NAME = "test";
    static String EMAIL = "test@test.com";
    static Integer AGE = 25;

    @BeforeEach
    void setUp() {

        testUser = User.builder()
                .id(ID)
                .name(NAME)
                .email(EMAIL)
                .age(AGE)
                .build();

        testUserRequestDto = UserRequestDto.builder()
                .name(NAME)
                .email(EMAIL)
                .age(AGE)
                .build();

        testUserResponseDto = UserResponseDto.builder()
                .id(ID)
                .name(NAME)
                .email(EMAIL)
                .age(AGE)
                .build();

        testUser2 = User.builder()
                .id(2L)
                .name("Alex")
                .email("alex@test.com")
                .age(30)
                .build();

        testUserResponseDto2 = UserResponseDto.builder()
                .id(2L)
                .name("Alex")
                .email("alex@test.com")
                .age(30)
                .build();
    }


    @Test
    void shouldCreateNewUser_WhenValidRequest() {
        //given
        doReturn(testUser).when(userRepository).save(any(User.class));
        doReturn(testUser).when(userMapper).toEntity(any(UserRequestDto.class));
        doReturn(testUserResponseDto).when(userMapper).toDto(any(User.class));

        //when
        UserResponseDto actual = userService.create(testUserRequestDto);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(EMAIL);
        assertThat(actual.getName()).isEqualTo(NAME);

        verify(userRepository, times(1)).save(testUser);

    }

    @Test
    void shouldGetUserById() {
        //given
        doReturn(Optional.of(testUser)).when(userRepository).findById(ID);
        doReturn(testUserResponseDto).when(userMapper).toDto(any(User.class));
        //when
        UserResponseDto actual = userService.getUserById(ID);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(EMAIL);
        assertThat(actual.getName()).isEqualTo(NAME);

        verify(userRepository, times(1)).findById(ID);

    }

    @Test
    void getUserById_shouldThrowWhenUserNotExist(){
        //given
        doReturn(Optional.empty()).when(userRepository).findById(ID);

        //when

        //then
        assertThatThrownBy(()-> userService.getUserById(ID))
                .isInstanceOf(EntityNotFoundException.class);

        verify(userRepository, times(1)).findById(ID);
        verify(userMapper,never()).toDto(any(User.class));
    }

    @Test
    void shouldUpdateUserWhenExist() {
        //given
        UserRequestDto updateRequestDto = UserRequestDto.builder()
                .email("updated@example.com")
                .name("Updated User")
                .age(20)
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .email("updated@example.com")
                .name("Test User")
                .age(20)
                .build();

        UserResponseDto updatedResponseDto = UserResponseDto.builder()
                .id(1L)
                .email("updated@example.com")
                .name("Test User")
                .age(20)
                .build();

        doReturn(Optional.of(testUser)).when(userRepository).findById(any());
        doReturn(updatedResponseDto).when(userMapper).toDto(any(User.class));
        doReturn(updatedUser).when(userRepository).save(any(User.class));
        //when
        UserResponseDto actual = userService.updateUser(ID, updateRequestDto);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getEmail()).isEqualTo("updated@example.com");

        verify(userRepository, times(1)).findById(ID);

    }

    @Test
    void updateUser_shouldThrowWhenUserNotExist() {
        //given
        doReturn(Optional.empty()).when(userRepository).findById(ID);

        //when
        assertThatThrownBy(()->userService.updateUser(ID, testUserRequestDto))
                .isInstanceOf(EntityNotFoundException.class);

        //then
        verify(userRepository, times(1)).findById(ID);
        verify(userRepository,never()).save(any(User.class));

    }

    @Test
    void shouldGetAllUsers() {
        //given
        doReturn(List.of(testUser, testUser2)).when(userRepository).findAll();
        doReturn(List.of(testUserResponseDto, testUserResponseDto2)).when(userMapper).toDto(List.of(testUser, testUser2));

        //when
        List<UserResponseDto> actual = userService.getAllUsers();

        //then
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getId()).isEqualTo(ID);
        assertThat(actual.get(1).getId()).isEqualTo(2L);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldGetUserByEmail() {
        //given
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail(EMAIL);
        doReturn(testUserResponseDto).when(userMapper).toDto(any(User.class));

        //when
        UserResponseDto actual = userService.getUserByEmail(EMAIL);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(EMAIL);

        verify(userRepository, times(1)).findByEmail(EMAIL);

    }

    @Test
    void getUserByEmail_shouldThrowWhenUserNotExist() {
        //given
        doReturn(Optional.empty()).when(userRepository)
                .findByEmail(any(String.class));

        //when
        assertThatThrownBy(()->userService.getUserByEmail(EMAIL))
                .isInstanceOf(EntityNotFoundWithEmailException.class);

        //then
        verify(userRepository, times(1)).findByEmail(EMAIL);

    }

    @Test
    void deleteUserById() {
        //given


        //when
        userService.deleteUserById(ID);

        //then
        verify(userRepository, times(1)).deleteById(ID);

    }
}