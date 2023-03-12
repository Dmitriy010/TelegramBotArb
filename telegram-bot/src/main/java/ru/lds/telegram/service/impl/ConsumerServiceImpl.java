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
    @RabbitListener(queues = "answer_order_info")
    public void consumeOrderInfo(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = "answer_subscribe")
    public void consumeSubscribe(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = "answer_create_subscribe")
    public void consumeCreateSubscribe(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = "answer_exchange")
    public void consumeExchange(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = "answer_limit")
    public void consumeLimit(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = "answer_register_user")
    public void consumeRegisterUser(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = "answer_payment_system")
    public void consumePaymentSystem(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
