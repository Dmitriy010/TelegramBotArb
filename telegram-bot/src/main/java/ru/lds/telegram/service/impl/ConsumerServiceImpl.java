package ru.lds.telegram.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.lds.telegram.bot.UpdateProcessor;
import ru.lds.telegram.service.ConsumerService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final UpdateProcessor updateProcessor;

    @Override
    @RabbitListener(queues = "answer_message")
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = "answer_message_subscribe")
    public void consumeSubscribe(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = "answer_action_subscribe")
    public void consumeSubscribeAction(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
