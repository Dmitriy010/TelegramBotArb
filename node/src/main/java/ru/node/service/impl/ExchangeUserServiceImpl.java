package ru.node.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.node.model.Exchange;
import ru.node.model.ExchangeUser;
import ru.node.model.User;
import ru.node.repository.ExchangeUserRepository;
import ru.node.service.ExchangeService;
import ru.node.service.ExchangeUserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeUserServiceImpl implements ExchangeUserService {

    private final ExchangeUserRepository exchangeUserRepository;

    @Override
    public void create(List<ExchangeUser> exchangeUserList) {
        exchangeUserRepository.saveAll(exchangeUserList);
    }

    @Override
    public void deleteByExchangesAndUser(User user, List<Exchange> exchangeList) {
        exchangeUserRepository.deleteAllByExchangesAndUser(user, exchangeList);
    }

    @Override
    public void deleteAllByUser(User user) {
        exchangeUserRepository.deleteAllByUser(user);
    }

    @Override
    public List<ExchangeUser> findAllByUserId(Long userId) {
        return exchangeUserRepository.findAllByUser(new User(userId));
    }
}
