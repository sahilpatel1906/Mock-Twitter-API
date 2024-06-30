package com.cooksys.team2socialmedia.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@AllArgsConstructor
@Getter
@Setter
public class BadRequestException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -2059736176939685899L;
    private String message;

}
