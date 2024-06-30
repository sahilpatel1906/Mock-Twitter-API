package com.cooksys.team2socialmedia.repositories;

import com.cooksys.team2socialmedia.entities.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Hashtag findByLabelIgnoreCase(String label);

}
