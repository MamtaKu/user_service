package com.productservice.user_service;

import com.userservice.user_service.UserServiceApplication;
import com.userservice.user_service.dtos.SignUpRequestDto;
import com.userservice.user_service.dtos.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSingUp {

    @Autowired
    TestRestTemplate restTemplate;


    @Test
    void testSingUp() {
        SignUpRequestDto singUpRequestDto = new SignUpRequestDto();
        singUpRequestDto.setEmail("hello@world.com");
        singUpRequestDto.setPassword("Imapassword");
        ResponseEntity<UserDto> responseEntity = restTemplate.postForEntity("/auth/signup",singUpRequestDto, UserDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
