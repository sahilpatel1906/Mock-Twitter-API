package com.cooksys.team2socialmedia.mappers;

import com.cooksys.team2socialmedia.dtos.CredentialsDto;
import com.cooksys.team2socialmedia.entities.Credentials;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {

    CredentialsDto entityToDto(Credentials entity);
    Credentials DtoToEntity(CredentialsDto profileDto);

}
