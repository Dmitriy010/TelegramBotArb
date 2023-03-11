package ru.node.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.node.dto.UserRegisterDto;
import ru.node.mapper.UserMapper;
import ru.node.model.User;
import ru.node.repository.UserRepository;
import ru.node.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static ru.node.constants.Constants.ZONE_ID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User create(UserRegisterDto userRegisterDto) {
        var newUser = userMapper.userRegisterDtoToUser(userRegisterDto);
        newUser.setDate(LocalDateTime.now(ZoneId.of(ZONE_ID)));
        return userRepository.save(newUser);
    }

    @Override
    public User findByUserId(Long userId) {
        var user = userRepository.findByUserId(userId);

        return user.orElse(null);
    }

    @Override
    public User findByUserName(String username) {
        return userRepository.findByUserName(username);
    }
}
