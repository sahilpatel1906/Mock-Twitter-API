package com.cooksys.team2socialmedia.services;

import com.cooksys.team2socialmedia.dtos.ContextDto;
import com.cooksys.team2socialmedia.dtos.TweetRequestDto;
import com.cooksys.team2socialmedia.dtos.TweetResponseDto;
import com.cooksys.team2socialmedia.entities.Hashtag;
import com.cooksys.team2socialmedia.entities.Tweet;
import com.cooksys.team2socialmedia.entities.User;

import java.util.List;

public interface TweetService {
    Tweet findById(Long id);

    List<Tweet> findAllNonDeletedTweets();

    List<Tweet> findAllTweetsByHashtag(Hashtag hashtag);

    Boolean hasContent(Tweet tweet);

    List<String> findHastagAndMentions(String content, String s);

    Tweet saveTweet(Tweet tweet);

    List<Tweet> findAllTweetsByUser(User user);

    Tweet createReplyTweet(TweetRequestDto tweetRequestDto, Tweet tweetToReply, User user);

    Tweet createTweet(TweetRequestDto tweetRequestDto, User user);

    Tweet addHashtagsAndMentionsToTweet(Tweet tweet);

    TweetResponseDto deleteTweet(Tweet tweet);

    List<Tweet> findUserMentionedTweets(User user);

    List<Tweet> sortTweetsReversedChronoOrder(List<Tweet> tweets);

    void likeTweet(Tweet tweet, User user);

    Tweet repostTweet(Tweet tweetToRepost, User user);

    ContextDto getContext(Tweet tweet);

    List<User> getAllTweetLikesActiveUsers(Tweet tweet);

    List<Tweet> findAllTweetReplies(Tweet tweet);

    List<User> getAllMentionsActiveUsers(Tweet tweet);

    List<Tweet> findAllActiveReposts(Tweet tweet);

    Boolean validateTweetNotNullAndNotDeleted(Tweet tweet);

}
