package ru.node.service;

import ru.node.dto.OrderDto;
import ru.node.dto.SubscribeActionDto;

public interface ConsumerService {
    void consumeTextMessageOrderInfo(OrderDto orderDto);

    void consumeTextMessageSubscribe(OrderDto orderDto);

    void consumeTextActionSubscribe(SubscribeActionDto subscribeActionDto);
}
