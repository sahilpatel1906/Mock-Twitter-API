package com.cooksys.team2socialmedia.services.impl;

import com.cooksys.team2socialmedia.dtos.ContextDto;
import com.cooksys.team2socialmedia.dtos.TweetRequestDto;
import com.cooksys.team2socialmedia.dtos.TweetResponseDto;
import com.cooksys.team2socialmedia.entities.Hashtag;
import com.cooksys.team2socialmedia.entities.Tweet;
import com.cooksys.team2socialmedia.entities.User;
import com.cooksys.team2socialmedia.mappers.TweetMapper;
import com.cooksys.team2socialmedia.repositories.TweetRepository;
import com.cooksys.team2socialmedia.services.HashtagService;
import com.cooksys.team2socialmedia.services.UserService;
import org.springframework.stereotype.Service;

import com.cooksys.team2socialmedia.services.TweetService;

import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final HashtagService hashtagService;
    private final UserService userService;

    @Override
    public Tweet findById(Long id) {
        return tweetRepository.findById(id).orElse(null);
    }

    @Override
    public List<Tweet> findAllNonDeletedTweets() {
        List<Tweet> tweets = tweetRepository.findByDeletedIsFalse();
        return sortTweetsReversedChronoOrder(tweets);
    }

    @Override
    public List<Tweet> findAllTweetsByHashtag(Hashtag hashtag) {
        List<Tweet> tweets = tweetRepository.findAllByHashtagAndDeletedFalseAndContentNotNull(hashtag);
        return sortTweetsReversedChronoOrder(tweets);
    }

    @Override
    public Boolean hasContent(Tweet tweet) {
        return tweet.getContent() != null;
    }

    @Override
    public List<String> findHastagAndMentions(String content, String s) {
        Pattern pattern = Pattern.compile("(?<=^|(?<=[^a-zA-Z0-9-_\\\\.]))" + s + "([A-Za-z]+[A-Za-z0-9_]+)");
        Matcher matcher = pattern.matcher(content);
        List<String> allMatches = new ArrayList<String>();
        while (matcher.find()) {
            allMatches.add(matcher.group());
        }
        return allMatches;
    }

    @Override
    public Tweet saveTweet(Tweet tweet) {
        return tweetRepository.saveAndFlush(tweet);
    }

    @Override
    public List<Tweet> findAllTweetsByUser(User user) {
        List<Tweet> tweets = tweetRepository.findAllByAuthor(user);
        return sortTweetsReversedChronoOrder(tweets);
    }

    @Override
    public Tweet createReplyTweet(TweetRequestDto tweetRequestDto, Tweet tweetToReply, User user) {
        Tweet savedTweet = createTweet(tweetRequestDto, user);
        savedTweet.setInReplyTo(tweetToReply);
        return tweetRepository.saveAndFlush(savedTweet);
    }

    @Override
    public Tweet createTweet(TweetRequestDto tweetRequestDto, User user) {
        Tweet tweet = tweetMapper.DtoToEntity(tweetRequestDto);
        tweet.setAuthor(user);
        Tweet savedTweet = addHashtagsAndMentionsToTweet(tweet);
        user.getTweets().add(savedTweet);
        userService.saveUser(user);
        return savedTweet;
    }

    @Override
    public Tweet addHashtagsAndMentionsToTweet(Tweet tweet) {
        List<String> hashtagsFound = findHastagAndMentions(tweet.getContent(), "#");
        List<String> mentionsFound = findHastagAndMentions(tweet.getContent(), "@");

        if (!hashtagsFound.isEmpty()) {
            List<Hashtag> hashtags = hashtagService.save(hashtagsFound);
            tweet.setHashtags(hashtags);
        }
        if (!mentionsFound.isEmpty()) {
            List<User> mentionedUsers = userService.saveUserMentions(mentionsFound);
            tweet.setMentionedUsers(mentionedUsers);
        }
        return tweetRepository.saveAndFlush(tweet);
    }

    @Override
    public TweetResponseDto deleteTweet(Tweet tweet) {
        tweet.setDeleted(true);
        tweetRepository.saveAndFlush(tweet);
        return tweetMapper.entityToDto(tweet);
    }

    @Override

    public List<Tweet> findUserMentionedTweets(User user) {
        List<Tweet> tweets = findAllNonDeletedTweets()
                .stream()
                .filter(tweet -> (tweet.getMentionedUsers()).contains(user))
                .toList();
        return sortTweetsReversedChronoOrder(tweets);
    }

    @Override
    public List<Tweet> sortTweetsReversedChronoOrder(List<Tweet> tweets) {
        return tweets.stream()
                .sorted(Comparator.comparing(Tweet::getPosted).reversed())
                .collect(Collectors.toList());
    }

    public List<Tweet> sortTweetsChronoOrder(List<Tweet> tweets) {
        return tweets.stream()
                .sorted(Comparator.comparing(Tweet::getPosted).reversed())
                .collect(Collectors.toList());
    }

    public void likeTweet(Tweet tweet, User user) {
        List<Tweet> updateTweetsLiked = user.getLikedTweets();
        updateTweetsLiked.add(tweet);
        userService.saveUser(user);
    }

    @Override
    public Tweet repostTweet(Tweet tweetToRepost, User user) {
        Tweet newTweet = new Tweet();
        newTweet.setAuthor(user);
        newTweet.setRepostOf(tweetToRepost);
        Tweet tweetToReturn = tweetRepository.saveAndFlush(newTweet);
        user.getTweets().add(tweetToReturn);
        return tweetToReturn;
    }

    @Override
    public ContextDto getContext(Tweet tweet) {
        List<Tweet> before = new ArrayList<>();
        Tweet currTweet = tweet.getInReplyTo();
        while (currTweet != null) {
            before.add(currTweet);
            currTweet = currTweet.getInReplyTo();
        }
        List<Tweet> after = new ArrayList<>(tweet.getReplies());
        afterContextGen(after, 0);
        ContextDto contextDto = new ContextDto();
        removeDeletedTweetFromList(before);
        removeDeletedTweetFromList(after);
        List<Tweet> sortedBefore = sortTweetsChronoOrder(before);
        List<Tweet> sortedAfter = sortTweetsChronoOrder(after);
        contextDto.setAfter(tweetMapper.entitiesToDtos(sortedAfter));
        contextDto.setBefore(tweetMapper.entitiesToDtos(sortedBefore));
        contextDto.setTarget(tweetMapper.entityToDto(tweet));
        return contextDto;
    }

    private void removeDeletedTweetFromList(List<Tweet> tweets) {
        for (Tweet tweet : tweets) {
            if (tweet.isDeleted()) {
                tweets.remove(tweet);
            }
        }
    }

    private void afterContextGen(List<Tweet> after, int size) {
        int sizeBefore = after.size();
        for (int i = size; i < after.size(); i++) {
            Tweet t = after.get(i);
            if (t.getReplies() != null) {
                after.addAll(t.getReplies());
            }
        }
        int sizeAfter = after.size();
        if (sizeAfter > sizeBefore) {
            afterContextGen(after, sizeBefore);
        }
    }

    public List<User> getAllTweetLikesActiveUsers(Tweet tweet) {
        Set<User> users = tweet.getLikedByUsers()
                .stream()
                .filter(user -> !user.isDeleted())
                .collect(Collectors.toSet());
        return new ArrayList<>(users);
    }

    @Override
    public List<Tweet> findAllTweetReplies(Tweet tweet) {
        return tweet.getReplies()
                .stream()
                .filter(t -> !t.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getAllMentionsActiveUsers(Tweet tweet) {
        return tweet.getMentionedUsers()
                .stream()
                .filter(user -> !user.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Tweet> findAllActiveReposts(Tweet tweet) {
        return tweet.getReposts()
                .stream()
                .filter(t -> !t.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Boolean validateTweetNotNullAndNotDeleted(Tweet tweet) {
        return tweet == null || tweet.isDeleted();
    }


}
