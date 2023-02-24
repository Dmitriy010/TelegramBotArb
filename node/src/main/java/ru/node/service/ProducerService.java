package ru.node.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void producerAnswer(SendMessage sendMessage);

    void producerAnswerSubscribe(SendMessage sendMessage);

    void producerAnswerActionSubscribe(SendMessage sendMessage);
}
