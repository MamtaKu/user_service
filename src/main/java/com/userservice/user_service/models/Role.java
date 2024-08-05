package com.userservice.user_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
public class Role extends BaseModel {
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
    private String role;
}
