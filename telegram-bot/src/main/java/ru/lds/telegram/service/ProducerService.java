package ru.lds.telegram.service;

import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.dto.SubscribeActionDto;
import ru.lds.telegram.dto.UserActionDto;
import ru.lds.telegram.dto.UserRegisterDto;

public interface ProducerService {

    void produceOrderInfo(String rabbitQueue, OrderDto orderDto);

    void produceSubscribeAction(String rabbitQueue, SubscribeActionDto subscribeActionDto);

    void produceUserAction(String rabbitQueue, UserActionDto userActionDto);

    void produceUserRegister(String rabbitQueue, UserRegisterDto userRegisterDto);
}
