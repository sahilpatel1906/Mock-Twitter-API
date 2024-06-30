package com.cooksys.team2socialmedia.services.impl;


import com.cooksys.team2socialmedia.dtos.CredentialsDto;
import com.cooksys.team2socialmedia.dtos.UserRequestDto;
import com.cooksys.team2socialmedia.dtos.UserResponseDto;
import com.cooksys.team2socialmedia.entities.Credentials;
import com.cooksys.team2socialmedia.entities.Profile;
import com.cooksys.team2socialmedia.entities.Tweet;
import com.cooksys.team2socialmedia.entities.User;
import com.cooksys.team2socialmedia.mappers.UserMapper;
import com.cooksys.team2socialmedia.repositories.TweetRepository;
import com.cooksys.team2socialmedia.repositories.UserRepository;
import org.springframework.stereotype.Service;
import com.cooksys.team2socialmedia.services.UserService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findByDeleted(false);
        return userMapper.entitiesToDtos(users);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByCredentialsUsernameAndDeleted(username, false);
    }

    @Override
    public User updateProfile(User user, Profile profile) {
        Profile profileToSave = user.getProfile();
        if (profile.getFirstName() != null) {
            profileToSave.setFirstName(profile.getFirstName());
        }
        if (profile.getLastName() != null) {
            profileToSave.setLastName(profile.getLastName());
        }
        if (profile.getEmail() != null) {
            profileToSave.setEmail(profile.getEmail());
        }
        if (profile.getPhone() != null) {
            profileToSave.setPhone(profile.getPhone());
        }
        user.setProfile(profileToSave);
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User deleteUser(User user) {
        user.setDeleted(true);
        List<Tweet> tweets = user.getTweets();
        for (Tweet tweet : tweets) {
            tweet.setDeleted(true);
            tweetRepository.saveAndFlush(tweet);
        }
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User findByCredentials(CredentialsDto credentialsDto) {
        return userRepository.findByCredentialsUsernameAndCredentialsPassword(credentialsDto.getUsername(), credentialsDto.getPassword());
    }

    @Override
    public List<User> saveUserMentions(List<String> mentionsFound) {
        List<User> mentionedUsers = new ArrayList<>();
        for (String s : mentionsFound) {
            User existingUser = findByUsername(s.substring(1));
            mentionedUsers.add(existingUser);
        }
        return mentionedUsers;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User checkForDeletedUserByCredentials(UserRequestDto userRequestDto) {
        User user = userMapper.DtoToEntity(userRequestDto);
        User deletedUser = userRepository.findByCredentialsAndDeleted(user.getCredentials(), true);
        if (deletedUser != null) {
            deletedUser.setDeleted(false);
            return userRepository.saveAndFlush(deletedUser);
        }
        return null;
    }

    @Override
    public User createNewUser(UserRequestDto userRequestDto) {
        return userRepository.saveAndFlush(userMapper.DtoToEntity(userRequestDto));
    }

    @Override
    public List<User> getActiveFollowers(User user) {
        return user.getFollowers().stream().filter(follower -> !follower.isDeleted()).collect(Collectors.toList());
    }

    public void saveUser(User user) {
        userRepository.saveAndFlush(user);
    }


    public User findActiveUserByCredentials(Credentials credentials) {
        return userRepository.findByCredentialsAndDeleted(credentials, false);
    }

    @Override
    public void removeFollower(User followedUser, User followingUser) {
        if (followedUser.getFollowers().remove(followingUser)) {
            userRepository.saveAndFlush(followedUser);
        }
        if (followingUser.getFollowing().remove(followedUser)) {
            userRepository.saveAndFlush(followingUser);
        }
    }

    @Override
    public List<User> getActiveFollowing(User user) {
        return user.getFollowing()
                .stream()
                .filter(users -> !users.isDeleted())
                .collect(Collectors.toList());
    }


}
