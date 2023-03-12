package ru.lds.telegram.service;

import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.dto.UserActionLimitDto;
import ru.lds.telegram.dto.UserActionDto;
import ru.lds.telegram.dto.UserRegisterDto;

public interface ProducerService {

    void produceOrderInfo(String rabbitQueue, OrderDto orderDto);
    void produceUserAction(String rabbitQueue, UserActionDto userActionDto);
    void produceUserActionLimit(String rabbitQueue, UserActionLimitDto userActionLimitDto);
    void produceUserRegister(String rabbitQueue, UserRegisterDto userRegisterDto);
}
