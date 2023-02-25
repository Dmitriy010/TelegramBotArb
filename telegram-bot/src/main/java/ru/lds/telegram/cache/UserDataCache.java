package ru.lds.telegram.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.enums.BotState;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private final Map<Long, OrderDto> usersMessages = new HashMap<>();

    @Override
    public void setUserCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public void setCurrentUserMessage(long userId, OrderDto orderDto) {
        usersMessages.put(userId, orderDto);
    }

    @Override
    public OrderDto getCurrentUserMessage(long userId) {
        return usersMessages.get(userId);
    }

    @Override
    public void deleteUserMessage(long userId) {
        usersMessages.remove(userId);
    }

    @Override
    public BotState getUserCurrentBotState(long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.START;
        }
        return botState;
    }
}

