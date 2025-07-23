package ru.acton.ivantkachuk.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.acton.ivantkachuk.userservice.dto.UserRequestDto;
import ru.acton.ivantkachuk.userservice.dto.UserResponseDto;
import ru.acton.ivantkachuk.userservice.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserRequestDto testUserRequestDto;
    private UserResponseDto testUserResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();


        testUserRequestDto = UserRequestDto.builder()
                .email("test@example.com")
                .name("Test User")
                .age(25)
                .build();

        testUserResponseDto = UserResponseDto.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .age(25)
                .build();
    }

    @Test
    void deleteUserById() throws Exception {
        //given
        doNothing().when(userService).deleteUserById(1L);

        //when
        mockMvc.perform(delete("/user/{userId}",1L))
                .andExpect(status().isNoContent());

        //then
        verify(userService).deleteUserById(anyLong());

    }

    @Test
    void getUserById() throws Exception {
        //given
        doReturn(testUserResponseDto).when(userService).getUserById(anyLong());

        //when
        mockMvc.perform(get("/user/{userId}",1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.age").value("25"));
    }

    @Test
    void createUser() throws Exception {
        //given
        doReturn(testUserResponseDto).when(userService).create(any(UserRequestDto.class));
        //when
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test User\", " +
                                "\"id\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(userService).create(any(UserRequestDto.class));
    }

    @Test
    void getAllUsers() throws Exception {
        //given
        UserResponseDto user2 = UserResponseDto.builder()
                .id(2L)
                .email("user2@example.com")
                .name("User 2")
                .age(45)
                .build();

        List<UserResponseDto> users = List.of(testUserResponseDto, user2);
        doReturn(users).when(userService).getAllUsers();

        // when
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));

        verify(userService).getAllUsers();
    }

    @Test
    void getUserByEmail() throws Exception {
        //given
        String email = "test@example.com";
        doReturn(testUserResponseDto).when(userService).getUserByEmail(email);

        //when
        mockMvc.perform(get("/user/by-email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.age").value("25"));

        verify(userService).getUserByEmail(email);
    }
}