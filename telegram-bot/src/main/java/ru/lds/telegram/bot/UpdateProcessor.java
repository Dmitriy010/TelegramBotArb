package ru.lds.telegram.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.lds.telegram.cache.DataCache;
import ru.lds.telegram.enums.BotState;
import ru.lds.telegram.enums.PaymentSystem;
import ru.lds.telegram.enums.TradeType;
import ru.lds.telegram.service.UpdateProducer;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateProcessor {

    private final DataCache dataCache;
    private final TelegramBot telegramBot;
    private final UpdateProducer updateProducer;

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {

            var chatId = update.getMessage().getChatId();
            if (update.getMessage().getText().equals("Узнать цену")) {
                dataCache.setUsersCurrentBotState(chatId, BotState.CHECK_COST);
            }


            switch (dataCache.getUsersCurrentBotState(chatId)) {
                case START -> {
                    var message = new SendMessage(chatId.toString(), "Выберите действие");
                    message.setReplyMarkup(getMenu());
                    telegramBot.sendAnswerMessage(message);
                }
                case CHECK_COST -> {
                    var message = new SendMessage();
                    message.setChatId(chatId);
                    message.setReplyMarkup(getKeyBoardPaymentSystem());
                    message.setText("Выберите платежную систему");
                    telegramBot.sendAnswerMessage(message);
                    dataCache.setUsersCurrentBotState(chatId, BotState.PAYMENT_SYSTEM);
                }
            }

        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage().hasText()) {
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            var callbackData = update.getCallbackQuery().getData();
            switch (dataCache.getUsersCurrentBotState(chatId)) {
                case PAYMENT_SYSTEM -> {
                    dataCache.setUsersMessage(chatId, PaymentSystem.valueOf(callbackData).getName());
                    var message = new SendMessage(chatId.toString(), "Выберите тип");
                    message.setReplyMarkup(getKeyBoardTradeType());
                    telegramBot.sendAnswerMessage(message);
                    dataCache.setUsersCurrentBotState(chatId, BotState.TRADE_TYPE);
                }
                case TRADE_TYPE -> {
                    var result = dataCache.setUsersMessage(chatId, TradeType.valueOf(callbackData).name());
                    update.getCallbackQuery().getMessage().setText(result);
                    updateProducer.produce("text_message_update", update);
                    dataCache.setUsersCurrentBotState(chatId, BotState.START);
                    dataCache.deleteUsersMessage(chatId);
                }
            }
        }
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private InlineKeyboardMarkup getKeyBoardPaymentSystem() {
        var tinkoffButton = new InlineKeyboardButton();
        tinkoffButton.setText(PaymentSystem.TINKOFF.getName());
        tinkoffButton.setCallbackData(PaymentSystem.TINKOFF.name());

        var raifButton = new InlineKeyboardButton();
        raifButton.setText(PaymentSystem.RAIFFEISENBANK.getName());
        raifButton.setCallbackData(PaymentSystem.RAIFFEISENBANK.name());

        var rosbankButton = new InlineKeyboardButton();
        rosbankButton.setText(PaymentSystem.ROSBANK.getName());
        rosbankButton.setCallbackData(PaymentSystem.ROSBANK.name());

        var payeerButton = new InlineKeyboardButton();
        payeerButton.setText(PaymentSystem.PAYEER.getName());
        payeerButton.setCallbackData(PaymentSystem.PAYEER.name());

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(List.of(tinkoffButton, raifButton));
        rowsInLine.add(List.of(rosbankButton, payeerButton));

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    private InlineKeyboardMarkup getKeyBoardTradeType() {
        var buyButton = new InlineKeyboardButton();
        buyButton.setText(TradeType.BUY.name());
        buyButton.setCallbackData(TradeType.BUY.name());

        var sellButton = new InlineKeyboardButton();
        sellButton.setText(TradeType.SELL.name());
        sellButton.setCallbackData(TradeType.SELL.name());

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(List.of(buyButton, sellButton));

        var markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    private ReplyKeyboardMarkup getMenu() {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        var row = new KeyboardRow();
        row.add("Узнать цену");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
