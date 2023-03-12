package ru.node.service.impl;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.node.model.LimitUser;
import ru.node.repository.LimitUserRepository;
import ru.node.service.LimitUserService;

@Service
@RequiredArgsConstructor
public class LimitUserServiceImpl implements LimitUserService {

    private final LimitUserRepository limitUserRepository;

    @Override
    public void create(LimitUser limitUser) {
        limitUserRepository.save(limitUser);
    }

    @Override
    @Timed("updateUserLimit")
    public void update(LimitUser limitUser) {
        limitUserRepository.save(limitUser);
    }

    @Override
    @Timed("getUserLimit")
    public LimitUser findByUserId(Long userId) {
        return limitUserRepository.findByUserId(userId);
    }
}
