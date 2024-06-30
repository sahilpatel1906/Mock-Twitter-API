package com.cooksys.team2socialmedia.repositories;

import com.cooksys.team2socialmedia.entities.Credentials;
import com.cooksys.team2socialmedia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDeleted(Boolean deletedFlagPresent);

    User findByCredentialsUsernameAndDeleted(String username, Boolean deletedFlagPresent);

    User findByCredentialsUsernameAndCredentialsPassword(String username, String password);

    User findByCredentialsAndDeleted(Credentials credentials, boolean deleted);

}
