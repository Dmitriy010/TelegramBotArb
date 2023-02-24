package ru.lds.telegram.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ConsumerService {

    void consume(SendMessage sendMessage);

    void consumeSubscribe(SendMessage sendMessage);

    void consumeSubscribeAction(SendMessage sendMessage);
}
