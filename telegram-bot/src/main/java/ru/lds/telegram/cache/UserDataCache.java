package ru.lds.telegram.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.enums.BotStateEnum;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserDataCache implements DataCache {
    private final Map<Long, BotStateEnum> usersBotStates = new HashMap<>();
    private final Map<Long, OrderDto> usersMessages = new HashMap<>();

    @Override
    public void setUserCurrentBotState(long userId, BotStateEnum botStateEnum) {
        usersBotStates.put(userId, botStateEnum);
    }

    @Override
    public void setCurrentOrderDtoByUserId(long userId, OrderDto orderDto) {
        usersMessages.put(userId, orderDto);
    }

    @Override
    public OrderDto getCurrentOrderDtoByUserId(long userId) {
        return usersMessages.get(userId);
    }

    @Override
    public void deleteOrderDtoByUserId(long userId) {
        usersMessages.remove(userId);
    }

    @Override
    public BotStateEnum getUserCurrentBotState(long userId) {
        BotStateEnum botStateEnum = usersBotStates.get(userId);
        if (botStateEnum == null) {
            botStateEnum = BotStateEnum.START;
        }
        return botStateEnum;
    }
}

