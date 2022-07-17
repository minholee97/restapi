package com.restapi.service;

import com.restapi.advice.exception.UserNotFoundException;
import com.restapi.dto.UserRequestDto;
import com.restapi.dto.UserResponseDto;
import com.restapi.entity.User;
import com.restapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    @Transactional
    public void save(UserRequestDto userDto) {
        userRepository.save(userDto.toEntity());
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id){
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return new UserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return new UserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAllUser() {
        return userRepository.findAll().stream().map(UserResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, UserRequestDto userRequestDto) {
        User modifiedUser = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        modifiedUser.setEmail(userRequestDto.getEmail());
        modifiedUser.setName(userRequestDto.getName());
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

}
