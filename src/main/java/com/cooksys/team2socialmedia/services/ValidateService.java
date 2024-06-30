package com.cooksys.team2socialmedia.services;

import com.cooksys.team2socialmedia.dtos.UserRequestDto;

public interface ValidateService {
    Boolean checkLabelExists(String label);

    Boolean checkUsernameExists(String username);

    Boolean checkUsernameAvailable(String username);

    Boolean checkForRequiredFields(UserRequestDto userRequestDto);
}
