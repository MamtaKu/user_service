package com.userservice.user_service.controllers;

import com.userservice.user_service.dtos.*;
import com.userservice.user_service.models.SessionStatus;
import com.userservice.user_service.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto){
        return authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto logoutRequestDto){
        return authService.logout(logoutRequestDto.getToken(), logoutRequestDto.getUserId());

    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        UserDto userDto = authService.signUp(signUpRequestDto.getEmail(),signUpRequestDto.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);

    }
    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(ValidateTokenRequestDto validateTokenRequestDto){
        SessionStatus sessionStatus = authService.validate(validateTokenRequestDto.getToken(),validateTokenRequestDto.getUserId());
        return new ResponseEntity<>(sessionStatus,HttpStatus.OK);

    }
}
