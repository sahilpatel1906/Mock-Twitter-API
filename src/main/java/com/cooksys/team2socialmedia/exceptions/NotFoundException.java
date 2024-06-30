package com.cooksys.team2socialmedia.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@AllArgsConstructor
@Getter
@Setter
public class NotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -270410025759807936L;
    private String message;
}
