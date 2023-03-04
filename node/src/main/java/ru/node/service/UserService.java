package ru.node.service;

import org.springframework.lang.Nullable;
import ru.node.dto.UserRegisterDto;
import ru.node.model.User;

public interface UserService {

    void create(UserRegisterDto userRegisterDto);

    @Nullable
    User findByUserId(Long userId);
}
