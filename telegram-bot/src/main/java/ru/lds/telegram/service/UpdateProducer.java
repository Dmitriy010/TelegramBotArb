package ru.lds.telegram.service;

import ru.lds.telegram.dto.OrderInfoDto;
import ru.lds.telegram.dto.SubscribeActionDto;

public interface UpdateProducer {

    void produce(String rabbitQueue, OrderInfoDto orderInfoDto);

    void produceSubscribeAction(String rabbitQueue, SubscribeActionDto subscribeActionDto);
}
