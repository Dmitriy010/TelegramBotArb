package ru.node.service;

import ru.node.dto.OrderInfoDto;
import ru.node.dto.OrderSubscribeDto;
import ru.node.dto.SubscribeActionDto;

public interface ConsumerService {
    void consumeTextMessageUpdates(OrderInfoDto jsonObject);

    void consumeTextMessageSubscribe(OrderSubscribeDto orderSubscribeDto);

    void consumeTextActionSubscribe(SubscribeActionDto subscribeActionDto);
}
