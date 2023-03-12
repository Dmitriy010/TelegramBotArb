package ru.lds.telegram.cache;

import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.enums.BotStateEnum;

public interface DataCache {
    void setUserCurrentBotState(long userId, BotStateEnum botStateEnum);

    void setCurrentOrderDtoByUserId(long userId, OrderDto orderDto);

    OrderDto getCurrentOrderDtoByUserId(long userId);

    void deleteOrderDtoByUserId(long userId);

    BotStateEnum getUserCurrentBotState(long userId);
}
