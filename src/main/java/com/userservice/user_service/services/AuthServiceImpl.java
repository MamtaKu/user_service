package com.userservice.user_service.services;

import com.userservice.user_service.dtos.UserDto;
import com.userservice.user_service.models.Role;
import com.userservice.user_service.models.Session;
import com.userservice.user_service.models.SessionStatus;
import com.userservice.user_service.models.User;
import com.userservice.user_service.repositories.SessionRepository;
import com.userservice.user_service.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public ResponseEntity<UserDto> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            return null;
        }
        User user = userOptional.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            String dbPassword = user.getPassword();
            System.out.println(dbPassword);
            throw new RuntimeException("Wrong password entered");
        }



        //Genertaing token
        //String token = RandomStringUtils.randomAlphanumeric(30);

        // Create a test key suitable for the desired HMAC-SHA algorithm:
        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256
        SecretKey key = alg.key().build();

//                String message = "{\n" +
//                "  \"email\": \"mamta@gmail.com\",\n" +
//                "  \"roles\": [\n" +
//                "    \"student\",\n" +
//                "    \"ta\"\n" +
//                "  ],\n" +
//                "  \"expiry\": \"31stJan2024\"\n" +
//                "}";

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("email",user.getEmail());
        jsonMap.put("roles", List.of(user.getRoles()));
        jsonMap.put("createdAt",new Date());
        jsonMap.put("expiryAt", DateUtils.addDays(new Date(), 30));

       // byte[] content = message.getBytes(StandardCharsets.UTF_8);

        // Create the compact JWS:
        //String jws = Jwts.builder().content(content, "text/plain").signWith(key, alg).compact();

        String jws = Jwts.builder()
                .claims(jsonMap)
                .signWith(key, alg)
                .compact();

        // Parse the compact JWS:
        //content = Jwts.parser().verifyWith(key).build().parseSignedContent(jws).getPayload();

        //assert message.equals(new String(content, StandardCharsets.UTF_8));





        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(jws);
        session.setUser(user);
        sessionRepository.save(session);

        UserDto  userDto = new UserDto();
        userDto.setEmail(email);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:"+ jws);

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
        Session session = sessionOptional.get();

        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
            return SessionStatus.ENDED;
        }

        Date currentTime = new Date();
        if(session.getExpiringAt().before(currentTime)){
            return SessionStatus.ENDED;
        }

        //jwt Decoding
        Jws<Claims> claimsJws =  Jwts.parser().build().parseSignedClaims(token);
        String email = (String) claimsJws.getPayload().get("email");
        List<Role>  roles= (List<Role>) claimsJws.getPayload().get("roles");
        Date createdAt = (Date) claimsJws.getPayload().get("createdAt");


        return SessionStatus.ACTIVE;


    }
}
