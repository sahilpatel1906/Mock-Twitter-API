package com.cooksys.team2socialmedia.entities;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Embedded
    private Credentials credentials;

    @Embedded
    private Profile profile;

    @CreationTimestamp
    private Timestamp joined;
    
    private boolean deleted = false;

    @OneToMany(mappedBy = "author")
    private List<Tweet> tweets;

    @ManyToMany
    @JoinTable(name = "user_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tweet_id"))
    private List<Tweet> likedTweets;

    @ManyToMany(mappedBy="mentionedUsers")
    private List<Tweet> mentionedTweets;

    @ManyToMany
    @JoinTable(name = "followers_following")
    private List<User> following;

    @ManyToMany(mappedBy = "following")
    private List<User> followers;

}
