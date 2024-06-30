package com.cooksys.team2socialmedia.entities;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User author;

    @CreationTimestamp
    private Timestamp posted;

    private boolean deleted = false;

    private String content;

    @ManyToOne
    private Tweet inReplyTo;

    @OneToMany(mappedBy = "inReplyTo")
    List<Tweet> replies = new ArrayList<>();

    @ManyToOne
    private Tweet repostOf;

    @OneToMany(mappedBy = "repostOf")
    List<Tweet> reposts = new ArrayList<>();

    @ManyToMany(mappedBy = "likedTweets")
    private List<User> likedByUsers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_mentions",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> mentionedUsers = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "tweet_hashtags",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
    private List<Hashtag> hashtags = new ArrayList<>();

}
