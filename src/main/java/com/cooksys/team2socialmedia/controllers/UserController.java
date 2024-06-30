package com.cooksys.team2socialmedia.controllers;

import com.cooksys.team2socialmedia.dtos.CredentialsDto;
import com.cooksys.team2socialmedia.dtos.TweetResponseDto;
import com.cooksys.team2socialmedia.dtos.UserRequestDto;
import com.cooksys.team2socialmedia.dtos.UserResponseDto;
import com.cooksys.team2socialmedia.entities.Credentials;
import com.cooksys.team2socialmedia.entities.Profile;
import com.cooksys.team2socialmedia.entities.Tweet;
import com.cooksys.team2socialmedia.entities.User;
import com.cooksys.team2socialmedia.exceptions.BadRequestException;
import com.cooksys.team2socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.team2socialmedia.exceptions.NotFoundException;
import com.cooksys.team2socialmedia.mappers.CredentialsMapper;
import com.cooksys.team2socialmedia.mappers.ProfileMapper;
import com.cooksys.team2socialmedia.mappers.TweetMapper;
import com.cooksys.team2socialmedia.mappers.UserMapper;
import com.cooksys.team2socialmedia.services.TweetService;
import com.cooksys.team2socialmedia.services.UserService;
import com.cooksys.team2socialmedia.services.ValidateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CredentialsMapper credentialsMapper;
    private final ProfileMapper profileMapper;
    private final UserMapper userMapper;
    private final ValidateService validateService;
    private final TweetService tweetService;
    private final TweetMapper tweetMapper;


    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        if(userRequestDto == null) {
            throw new BadRequestException("All required fields have not been filled out.");
        }
        if (!validateService.checkForRequiredFields(userRequestDto)) {
            throw new BadRequestException("All required fields have not been filled out.");
        }
        User deletedUser = userService.checkForDeletedUserByCredentials(userRequestDto);
        if (deletedUser != null) {
            return userMapper.entityToDto(deletedUser);
        }
        if (!validateService.checkUsernameAvailable(userRequestDto.getCredentials().getUsername())) {
            throw new NotAuthorizedException("This username is not available");
        }
        User newUser = userService.createNewUser(userRequestDto);
        return userMapper.entityToDto(newUser);
    }

    @PostMapping("/@{username}/follow")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void followUser(@PathVariable String username, @RequestBody CredentialsDto credentialsDto) {
        User user = userService.findByCredentials(credentialsDto);
        User userToFollow = userService.findByUsername(username);
        if (user == null) {
            throw new NotAuthorizedException("Credentials Provided are Wrong");
        }
        if (userToFollow != null && userToFollow.isDeleted()) {
            throw new NotFoundException("User to follow does not exist");

        } else if (userToFollow == null) {
            throw new NotFoundException("User to follow not found");
        }
        List<User> usersFollowing = user.getFollowing();
        if (!usersFollowing.contains(userToFollow)) {
            usersFollowing.add(userToFollow);
            user.setFollowing(usersFollowing);
            userService.saveUser(user);
        } else {
            throw new BadRequestException("You are already following this user.");
        }
    }

    @PatchMapping("/@{username}")
    public UserResponseDto updateProfile(@PathVariable String username, @RequestBody UserRequestDto userRequestDto) {
        User userToUpdate = userService.findByUsername(username);
        Credentials credentials = credentialsMapper.DtoToEntity(userRequestDto.getCredentials());
        Profile profile = profileMapper.DtoToEntity(userRequestDto.getProfile());
        if(userToUpdate == null || userToUpdate.isDeleted() || credentials == null || profile == null) {
            throw new BadRequestException("This user does not exist");
        }
        if(!(userToUpdate.getCredentials()).equals(credentials)){
            throw new NotAuthorizedException("Your credentials do not match");
        }
        User updatedUser = userService.updateProfile(userToUpdate, profile);
        return userMapper.entityToDto(updatedUser);
    }

    @DeleteMapping("/@{username}")
    public UserResponseDto deleteUser(@PathVariable String username, @RequestBody CredentialsDto credentialsDto) {
        User user = userService.findByUsername(username);
        Credentials credentials = credentialsMapper.DtoToEntity(credentialsDto);
        if (user == null) {
            throw new NotFoundException("username does not exist");
        } else if (!user.getCredentials().equals(credentials)) {
            throw new NotAuthorizedException("Wrong credentials provided");
        }
        return userMapper.entityToDto(userService.deleteUser(user));
    }

    @GetMapping("/@{username}")
    public UserResponseDto findUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("This user does not exist.");
        }
        return userMapper.entityToDto(user);
    }

    @GetMapping("/@{username}/tweets")
    public List<TweetResponseDto> getAllTweetsByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("This user does not exist.");
        }
        List<Tweet> userTweets = tweetService.findAllTweetsByUser(user);
        return tweetMapper.entitiesToDtos(userTweets);
    }

    @GetMapping("/@{username}/feed")
    public List<TweetResponseDto> getFeedByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("This user does not exist.");
        }
        List<Tweet> userTweets = tweetService.findAllTweetsByUser(user);
        List<Tweet> followingTweets = new ArrayList<>();
        List<User> following = user.getFollowing();
        for (User followerUser : following) {
            if (!user.isDeleted()) {
                List<Tweet> temp = tweetService.findAllTweetsByUser(followerUser);
                followingTweets.addAll(temp);
            }
        }
        userTweets.addAll(followingTweets);
        List<Tweet> sortedTweets = userTweets.stream()
                .sorted(Comparator.comparing(Tweet::getPosted).reversed())
                .toList();
        return tweetMapper.entitiesToDtos(sortedTweets);
    }

    @GetMapping("/@{username}/followers")
    public List<UserResponseDto> getActiveFollowers(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("This user does not exist.");
        }
        List<User> activeFollowers = userService.getActiveFollowers(user);
        return userMapper.entitiesToDtos(activeFollowers);
    }

    @PostMapping("/@{username}/unfollow")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void removeFollower(@PathVariable String username, @RequestBody CredentialsDto credentialsDto) {
        User followedUser = userService.findByUsername(username);
        User followingUser = userService.findActiveUserByCredentials(credentialsMapper.DtoToEntity(credentialsDto));
        if (followingUser == null) {
            throw new NotFoundException("The credentials provided to do not match a registered user");
        }
        if (followedUser == null) {
            throw new NotFoundException("The username to unfollow does not exist or is not active");
        }
        if (!(followedUser.getFollowers()).contains(followingUser)) {
            throw new BadRequestException(followingUser.getCredentials().getUsername() + " is not following the user with the username: " + username);
        }
        userService.removeFollower(followedUser, followingUser);
    }

    @GetMapping("/@{username}/following")
    public List<UserResponseDto> getActiveFollowingList(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("This user does not exist.");
        }
        List<User> activeFollowing = userService.getActiveFollowing(user);
        return userMapper.entitiesToDtos(activeFollowing);
    }

    @GetMapping("/@{username}/mentions")
    public List<TweetResponseDto> findAllUserMentions(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("This user does not exist.");
        }
        List<Tweet> mentionedTweets = tweetService.findUserMentionedTweets(user);
        return tweetMapper.entitiesToDtos(mentionedTweets);
    }
}
