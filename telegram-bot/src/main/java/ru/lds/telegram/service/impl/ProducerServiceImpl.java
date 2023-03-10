package ru.lds.telegram.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.dto.UserActionLimitDto;
import ru.lds.telegram.dto.UserActionDto;
import ru.lds.telegram.dto.UserRegisterDto;
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
    public void produceUserAction(String rabbitQueue, UserActionDto userActionDto) {
        rabbitTemplate.convertAndSend(rabbitQueue, userActionDto);
    }

    @Override
    public void produceUserActionLimit(String rabbitQueue, UserActionLimitDto userActionLimitDto) {
        rabbitTemplate.convertAndSend(rabbitQueue, userActionLimitDto);
    }

    @Override
    public void produceUserRegister(String rabbitQueue, UserRegisterDto userRegisterDto) {
        rabbitTemplate.convertAndSend(rabbitQueue, userRegisterDto);
    }
}
