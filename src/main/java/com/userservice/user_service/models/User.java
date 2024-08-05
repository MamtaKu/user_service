package com.userservice.user_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class User extends BaseModel {

    @Column(unique = true)
    private String email;
    private String password;
    @ManyToMany
    private Set<Role> roles = new HashSet<>();


}
