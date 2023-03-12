package ru.node.bot;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.node.enums.UserActionEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TelegramButton {

    public InlineKeyboardMarkup getKeyBoardExchangeOrPaymentSystem(Map<Long, List<String>> map) {
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        map.forEach((key, value) -> {
            var myExchangeButton = new InlineKeyboardButton();
            if (value.size() == 1) {
                myExchangeButton.setText("Добавить " + value.get(0) + EmojiParser.parseToUnicode(" ✅"));
                myExchangeButton.setCallbackData(UserActionEnum.ADD.getName() + "," + key);
            } else {
                myExchangeButton.setText("Удалить " + value.get(0) + EmojiParser.parseToUnicode(" ❌"));
                myExchangeButton.setCallbackData(UserActionEnum.DELETE.getName() + "," + key);
            }

            rowsInLine.add(List.of(myExchangeButton));
        });

        var myExchangeButton = new InlineKeyboardButton();
        myExchangeButton.setText("Удалить все " + EmojiParser.parseToUnicode(" ❌❌❌"));
        myExchangeButton.setCallbackData(UserActionEnum.DELETE_ALL.getName() + "," + "0");
        rowsInLine.add(List.of(myExchangeButton));

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    public InlineKeyboardMarkup getKeyBoardDeleteSubscribe(Long subscribeId) {
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        var deleteSubscribeButton = new InlineKeyboardButton();
        deleteSubscribeButton.setText("Удалить " + EmojiParser.parseToUnicode(" ❌"));
        deleteSubscribeButton.setCallbackData("delete," + subscribeId);
        rowsInLine.add(List.of(deleteSubscribeButton));

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    public InlineKeyboardMarkup getKeyBoardLimit(Map<Long, List<Long>> mapLimit) {
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        mapLimit.forEach((key, value) -> {
            var myLimitButton = new InlineKeyboardButton();
            if (value.size() == 1) {
                myLimitButton.setText("От " + value.get(0) + " RUB" + EmojiParser.parseToUnicode("🔄"));
                myLimitButton.setCallbackData(UserActionEnum.UPDATE.getName() + "," + key);
            } else {
                myLimitButton.setText("Текущий от " + value.get(0) + " RUB" + EmojiParser.parseToUnicode(" ✅"));
                myLimitButton.setCallbackData(UserActionEnum.UPDATE.getName() + "," + key);
            }

            rowsInLine.add(List.of(myLimitButton));
        });

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }
}
