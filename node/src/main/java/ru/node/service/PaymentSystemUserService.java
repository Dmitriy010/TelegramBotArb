package ru.node.service;

import ru.node.model.PaymentSystemUser;
import ru.node.model.User;

import java.util.List;

public interface PaymentSystemUserService {

    void createByUserIdAndPaymentSystemId(Long userId, Long paymentSystemId);
    void deleteByUserAndPaymentSystemId(User user, Long paymentSystemId);
    void deleteAllByUser(User user);
    List<PaymentSystemUser> findAllByUserId(Long userId);
}
