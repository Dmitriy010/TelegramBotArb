package ru.node.service;

import ru.node.model.Exchange;
import ru.node.model.PaymentSystem;

import java.util.List;

public interface PaymentSystemService {

    List<PaymentSystem> findAll();

    List<PaymentSystem> findAllByIds(List<Long> listIds);
}
