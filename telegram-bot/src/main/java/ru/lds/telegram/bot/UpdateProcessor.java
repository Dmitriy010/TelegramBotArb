package ru.lds.telegram.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.lds.telegram.cache.DataCache;
import ru.lds.telegram.dto.OrderInfoDto;
import ru.lds.telegram.dto.OrderSubscribeDto;
import ru.lds.telegram.dto.SubscribeActionDto;
import ru.lds.telegram.enums.Asset;
import ru.lds.telegram.enums.BotState;
import ru.lds.telegram.enums.Exchange;
import ru.lds.telegram.enums.PaymentSystem;
import ru.lds.telegram.enums.TradeType;
import ru.lds.telegram.service.UpdateProducer;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateProcessor {

    private final DataCache dataCache;
    private final TelegramBot telegramBot;
    private final TelegramButton telegramButton;
    private final UpdateProducer updateProducer;

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            //Смена статуса бота для пользователя
            var userId = update.getMessage().getChatId();
            if (update.getMessage().getText().contains("Узнать цену")) {
                dataCache.setUsersCurrentBotState(userId, BotState.CHECK_COST);
            }
            if (update.getMessage().getText().contains("Подписаться на цену")) {
                dataCache.setUsersCurrentBotState(userId, BotState.SUBSCRIBE_COST);
            }
            if (update.getMessage().getText().contains("Мои подписки")) {
                dataCache.setUsersCurrentBotState(userId, BotState.SUBSCRIBES);
            }
            if (update.getMessage().getText().contains("Удалить подписку")) {
                dataCache.setUsersCurrentBotState(userId, BotState.START_DELETE_SUBSCRIBE);
            }

            switch (dataCache.getUsersCurrentBotState(userId)) {
                case START -> {
                    var message = new SendMessage(userId.toString(), "Выберите действие:");
                    message.setReplyMarkup(telegramButton.getMenu());
                    telegramBot.sendAnswerMessage(message);
                }
                case SUBSCRIBE_COST -> {
                    dataCache.addToUsersMessage(userId, "price", "0");
                    var message = new SendMessage();
                    message.setChatId(userId);
                    message.setReplyMarkup(telegramButton.getKeyBoardAssetType());
                    message.setText("Выберите криптовалюту:");
                    telegramBot.sendAnswerMessage(message);
                    dataCache.setUsersCurrentBotState(userId, BotState.ASSET);
                }
                case PRICE -> {
                    try {
                        var price = Double.parseDouble(update.getMessage().getText());
                        var result = dataCache.addToUsersMessage(userId, "price", Double.toString(price));
                        OrderSubscribeDto orderSubscribeDto = null;
                        try {
                            orderSubscribeDto = new ObjectMapper().readValue(result.toString(), OrderSubscribeDto.class);
                            orderSubscribeDto.setUserId(userId);
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage(), e);
                        }
                        if (Objects.nonNull(orderSubscribeDto)) {
                            updateProducer.produce("text_message_subscribe", orderSubscribeDto);
                        } else {
                            telegramBot.sendAnswerMessage(new SendMessage(userId.toString(), "Произошла внутренняя ошибка!"));
                        }
                        dataCache.setUsersCurrentBotState(userId, BotState.START);
                        dataCache.deleteUsersMessage(userId);
                    } catch (NumberFormatException e) {
                        telegramBot.sendAnswerMessage(new SendMessage(userId.toString(), "Введите числовое значение!"));
                    }
                }
                case CHECK_COST -> {
                    var message = new SendMessage();
                    message.setChatId(userId);
                    message.setReplyMarkup(telegramButton.getKeyBoardAssetType());
                    message.setText("Выберите криптовалюту:");
                    telegramBot.sendAnswerMessage(message);
                    dataCache.setUsersCurrentBotState(userId, BotState.ASSET);
                }
                case SUBSCRIBES -> {
                    updateProducer.produceSubscribeAction("text_action_subscribe",
                            SubscribeActionDto.builder()
                                    .action("findAll")
                                    .userId(userId)
                                    .build());
                    dataCache.setUsersCurrentBotState(userId, BotState.START);
                }
                case START_DELETE_SUBSCRIBE -> {
                    var message = new SendMessage();
                    message.setChatId(userId);
                    message.setText("Введите идентификатор подписки: ");
                    telegramBot.sendAnswerMessage(message);
                    dataCache.setUsersCurrentBotState(userId, BotState.DELETE_SUBSCRIBE);
                }
                case DELETE_SUBSCRIBE -> {
                    try {
                        var subscribeId = Long.parseLong(update.getMessage().getText());
                        updateProducer.produceSubscribeAction("text_action_subscribe",
                                SubscribeActionDto.builder()
                                        .action("delete")
                                        .subscribeId(subscribeId)
                                        .userId(userId)
                                        .build());
                        dataCache.setUsersCurrentBotState(userId, BotState.START);
                    } catch (NumberFormatException e) {
                        var message = new SendMessage();
                        message.setChatId(userId);
                        message.setText("Введите числовой идентификатор!");
                        telegramBot.sendAnswerMessage(message);
                    }
                }
            }
            //Ответы с кнопок
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage().hasText()) {
            var userId = update.getCallbackQuery().getMessage().getChatId();
            var callbackData = update.getCallbackQuery().getData();
            switch (dataCache.getUsersCurrentBotState(userId)) {
                case ASSET -> {
                    dataCache.addToUsersMessage(userId, "asset", Asset.valueOf(callbackData).name());
                    var message = new SendMessage(userId.toString(), "Выберите биржу:");
                    message.setReplyMarkup(telegramButton.getKeyBoardExchange());
                    telegramBot.sendAnswerMessage(message);
                    dataCache.setUsersCurrentBotState(userId, BotState.EXCHANGE);
                }
                case EXCHANGE -> {
                    dataCache.addToUsersMessage(userId, "exchange", Exchange.getByName(callbackData).getName());
                    var message = new SendMessage(userId.toString(), "Выберите платежную систему:");
                    message.setReplyMarkup(telegramButton.getKeyBoardPaymentSystem());
                    telegramBot.sendAnswerMessage(message);
                    dataCache.setUsersCurrentBotState(userId, BotState.PAYMENT_SYSTEM);
                }
                case PAYMENT_SYSTEM -> {
                    dataCache.addToUsersMessage(userId, "paymentSystem", PaymentSystem.valueOf(callbackData).getName());
                    var message = new SendMessage(userId.toString(), "Выберите тип сделки:");
                    message.setReplyMarkup(telegramButton.getKeyBoardTradeType());
                    telegramBot.sendAnswerMessage(message);
                    dataCache.setUsersCurrentBotState(userId, BotState.TRADE_TYPE);
                }
                case TRADE_TYPE -> {
                    var result = dataCache.addToUsersMessage(userId, "tradeType", TradeType.valueOf(callbackData).name());
                    try {
                        result.get("price");
                        var message = new SendMessage(userId.toString(), "Введите цену для подписки: ");
                        telegramBot.sendAnswerMessage(message);
                        dataCache.setUsersCurrentBotState(userId, BotState.PRICE);
                    } catch (JSONException e) {
                        OrderInfoDto orderInfoDto = null;
                        try {
                            orderInfoDto = new ObjectMapper().readValue(result.toString(), OrderInfoDto.class);
                            orderInfoDto.setUserId(userId);
                        } catch (JsonProcessingException ex) {
                            log.error(ex.getMessage(), ex);
                        }
                        if (Objects.nonNull(orderInfoDto)) {
                            updateProducer.produce("text_message_update", orderInfoDto);
                        } else {
                            telegramBot.sendAnswerMessage(new SendMessage(userId.toString(), "Произошла внутренняя ошибка!"));
                        }
                        dataCache.setUsersCurrentBotState(userId, BotState.START);
                        dataCache.deleteUsersMessage(userId);
                    }
                }
            }
        }

    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
