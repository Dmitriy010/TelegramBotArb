package ru.node.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.node.service.ProducerService;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void producerAnswerOrderInfo(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_order_info", sendMessage);
    }

    @Override
    public void producerAnswerSubscribe(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_subscribe", sendMessage);
    }

    @Override
    public void producerAnswerCreateSubscribe(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_create_subscribe", sendMessage);
    }

    @Override
    public void producerAnswerExchange(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_exchange", sendMessage);
    }

    @Override
    public void producerAnswerPaymentSystem(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_payment_system", sendMessage);
    }

    @Override
    public void producerAnswerLimit(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_limit", sendMessage);
    }
}
