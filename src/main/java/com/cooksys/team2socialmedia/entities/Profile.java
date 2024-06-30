package com.cooksys.team2socialmedia.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class Profile {

    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String email;

    private String phone;



}
