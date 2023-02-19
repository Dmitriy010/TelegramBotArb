package ru.lds.telegram.cache;

import ru.lds.telegram.enums.BotState;

public interface DataCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    String setUsersMessage(long userId, String message);

    void deleteUsersMessage(long userId);

    BotState getUsersCurrentBotState(long userId);
}
