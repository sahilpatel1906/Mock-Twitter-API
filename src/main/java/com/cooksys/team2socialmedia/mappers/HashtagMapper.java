package com.cooksys.team2socialmedia.mappers;

import com.cooksys.team2socialmedia.dtos.HashtagDto;
import com.cooksys.team2socialmedia.entities.Hashtag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

    HashtagDto entityToDto(Hashtag entity);
    Hashtag DtoToEntity(HashtagDto hashtagDto);

    List<HashtagDto> entitiesToDto(List<Hashtag> hashtags);
}
