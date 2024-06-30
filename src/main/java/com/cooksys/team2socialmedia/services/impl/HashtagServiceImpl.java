package com.cooksys.team2socialmedia.services.impl;

import com.cooksys.team2socialmedia.entities.Hashtag;
import com.cooksys.team2socialmedia.repositories.HashtagRepository;
import org.springframework.stereotype.Service;
import com.cooksys.team2socialmedia.services.HashtagService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;

    @Override
    public List<Hashtag> findAll() {
        return hashtagRepository.findAll();
    }

    @Override
    public Hashtag findByLabel(String label) {
        return hashtagRepository.findByLabelIgnoreCase(label);
    }

    @Override
    public List<Hashtag> save(List<String> hashtagsFound) {
        List<Hashtag> hashtags = new ArrayList<>();
        for (String s : hashtagsFound) {
            Hashtag existingHastag = findByLabel(s);
            if (existingHastag == null) {
                Hashtag hashtagToAdd = new Hashtag();
                hashtagToAdd.setLabel(s.substring(1));
                hashtagRepository.save(hashtagToAdd);
                hashtags.add(hashtagToAdd);
            } else {
                hashtags.add(existingHastag);
            }
        }
        return hashtags;
    }
}
