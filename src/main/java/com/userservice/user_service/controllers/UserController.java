package com.userservice.user_service.controllers;

import com.userservice.user_service.dtos.UserDto;
import com.userservice.user_service.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto getUserDetails(@PathVariable Long id){
        UserDto userDto = userService.getUserDetails(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK).getBody();
    }
}
