package com.cooksys.team2socialmedia.controllers;

import com.cooksys.team2socialmedia.dtos.HashtagDto;
import com.cooksys.team2socialmedia.dtos.TweetResponseDto;
import com.cooksys.team2socialmedia.entities.Hashtag;
import com.cooksys.team2socialmedia.entities.Tweet;
import com.cooksys.team2socialmedia.exceptions.NotFoundException;
import com.cooksys.team2socialmedia.mappers.HashtagMapper;
import com.cooksys.team2socialmedia.mappers.TweetMapper;
import com.cooksys.team2socialmedia.services.HashtagService;
import com.cooksys.team2socialmedia.services.TweetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class HashtagController {

    private final HashtagService hashtagService;
    private final HashtagMapper hashtagMapper;
    private final TweetService tweetService;
    private final TweetMapper tweetMapper;


    @GetMapping
    public List<HashtagDto> getAllHashtags() {
       return hashtagMapper.entitiesToDto(hashtagService.findAll());
    }

    @GetMapping("/{label}")
    public List<TweetResponseDto> getAllTweetsByHashtag(@PathVariable String label) {
        Hashtag hashtag = hashtagService.findByLabel(label);
        if(hashtag == null) {
            throw new NotFoundException("This Hashtag does not exist");
        }
        List<Tweet> tweetsWithHashtag = tweetService.findAllTweetsByHashtag(hashtag);
        return tweetMapper.entitiesToDtos(tweetsWithHashtag);
    }
}
