package ru.node.service;

import ru.node.model.Exchange;
import ru.node.model.ExchangeUser;
import ru.node.model.PaymentSystem;
import ru.node.model.PaymentSystemUser;
import ru.node.model.User;

import java.util.List;

public interface PaymentSystemUserService {

    void create(List<PaymentSystemUser> paymentSystemUserList);

    void deleteByPaymentSystemsAndUser(User user, List<PaymentSystem> paymentSystemList);

    void deleteAllByUser(User user);

    List<PaymentSystemUser> findAllByUserId(Long userId);
}
