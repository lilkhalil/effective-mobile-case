package ru.mirea.lilkhalil.tasks.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.dto.SignUpRqDto;
import ru.mirea.lilkhalil.tasks.dto.UserDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto userToUserDto(User user);
    @Mapping(target = "password", ignore = true)
    User signUpRqDtoToUser(SignUpRqDto signUpRqDto);
}
