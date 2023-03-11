package ru.node.service.impl;

import jakarta.transaction.Transactional;
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
    @Transactional
    public void createByUserIdAndPaymentSystemId(Long userId, Long paymentSystem) {
        paymentSystemUserRepository.save(new PaymentSystemUser(new User(userId), new PaymentSystem(paymentSystem)));
    }

    @Override
    public void deleteByUserAndPaymentSystemId(User user, Long paymentSystem) {
        paymentSystemUserRepository.deleteByUserAndPaymentSystem(user, new PaymentSystem(paymentSystem));
    }

    @Override
    public void deleteAllByUser(User user) {
        paymentSystemUserRepository.deleteAllByUser(user);
    }

    @Override
    public List<PaymentSystemUser> findAllByUserId(Long userId) {
        return paymentSystemUserRepository.findAllByUser(userId);
    }
}
