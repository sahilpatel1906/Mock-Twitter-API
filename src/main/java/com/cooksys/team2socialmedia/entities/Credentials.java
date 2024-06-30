package com.cooksys.team2socialmedia.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class Credentials {

	@Column(nullable = false, unique = true)
    private String username;
	
	@Column(nullable = false)
    private String password;
}
