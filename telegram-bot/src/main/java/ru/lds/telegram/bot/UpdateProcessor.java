package ru.lds.telegram.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.lds.telegram.cache.DataCache;
import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.dto.SubscribeActionDto;
import ru.lds.telegram.enums.Asset;
import ru.lds.telegram.enums.BotState;
import ru.lds.telegram.enums.Exchange;
import ru.lds.telegram.enums.PaymentSystem;
import ru.lds.telegram.enums.SubscribeAction;
import ru.lds.telegram.enums.TradeType;
import ru.lds.telegram.service.ProducerService;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateProcessor {

    private final DataCache dataCache;
    private final TelegramBot telegramBot;
    private final TelegramButton telegramButton;
    private final ProducerService producerService;

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Update message is null");
            return;
        }
        //Проверка тестового сообщения
        if (update.hasMessage() && update.getMessage().hasText()) {
            var userId = update.getMessage().getChatId();
            var messageText = update.getMessage().getText();

            //Смена статуса бота для пользователя
            checkStartMessageAndChangeStatus(userId, messageText);

            actionWithTextMessage(userId, messageText);
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage().hasText()) {
            actionWithCallBackButton(update);
        }
    }

    private void actionWithCallBackButton(Update update) {
        var userId = update.getCallbackQuery().getMessage().getChatId();
        var callbackData = update.getCallbackQuery().getData();
        switch (dataCache.getUserCurrentBotState(userId)) {
            case ASSET -> {
                var orderDto = dataCache.getCurrentUserMessage(userId);
                orderDto.setAsset(Asset.valueOf(callbackData).name());
                dataCache.setCurrentUserMessage(userId, orderDto);

                var message = new SendMessage(userId.toString(), "Выберите биржу: ");
                message.setReplyMarkup(telegramButton.getKeyBoardExchange());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotState.EXCHANGE);
            }
            case EXCHANGE -> {
                var orderDto = dataCache.getCurrentUserMessage(userId);
                orderDto.setExchange(Exchange.getByName(callbackData).getName());
                dataCache.setCurrentUserMessage(userId, orderDto);

                var message = new SendMessage(userId.toString(), "Выберите платежную систему: ");
                message.setReplyMarkup(telegramButton.getKeyBoardPaymentSystem());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotState.PAYMENT_SYSTEM);
            }
            case PAYMENT_SYSTEM -> {
                var orderDto = dataCache.getCurrentUserMessage(userId);
                orderDto.setPaymentSystem(PaymentSystem.valueOf(callbackData).getName());
                dataCache.setCurrentUserMessage(userId, orderDto);

                var message = new SendMessage(userId.toString(), "Выберите тип сделки: ");
                message.setReplyMarkup(telegramButton.getKeyBoardTradeType());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotState.TRADE_TYPE);
            }
            case TRADE_TYPE -> {
                var orderDto = dataCache.getCurrentUserMessage(userId);
                orderDto.setTradeType(TradeType.valueOf(callbackData).name());
                dataCache.setCurrentUserMessage(userId, orderDto);

                if (Objects.nonNull(orderDto.getPrice())) {
                    var message = new SendMessage(userId.toString(), "Введите цену для подписки: ");
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotState.PRICE);
                } else {
                    producerService.produceOrderInfo("text_message_order_info", orderDto);
                    dataCache.setUserCurrentBotState(userId, BotState.START);
                    dataCache.deleteUserMessage(userId);
                }
            }
        }
    }

    private void actionWithTextMessage(Long userId, String messageText) {
        switch (dataCache.getUserCurrentBotState(userId)) {
            case START -> {
                var message = new SendMessage(userId.toString(), "Выберите действие: ");
                message.setReplyMarkup(telegramButton.getMenu());
                telegramBot.sendAnswerMessage(message);
            }
            case SUBSCRIBE_COST -> {
                var orderDto = dataCache.getCurrentUserMessage(userId);
                orderDto.setPrice(0.0);
                dataCache.setCurrentUserMessage(userId, orderDto);

                var message = new SendMessage(userId.toString(), "Выберите криптовалюту: ");
                message.setReplyMarkup(telegramButton.getKeyBoardAssetType());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotState.ASSET);
            }
            case PRICE -> {
                try {
                    var price = Double.parseDouble(messageText);
                    var orderDto = dataCache.getCurrentUserMessage(userId);
                    orderDto.setPrice(price);
                    dataCache.setCurrentUserMessage(userId, orderDto);
                    producerService.produceOrderInfo("text_message_subscribe", orderDto);

                    dataCache.setUserCurrentBotState(userId, BotState.START);
                    dataCache.deleteUserMessage(userId);
                } catch (NumberFormatException e) {
                    telegramBot.sendAnswerMessage(new SendMessage(userId.toString(), "Введите числовое значение!"));
                }
            }
            case CHECK_COST -> {
                var message = new SendMessage(userId.toString(), "Выберите криптовалюту: ");
                message.setReplyMarkup(telegramButton.getKeyBoardAssetType());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotState.ASSET);
            }
            case SUBSCRIBES -> {
                producerService.produceSubscribeAction("text_action_subscribe",
                        SubscribeActionDto.builder()
                                .action(SubscribeAction.FIND_ALL.getName())
                                .userId(userId)
                                .build());

                dataCache.setUserCurrentBotState(userId, BotState.START);
            }
            case START_DELETE_SUBSCRIBE -> {
                var message = new SendMessage(userId.toString(), "Введите идентификатор подписки: ");
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotState.DELETE_SUBSCRIBE);
            }
            case DELETE_SUBSCRIBE -> {
                try {
                    var subscribeId = Long.parseLong(messageText);
                    producerService.produceSubscribeAction("text_action_subscribe",
                            SubscribeActionDto.builder()
                                    .action("delete")
                                    .subscribeId(subscribeId)
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.START);
                } catch (NumberFormatException e) {
                    var message = new SendMessage(userId.toString(), "Введите числовой идентификатор!");
                    telegramBot.sendAnswerMessage(message);
                }
            }
        }
    }

    private void checkStartMessageAndChangeStatus(Long userId, String messageText) {
        if (messageText.contains("/start")) {
            dataCache.deleteUserMessage(userId);
            dataCache.setUserCurrentBotState(userId, BotState.START);
        }
        if (messageText.contains("Узнать цену")) {
            dataCache.deleteUserMessage(userId);
            dataCache.setCurrentUserMessage(userId, new OrderDto(userId));
            dataCache.setUserCurrentBotState(userId, BotState.CHECK_COST);
        }
        if (messageText.contains("Подписаться на цену")) {
            dataCache.deleteUserMessage(userId);
            dataCache.setCurrentUserMessage(userId, new OrderDto(userId));
            dataCache.setUserCurrentBotState(userId, BotState.SUBSCRIBE_COST);
        }
        if (messageText.contains("Мои подписки")) {
            dataCache.deleteUserMessage(userId);
            dataCache.setCurrentUserMessage(userId, new OrderDto(userId));
            dataCache.setUserCurrentBotState(userId, BotState.SUBSCRIBES);
        }
        if (messageText.contains("Удалить подписку")) {
            dataCache.deleteUserMessage(userId);
            dataCache.setCurrentUserMessage(userId, new OrderDto(userId));
            dataCache.setUserCurrentBotState(userId, BotState.START_DELETE_SUBSCRIBE);
        }
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
