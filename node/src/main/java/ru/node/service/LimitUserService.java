package ru.node.service;

import ru.node.model.LimitUser;

public interface LimitUserService {

    void create(LimitUser limitUser);

    void update(LimitUser limitUser);

    LimitUser findByUserId(Long userId);
}
