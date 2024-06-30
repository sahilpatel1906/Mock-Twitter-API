package com.cooksys.team2socialmedia.mappers;

import com.cooksys.team2socialmedia.dtos.UserRequestDto;
import com.cooksys.team2socialmedia.dtos.UserResponseDto;
import com.cooksys.team2socialmedia.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ProfileMapper.class, CredentialsMapper.class })
public interface UserMapper {

    @Mapping(target = "username", source = "credentials.username")
    UserResponseDto entityToDto(User entity);
    List<UserResponseDto> entitiesToDtos(List<User> entities);
    User DtoToEntity(UserRequestDto userRequestDto);

}
