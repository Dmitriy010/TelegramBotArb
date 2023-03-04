package ru.node.service;

import ru.node.model.Exchange;
import ru.node.model.ExchangeUser;
import ru.node.model.User;

import java.util.List;

public interface ExchangeUserService {

    void create(List<ExchangeUser> exchangeUserList);

    void deleteByExchangesAndUser(User user, List<Exchange> exchangeList);

    void deleteAllByUser(User user);

    List<ExchangeUser> findAllByUserId(Long userId);
}
