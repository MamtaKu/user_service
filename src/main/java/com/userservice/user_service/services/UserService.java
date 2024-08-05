package com.userservice.user_service.services;

import com.userservice.user_service.dtos.UserDto;
import org.springframework.stereotype.Service;



public interface UserService {
    UserDto getUserDetails(Long id);

}
