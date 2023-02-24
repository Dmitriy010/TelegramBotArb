package ru.lds.telegram.cache;

import org.json.JSONObject;
import ru.lds.telegram.enums.BotState;

public interface DataCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    JSONObject addToUsersMessage(long userId, String key, String value);

    void deleteUsersMessage(long userId);

    BotState getUsersCurrentBotState(long userId);
}
