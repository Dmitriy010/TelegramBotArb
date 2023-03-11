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
    public void producerAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_order_info_message", sendMessage);
    }

    @Override
    public void producerAnswerSubscribe(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_message_subscribe", sendMessage);
    }

    @Override
    public void producerAnswerActionSubscribe(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_action_subscribe", sendMessage);
    }

    @Override
    public void producerAnswerActionUserExchange(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_action_user_exchange", sendMessage);
    }

    @Override
    public void producerAnswerActionUserPaymentSystem(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_action_user_payment_system", sendMessage);
    }

    @Override
    public void producerAnswerActionUser(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_action_user", sendMessage);
    }
}
