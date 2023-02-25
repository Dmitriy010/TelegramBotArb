package ru.lds.telegram.cache;

import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.enums.BotState;

public interface DataCache {
    void setUserCurrentBotState(long userId, BotState botState);

    void setCurrentUserMessage(long userId, OrderDto orderDto);

    OrderDto getCurrentUserMessage(long userId);

    void deleteUserMessage(long userId);

    BotState getUserCurrentBotState(long userId);
}
