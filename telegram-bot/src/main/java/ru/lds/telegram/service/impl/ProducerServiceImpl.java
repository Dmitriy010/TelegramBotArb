package ru.lds.telegram.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.dto.SubscribeActionDto;
import ru.lds.telegram.service.ProducerService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceOrderInfo(String rabbitQueue, OrderDto orderDto) {
        rabbitTemplate.convertAndSend(rabbitQueue, orderDto);
    }

    @Override
    public void produceSubscribeAction(String rabbitQueue, SubscribeActionDto subscribeActionDto) {
        rabbitTemplate.convertAndSend(rabbitQueue, subscribeActionDto);
    }
}
