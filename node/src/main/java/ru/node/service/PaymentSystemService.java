package ru.node.service;

import ru.node.model.PaymentSystem;

import java.util.List;

public interface PaymentSystemService {

    List<PaymentSystem> findAll();
}
