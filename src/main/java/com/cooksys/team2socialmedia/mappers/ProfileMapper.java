package com.cooksys.team2socialmedia.mappers;

import com.cooksys.team2socialmedia.dtos.ProfileDto;
import com.cooksys.team2socialmedia.entities.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileDto entityToDto(Profile entity);
    Profile DtoToEntity(ProfileDto profileDto);
}
