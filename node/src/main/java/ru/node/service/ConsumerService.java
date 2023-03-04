package ru.node.service;

import ru.node.dto.OrderDto;
import ru.node.dto.SubscribeActionDto;
import ru.node.dto.UserActionDto;
import ru.node.dto.UserRegisterDto;

public interface ConsumerService {
    void consumeTextMessageOrderInfo(OrderDto orderDto);

    void consumeTextMessageSubscribe(OrderDto orderDto);

    void consumeTextActionSubscribe(SubscribeActionDto subscribeActionDto);

    void consumeTextActionUserExchange(UserActionDto userActionDto);

    void consumeTextActionUserPaymentSystem(UserActionDto userActionDto);

    void consumeTextRegisterUser(UserRegisterDto userRegisterDto);
}
