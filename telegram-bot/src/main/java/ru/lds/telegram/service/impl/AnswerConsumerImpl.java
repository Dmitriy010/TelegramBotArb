package ru.lds.telegram.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.lds.telegram.bot.TelegramBot;
import ru.lds.telegram.bot.UpdateProcessor;
import ru.lds.telegram.controller.Controller;
import ru.lds.telegram.service.AnswerConsumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateProcessor updateProcessor;

    @Override
    @RabbitListener(queues = "answer_message")
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
