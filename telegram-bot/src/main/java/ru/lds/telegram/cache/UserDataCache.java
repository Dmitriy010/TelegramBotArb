package ru.lds.telegram.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.lds.telegram.enums.BotState;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private final Map<Long, StringBuilder> usersMessage = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public String setUsersMessage(long userId, String message) {
        if (Objects.isNull(usersMessage.get(userId))) {
            usersMessage.put(userId, new StringBuilder().append(message).append(" "));
        } else {
            usersMessage.put(userId, usersMessage.get(userId).append(message).append(" "));
        }
        return usersMessage.get(userId).toString();
    }

    @Override
    public void deleteUsersMessage(long userId) {
        usersMessage.remove(userId);
    }

    @Override
    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.START;
        }
        return botState;
    }
}

