package ru.node.service;

import ru.node.model.ExchangeUser;
import ru.node.model.User;

import java.util.List;

public interface ExchangeUserService {

    void createByUserIdAndExchangeId(Long userId, Long exchangeId);

    void deleteByUserAndExchangeId(User user, Long exchangeId);

    void deleteAllByUser(User user);

    List<ExchangeUser> findAllByUserId(Long userId);
}
