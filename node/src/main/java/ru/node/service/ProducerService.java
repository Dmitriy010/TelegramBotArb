package ru.node.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void producerAnswerOrderInfo(SendMessage sendMessage);
    void producerAnswerSubscribe(SendMessage sendMessage);
    void producerAnswerExchange(SendMessage sendMessage);
    void producerAnswerPaymentSystem(SendMessage sendMessage);
    void producerAnswerLimit(SendMessage sendMessage);
    void producerAnswerCreateSubscribe(SendMessage sendMessage);
}
