package ru.node.service.impl;

import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.node.model.Exchange;
import ru.node.model.ExchangeUser;
import ru.node.model.User;
import ru.node.repository.ExchangeUserRepository;
import ru.node.service.ExchangeUserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeUserServiceImpl implements ExchangeUserService {

    private final ExchangeUserRepository exchangeUserRepository;

    @Override
    @Transactional
    @Timed("addUserExchange")
    public void createByUserIdAndExchangeId(Long userId, Long exchangeId) {
        exchangeUserRepository.save(new ExchangeUser(new User(userId), new Exchange(exchangeId)));
    }

    @Override
    @Timed("deleteUserExchange")
    public void deleteByUserAndExchangeId(User user, Long exchangeId) {
        exchangeUserRepository.deleteByUserAndExchange(user, new Exchange(exchangeId));
    }

    @Override
    @Timed("deleteAllUserExchange")
    public void deleteAllByUser(User user) {
        exchangeUserRepository.deleteAllByUser(user);
    }

    @Override
    @Timed("getAllUserExchange")
    public List<ExchangeUser> findAllByUserId(Long userId) {
        return exchangeUserRepository.findAllByUser(userId);
    }
}
