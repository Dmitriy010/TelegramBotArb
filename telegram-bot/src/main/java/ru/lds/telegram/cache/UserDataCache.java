package ru.lds.telegram.cache;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.lds.telegram.enums.BotState;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private final Map<Long, JSONObject> usersMessage = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public JSONObject addToUsersMessage(long userId, String key, String value) {
        if (Objects.isNull(usersMessage.get(userId))) {
            usersMessage.put(userId, new JSONObject().put(key, value));
        } else {
            usersMessage.put(userId, usersMessage.get(userId).put(key, value));
        }
        return usersMessage.get(userId);
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

