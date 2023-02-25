package ru.lds.telegram.service;

import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.dto.SubscribeActionDto;

public interface ProducerService {

    void produceOrderInfo(String rabbitQueue, OrderDto orderDto);

    void produceSubscribeAction(String rabbitQueue, SubscribeActionDto subscribeActionDto);
}
