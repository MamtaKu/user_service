package com.userservice.user_service.services;

import com.userservice.user_service.dtos.UserDto;
import com.userservice.user_service.models.SessionStatus;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<UserDto> login(String email, CharSequence password);
    ResponseEntity<Void> logout(String token, Long userId);
    UserDto signUp(String email, String password);
    SessionStatus validate(String token, Long userId);
}
