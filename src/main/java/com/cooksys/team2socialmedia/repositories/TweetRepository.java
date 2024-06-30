package com.cooksys.team2socialmedia.repositories;

import com.cooksys.team2socialmedia.entities.Hashtag;
import com.cooksys.team2socialmedia.entities.Tweet;
import com.cooksys.team2socialmedia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByDeletedIsFalse();

    List<Tweet> findAllByAuthor(User user);

    @Query("SELECT t FROM Tweet t JOIN t.hashtags h WHERE h = :hashtag AND t.deleted = false AND t.content IS NOT NULL")
    List<Tweet> findAllByHashtagAndDeletedFalseAndContentNotNull(@Param("hashtag") Hashtag hashtag);

}
