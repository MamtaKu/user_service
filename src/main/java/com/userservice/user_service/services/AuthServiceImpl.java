package com.userservice.user_service.services;

import com.userservice.user_service.dtos.UserDto;
import com.userservice.user_service.models.Session;
import com.userservice.user_service.models.SessionStatus;
import com.userservice.user_service.models.User;
import com.userservice.user_service.repositories.SessionRepository;
import com.userservice.user_service.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import java.util.HashMap;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

//    public AuthServiceImpl(UserRepository userRepository,
//                           SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
//        this.userRepository = userRepository;
//        this.sessionRepository = sessionRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }

    @Override
    public ResponseEntity<UserDto> login(String email, CharSequence password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            return null;
        }
        User user = userOptional.get();
        String  value = bCryptPasswordEncoder.encode(password);
        boolean matches = bCryptPasswordEncoder.matches(password, value);

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            String dbPassword = user.getPassword();
            System.out.println(dbPassword);
            throw new RuntimeException("Wrong password entered");
        }

        String token = RandomStringUtils.randomAlphanumeric(30);



        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        UserDto  userDto = new UserDto();
        userDto.setEmail(email);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:"+ token);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);

        return response;


    }

    @Override
    public ResponseEntity<Void> logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if(sessionOptional.isEmpty()){
            return null;
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
        return ResponseEntity.ok().build();


    }

    @Override
    public UserDto signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User saved = userRepository.save(user);
        return UserDto.from(user);
    }

    @Override
    public SessionStatus validate(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if(sessionOptional.isEmpty()){
            return null;
        }

        return SessionStatus.ACTIVE;


    }
}
