package com.cooksys.team2socialmedia.controllers;

import com.cooksys.team2socialmedia.dtos.*;
import com.cooksys.team2socialmedia.entities.Credentials;
import com.cooksys.team2socialmedia.entities.Tweet;
import com.cooksys.team2socialmedia.entities.User;
import com.cooksys.team2socialmedia.exceptions.BadRequestException;
import com.cooksys.team2socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.team2socialmedia.exceptions.NotFoundException;
import com.cooksys.team2socialmedia.mappers.CredentialsMapper;
import com.cooksys.team2socialmedia.mappers.HashtagMapper;
import com.cooksys.team2socialmedia.mappers.TweetMapper;
import com.cooksys.team2socialmedia.mappers.UserMapper;
import com.cooksys.team2socialmedia.services.TweetService;
import com.cooksys.team2socialmedia.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;
    private final UserService userService;
    private final TweetMapper tweetMapper;
    private final CredentialsMapper credentialsMapper;
    private final HashtagMapper hashtagMapper;
    private final UserMapper userMapper;

    @GetMapping
    public List<TweetResponseDto> getAllNonDeletedTweets() {
        List<Tweet> nonDeletedTweets = tweetService.findAllNonDeletedTweets();
        return tweetMapper.entitiesToDtos(nonDeletedTweets);
    }

    @GetMapping("/{id}")
    public TweetResponseDto getTweetById(@PathVariable Long id) {
        Tweet tweet = tweetService.findById(id);
        if (tweet == null) {
            throw new NotFoundException("A tweet with this Id does not exist");
        }
        return tweetMapper.entityToDto(tweet);
    }

    @GetMapping("/{id}/context")
    public ContextDto retrieveContext(@PathVariable long id) {
        Tweet tweet = tweetService.findById(id);
        if (tweet == null || tweet.isDeleted()) {
            throw new NotFoundException("A tweet with this Id does not exist");
        }
        return tweetService.getContext(tweet);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto postTweet(@RequestBody TweetRequestDto tweetRequestDto) {
        if (tweetRequestDto.getCredentials() == null) {
            throw new BadRequestException("Content or Credentials need to be filled out");
        }
        if (tweetRequestDto.getContent() == null || tweetRequestDto.getCredentials().getPassword() == null ||
                tweetRequestDto.getCredentials().getUsername() == null) {
            throw new BadRequestException("Content or Credentials need to be filled out");
        }
        User user = userService.findByCredentials(tweetRequestDto.getCredentials());
        if (user == null) {
            throw new NotAuthorizedException("Credentials are invalid");
        }
        Tweet tweet = tweetService.createTweet(tweetRequestDto, user);
        return tweetMapper.entityToDto(tweet);
    }

    @PostMapping("/{id}/reply")
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto postReplyTweet(@PathVariable Long id, @RequestBody TweetRequestDto tweetRequestDto) {
        Tweet tweetToReply = tweetService.findById(id);
        Credentials credentials = credentialsMapper.DtoToEntity(tweetRequestDto.getCredentials());
        User user = userService.findActiveUserByCredentials(credentials);
        if (tweetService.validateTweetNotNullAndNotDeleted(tweetToReply)) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        if (user == null) {
            throw new NotFoundException("A user with these credentials does not exist");
        }
        if ((tweetRequestDto.getContent()).isEmpty()) {
            throw new BadRequestException("Content is required to reply to a tweet");
        }
        Tweet newReplyTweet = tweetService.createReplyTweet(tweetRequestDto, tweetToReply, user);
        return tweetMapper.entityToDto(newReplyTweet);
    }

    @PostMapping("/{id}/repost")
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto postTweetRepost(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
        Tweet tweetToRepost = tweetService.findById(id);
        Credentials credentials = credentialsMapper.DtoToEntity(credentialsDto);
        User user = userService.findActiveUserByCredentials(credentials);
        if (tweetService.validateTweetNotNullAndNotDeleted(tweetToRepost)) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        if (user == null) {
            throw new NotFoundException("A user with these credentials does not exist");
        }
        Tweet newTweet = tweetService.repostTweet(tweetToRepost, user);
        return tweetMapper.entityToDto(newTweet);
    }

    @PostMapping("/{id}/like")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void likeTweet(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
        Tweet tweet = tweetService.findById(id);
        if (tweetService.validateTweetNotNullAndNotDeleted(tweet)) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        User user = userService.findActiveUserByCredentials(credentialsMapper.DtoToEntity(credentialsDto));
        if (user == null) {
            throw new NotAuthorizedException("User credentials not found");
        }
        tweetService.likeTweet(tweet, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TweetResponseDto deleteTweetById(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
        Tweet tweet = tweetService.findById(id);
        if (tweetService.validateTweetNotNullAndNotDeleted(tweet)) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        User user = userService.findActiveUserByCredentials(credentialsMapper.DtoToEntity(credentialsDto));
        if (user == null) {
            throw new NotAuthorizedException("User credentials not found");
        }
        return tweetService.deleteTweet(tweet);
    }

    @GetMapping("/{id}/tags")
    public List<HashtagDto> getHashtagsByTweet(@PathVariable Long id) {
        Tweet tweet = tweetService.findById(id);
        if (tweetService.validateTweetNotNullAndNotDeleted(tweet)) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        return hashtagMapper.entitiesToDto(tweet.getHashtags());
    }

    @GetMapping("/{id}/likes")
    public List<UserResponseDto> getTweetLikes(@PathVariable Long id) {
        Tweet tweet = tweetService.findById(id);
        if (tweetService.validateTweetNotNullAndNotDeleted(tweet)) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        List<User> users = tweetService.getAllTweetLikesActiveUsers(tweet);
        return userMapper.entitiesToDtos(users);
    }

    @GetMapping("/{id}/replies")
    public List<TweetResponseDto> getTweetReplies(@PathVariable Long id) {
        Tweet tweet = tweetService.findById(id);
        if (tweetService.validateTweetNotNullAndNotDeleted(tweet)) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        List<Tweet> tweetReplies = tweetService.findAllTweetReplies(tweet);
        return tweetMapper.entitiesToDtos(tweetReplies);
    }

    @GetMapping("/{id}/mentions")
    public List<UserResponseDto> getTweetMentions(@PathVariable Long id) {
        Tweet tweet = tweetService.findById(id);
        if (tweetService.validateTweetNotNullAndNotDeleted(tweet)) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        List<User> mentions = tweetService.getAllMentionsActiveUsers(tweet);
        return userMapper.entitiesToDtos(mentions);
    }

    @GetMapping("/{id}/reposts")
    public List<TweetResponseDto> getTweetReposts(@PathVariable Long id) {
        Tweet tweet = tweetService.findById(id);
        if (tweetService.validateTweetNotNullAndNotDeleted(tweet)) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        List<Tweet> activeReposts = tweetService.findAllActiveReposts(tweet);
        return tweetMapper.entitiesToDtos(activeReposts);
    }

}
