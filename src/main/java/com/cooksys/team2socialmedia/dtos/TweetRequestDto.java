package com.cooksys.team2socialmedia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TweetRequestDto {

    private String content;
    private CredentialsDto credentials;

}
