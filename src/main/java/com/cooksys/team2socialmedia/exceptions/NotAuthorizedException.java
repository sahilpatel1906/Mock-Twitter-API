package com.cooksys.team2socialmedia.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@AllArgsConstructor
@Getter
@Setter
public class NotAuthorizedException extends RuntimeException{


    @Serial
    private static final long serialVersionUID = -4111462997834410925L;
    private String message;
}
