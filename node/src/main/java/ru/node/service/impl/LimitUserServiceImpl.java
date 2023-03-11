package ru.node.service.impl;

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
    public void update(LimitUser limitUser) {
        limitUserRepository.save(limitUser);
    }

    @Override
    public LimitUser findByUserId(Long userId) {
        return limitUserRepository.findByUserId(userId);
    }
}
