package ru.lds.telegram.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ConsumerService {

    void consumeOrderInfo(SendMessage sendMessage);
    void consumeSubscribe(SendMessage sendMessage);
    void consumeCreateSubscribe(SendMessage sendMessage);
    void consumeRegisterUser(SendMessage sendMessage);
    void consumeExchange(SendMessage sendMessage);
    void consumeLimit(SendMessage sendMessage);
    void consumePaymentSystem(SendMessage sendMessage);
}
