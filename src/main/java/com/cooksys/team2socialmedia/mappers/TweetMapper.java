package com.cooksys.team2socialmedia.mappers;

import com.cooksys.team2socialmedia.dtos.TweetRequestDto;
import com.cooksys.team2socialmedia.dtos.TweetResponseDto;
import com.cooksys.team2socialmedia.entities.Tweet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface TweetMapper {

    TweetResponseDto entityToDto(Tweet entity);
    List<TweetResponseDto> entitiesToDtos(List<Tweet> entities);
    Tweet DtoToEntity(TweetRequestDto tweetRequestDto);

}
