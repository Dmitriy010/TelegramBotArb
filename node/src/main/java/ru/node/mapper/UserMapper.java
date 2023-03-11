package ru.node.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.NonNull;
import ru.node.config.MapStructConfig;
import ru.node.dto.UserRegisterDto;
import ru.node.model.User;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "exchangeUserList", ignore = true)
    @Mapping(target = "paymentSystemUserList", ignore = true)
    @Mapping(target = "limitUser", ignore = true)
    User userRegisterDtoToUser(@NonNull UserRegisterDto userRegisterDto);
}
