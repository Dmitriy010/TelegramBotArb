package ru.node.service;

import ru.node.dto.OrderDto;
import ru.node.dto.UserActionLimitDto;
import ru.node.dto.UserActionDto;
import ru.node.dto.UserRegisterDto;

public interface ConsumerService {
    void consumeOrderInfo(OrderDto orderDto);
    void consumeCreateSubscribe(OrderDto orderDto);
    void consumeSubscribe(UserActionDto userActionDto);
    void consumeExchange(UserActionDto userActionDto);
    void consumePaymentSystem(UserActionDto userActionDto);
    void consumeRegisterUser(UserRegisterDto userRegisterDto);
    void consumeLimit(UserActionLimitDto userActionLimitDto);
}
