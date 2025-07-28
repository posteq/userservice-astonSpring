package ru.acton.ivantkachuk.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.acton.ivantkachuk.userservice.entity.UserEvent;
import ru.acton.ivantkachuk.userservice.dto.UserRequestDto;
import ru.acton.ivantkachuk.userservice.dto.UserResponseDto;
import ru.acton.ivantkachuk.userservice.entity.Operation;
import ru.acton.ivantkachuk.userservice.entity.User;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityFoundWithEmailException;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityNotFoundException;
import ru.acton.ivantkachuk.userservice.exception.impl.EntityNotFoundWithEmailException;
import ru.acton.ivantkachuk.userservice.mapper.UserMapper;
import ru.acton.ivantkachuk.userservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaMessageService kafkaService;

    public UserResponseDto create(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new EntityFoundWithEmailException(userRequestDto.getEmail());
        }
        User savedUser = userRepository.save(userMapper.toEntity(userRequestDto));
        UserEvent event = new UserEvent(savedUser.getEmail(), Operation.CREATE);
        kafkaService.sendMessage(event);
        return userMapper.toDto(savedUser);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        return userMapper.toDto(user);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        user.setEmail(userRequestDto.getEmail());
        User save = userRepository.save(user);
        return userMapper.toDto(save);
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDto(users);
    }

    public UserResponseDto getUserByEmail(String email) {
        return userMapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundWithEmailException(email)));
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        userRepository.deleteById(id);
        if (user != null) {
            UserEvent event = new UserEvent(user.getEmail(), Operation.DELETE);
            kafkaService.sendMessage(event);
        }
    }
}
