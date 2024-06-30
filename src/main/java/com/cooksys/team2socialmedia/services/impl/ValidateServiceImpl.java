package com.cooksys.team2socialmedia.services.impl;

import com.cooksys.team2socialmedia.dtos.UserRequestDto;
import com.cooksys.team2socialmedia.repositories.HashtagRepository;
import com.cooksys.team2socialmedia.services.UserService;
import org.springframework.stereotype.Service;
import com.cooksys.team2socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final HashtagRepository hashtagRepository;
    private final UserService userService;


    @Override
    public Boolean checkLabelExists(String label) {
        return hashtagRepository.findByLabelIgnoreCase(label) != null;
    }

    @Override
    public Boolean checkUsernameExists(String username) {
        return userService.findAll()
                .stream()
                .anyMatch(user -> (user.getCredentials().getUsername()).equals(username));
    }

    @Override
    public Boolean checkUsernameAvailable(String username) {
        return !checkUsernameExists(username);
    }

    @Override
    public Boolean checkForRequiredFields(UserRequestDto userRequestDto) {
        return (userRequestDto.getProfile() != null) && (userRequestDto.getProfile().getEmail() != null) && (userRequestDto.getCredentials() != null) &&
                (userRequestDto.getCredentials().getUsername() != null) && (userRequestDto.getCredentials().getPassword() != null) && !(userRequestDto.getProfile().getEmail()).isEmpty() && !(userRequestDto.getCredentials().getUsername()).isEmpty() &&
                !(userRequestDto.getCredentials().getPassword()).isEmpty();
    }
}
