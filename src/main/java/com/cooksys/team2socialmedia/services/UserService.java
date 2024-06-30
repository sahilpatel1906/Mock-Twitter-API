package com.cooksys.team2socialmedia.services;


import com.cooksys.team2socialmedia.dtos.CredentialsDto;
import com.cooksys.team2socialmedia.dtos.TweetResponseDto;
import com.cooksys.team2socialmedia.dtos.UserRequestDto;
import com.cooksys.team2socialmedia.dtos.UserResponseDto;
import com.cooksys.team2socialmedia.entities.Credentials;
import com.cooksys.team2socialmedia.entities.Profile;
import com.cooksys.team2socialmedia.entities.Tweet;
import com.cooksys.team2socialmedia.entities.User;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();

    User findByUsername(String username);

    User updateProfile(User user, Profile profile);

    User deleteUser(User user);

    User findByCredentials(CredentialsDto credentials);

    List<User> saveUserMentions(List<String> mentionsFound);

    List<User> findAll();

    User checkForDeletedUserByCredentials(UserRequestDto userRequestDto);

    User createNewUser(UserRequestDto userRequestDto);

    List<User> getActiveFollowers(User user);

    void saveUser(User user);

    User findActiveUserByCredentials(Credentials credentials);

    void removeFollower(User followedUser, User followingUser);

    List<User> getActiveFollowing(User user);

}
