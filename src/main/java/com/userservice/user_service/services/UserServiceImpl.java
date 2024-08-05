package com.userservice.user_service.services;

import com.userservice.user_service.dtos.UserDto;
import com.userservice.user_service.models.User;
import com.userservice.user_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserDetails(Long id) {
        Optional<User> byId = userRepository.findById(id);

        if(byId.isEmpty()){
            return null;
        }
        return null;

    }
}
