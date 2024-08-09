package com.userservice.user_service.services;

import com.userservice.user_service.dtos.UserDto;



public interface UserService {
    UserDto getUserDetails(Long id);

}
