package com.userservice.user_service.security;

import com.userservice.user_service.models.User;
import com.userservice.user_service.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomSpringUserDetails implements UserDetailsService {
    public UserRepository userRepository;

    public CustomSpringUserDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Fetch the user with the given username from DB
        Optional<User> byEmail = userRepository.findByEmail(username);

        if(byEmail.isEmpty()){
            throw new UsernameNotFoundException("User with given username does not exist");
        }

        User user = byEmail.get();



        return new CustomUserDetails(user);
    }
}
