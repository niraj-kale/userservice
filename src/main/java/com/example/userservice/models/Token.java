package com.example.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Token extends BaseModel {
    private String value;
    @ManyToOne
    private User user;
    private Long expiryAt;
}
