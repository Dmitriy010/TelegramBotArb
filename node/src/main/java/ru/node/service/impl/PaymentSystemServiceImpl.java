package ru.node.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.node.model.PaymentSystem;
import ru.node.repository.PaymentSystemRepository;
import ru.node.service.PaymentSystemService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentSystemServiceImpl implements PaymentSystemService {

    private final PaymentSystemRepository paymentSystemRepository;

    @Override
    public List<PaymentSystem> findAll() {
        var paymentSystemList = paymentSystemRepository.findAll();
        return paymentSystemList.stream().sorted(Comparator.comparing(PaymentSystem::getId)).collect(Collectors.toList());
    }
}
