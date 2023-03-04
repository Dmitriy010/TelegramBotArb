package ru.node.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.node.model.PaymentSystem;
import ru.node.model.PaymentSystemUser;
import ru.node.model.User;
import ru.node.repository.PaymentSystemUserRepository;
import ru.node.service.PaymentSystemUserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentSystemUserServiceImpl implements PaymentSystemUserService {

    private final PaymentSystemUserRepository paymentSystemUserRepository;

    @Override
    public void create(List<PaymentSystemUser> paymentSystemUserList) {
        paymentSystemUserRepository.saveAll(paymentSystemUserList);
    }

    @Override
    public void deleteByPaymentSystemsAndUser(User user, List<PaymentSystem> paymentSystemList) {
        paymentSystemUserRepository.deleteAllByPaymentSystemsAndUser(user, paymentSystemList);
    }

    @Override
    public void deleteAllByUser(User user) {
        paymentSystemUserRepository.deleteAllByUser(user);
    }

    @Override
    public List<PaymentSystemUser> findAllByUserId(Long userId) {
        return paymentSystemUserRepository.findAllByUser(new User(userId));
    }
}
