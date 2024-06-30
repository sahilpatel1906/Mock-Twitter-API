package com.cooksys.team2socialmedia.services;

import com.cooksys.team2socialmedia.entities.Hashtag;

import java.util.List;

public interface HashtagService {
    List<Hashtag> findAll();

    Hashtag findByLabel(String label);

    List<Hashtag> save(List<String> hashtagsFound);
}
