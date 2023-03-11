package ru.lds.telegram.bot;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.lds.telegram.cache.DataCache;
import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.dto.SubscribeActionDto;
import ru.lds.telegram.dto.UserActionDto;
import ru.lds.telegram.dto.UserActionExOrPsDto;
import ru.lds.telegram.dto.UserRegisterDto;
import ru.lds.telegram.enums.Asset;
import ru.lds.telegram.enums.BotState;
import ru.lds.telegram.enums.Exchange;
import ru.lds.telegram.enums.PaymentSystem;
import ru.lds.telegram.enums.SubscribeAction;
import ru.lds.telegram.enums.TradeType;
import ru.lds.telegram.enums.UserAction;
import ru.lds.telegram.enums.UserActionExchange;
import ru.lds.telegram.enums.UserActionPaymentSystem;
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
            //Регистрация пользователя
            if (dataCache.getUserCurrentBotState(userId).equals(BotState.START)) {
                var updateMessage = update.getMessage();
                var userRegisterDto = new UserRegisterDto(
                        updateMessage.getChatId(),
                        updateMessage.getFrom().getFirstName(),
                        updateMessage.getFrom().getLastName(),
                        updateMessage.getFrom().getUserName());
                producerService.produceUserRegister("text_register_user", userRegisterDto);

                var message = new SendMessage(userId.toString(), "Выберите действие: ");
                message.setReplyMarkup(telegramButton.getMainMenu());
                telegramBot.sendAnswerMessage(message);
            }
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
            case ACTION_MY_EXCHANGES -> {
                var action = callbackData.split(",");
                producerService.produceUserActionExOrPs("text_action_user_exchange",
                        UserActionExOrPsDto.builder()
                                .action(action[0])
                                .userId(userId)
                                .id(Long.parseLong(action[1]))
                                .build());

                dataCache.setUserCurrentBotState(userId, BotState.ACTION_MY_EXCHANGES);
            }
            case ACTION_MY_PAYMENT_SYSTEM -> {
                var action = callbackData.split(",");
                producerService.produceUserActionExOrPs("text_action_user_payment_system",
                        UserActionExOrPsDto.builder()
                                .action(action[0])
                                .userId(userId)
                                .id(Long.parseLong(action[1]))
                                .build());

                dataCache.setUserCurrentBotState(userId, BotState.ACTION_MY_PAYMENT_SYSTEM);
            }
            case ACTION_MY_LIMITS -> {
                var action = callbackData.split(",");
                producerService.produceUserAction("text_action_user",
                        UserActionDto.builder()
                                .action(action[0])
                                .userId(userId)
                                .limit(Long.parseLong(action[1]))
                                .build());

                dataCache.setUserCurrentBotState(userId, BotState.ACTION_MY_LIMITS);
            }
            case ACTION_SUBSCRIBES -> {
                var action = callbackData.split(",");
                producerService.produceSubscribeAction("text_action_subscribe",
                        SubscribeActionDto.builder()
                                .action(action[0])
                                .userId(userId)
                                .subscribeId(Long.parseLong(action[1]))
                                .build());

                dataCache.setUserCurrentBotState(userId, BotState.ACTION_SUBSCRIBES);
            }
        }
    }

    private void actionWithTextMessage(Long userId, String messageText) {
        switch (dataCache.getUserCurrentBotState(userId)) {
            case SETTINGS_PROFILE -> {
                var message = new SendMessage(userId.toString(), "Выберите действие");
                message.setReplyMarkup(telegramButton.getProfileMenu());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotState.ACTION_PROFILE);
            }
            case ACTION_PROFILE, ACTION_MY_EXCHANGES, ACTION_MY_PAYMENT_SYSTEM, ACTION_MY_LIMITS -> {
                if (messageText.contains("Мои биржи")) {
                    var message = new SendMessage(userId.toString(), "Управление биржами " + EmojiParser.parseToUnicode("✨"));
                    telegramBot.sendAnswerMessage(message);
                    producerService.produceUserActionExOrPs("text_action_user_exchange",
                            UserActionExOrPsDto.builder()
                                    .action(UserActionExchange.FIND_ALL.getName())
                                    .userId(userId)
                                    .build());
                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_MY_EXCHANGES);
                } else if (messageText.contains("Мои платежные системы")) {
                    var message = new SendMessage(userId.toString(), "Управление платежными системами " + EmojiParser.parseToUnicode("💸"));
                    telegramBot.sendAnswerMessage(message);
                    producerService.produceUserActionExOrPs("text_action_user_payment_system",
                            UserActionExOrPsDto.builder()
                                    .action(UserActionPaymentSystem.FIND_ALL.getName())
                                    .userId(userId)
                                    .build());
                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_MY_PAYMENT_SYSTEM);
                } else if (messageText.contains("Мои лимиты")) {
                    var message = new SendMessage(userId.toString(), "Управление лимитами");
                    telegramBot.sendAnswerMessage(message);
                    producerService.produceUserAction("text_action_user",
                            UserActionDto.builder()
                                    .action(UserAction.FIND_ALL_LIMITS.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_MY_LIMITS);
                } else if (messageText.contains("Основное меню")) {
                    var message = new SendMessage(userId.toString(), "Выберите действие: ");
                    message.setReplyMarkup(telegramButton.getMainMenu());
                    telegramBot.sendAnswerMessage(message);
                    dataCache.setUserCurrentBotState(userId, BotState.START);
                }
            }
            //subscribe cases
            case CHECK_SUBSCRIBES -> {
                var message = new SendMessage(userId.toString(), "Выберите действие");
                message.setReplyMarkup(telegramButton.addKeyBoardsubscribe());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotState.ACTION_SUBSCRIBES);
            }
            case ACTION_SUBSCRIBES -> {
                if (messageText.contains("Мои подписки")) {
                    producerService.produceSubscribeAction("text_action_subscribe",
                            SubscribeActionDto.builder()
                                    .action(SubscribeAction.FIND_ALL.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_SUBSCRIBES);
                }
                if (messageText.contains("Удалить подписку")) {
                    var message = new SendMessage(userId.toString(),
                            "Введите идентификатор подписки или идентификаторы подписок через запятую" + EmojiParser.parseToUnicode("⏰"));
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotState.DELETE_SUBSCRIBE);
                }
                if (messageText.contains("Удалить все подписки")) {
                    producerService.produceSubscribeAction("text_action_subscribe",
                            SubscribeActionDto.builder()
                                    .action(SubscribeAction.DELETE_ALL.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_SUBSCRIBES);
                }
                if (messageText.contains("Добавить подписку")) {
                    dataCache.deleteUserMessage(userId);
                    var orderDto = new OrderDto(userId);
                    orderDto.setPrice(0.0);
                    dataCache.setCurrentUserMessage(userId, orderDto);

                    var message = new SendMessage(userId.toString(), "Выберите криптовалюту: ");
                    message.setReplyMarkup(telegramButton.getKeyBoardAssetType());
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotState.ASSET);
                }
            }
            case PRICE -> {
                try {
                    var price = Double.parseDouble(messageText);
                    var orderDto = dataCache.getCurrentUserMessage(userId);
                    orderDto.setPrice(price);
                    dataCache.setCurrentUserMessage(userId, orderDto);
                    producerService.produceOrderInfo("text_message_subscribe", orderDto);

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_SUBSCRIBES);
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
        if (messageText.contains("Подписка на цену")) {
            dataCache.setUserCurrentBotState(userId, BotState.CHECK_SUBSCRIBES);
        }
        if (messageText.contains("Настройки профиля")) {
            dataCache.deleteUserMessage(userId);
            dataCache.setUserCurrentBotState(userId, BotState.SETTINGS_PROFILE);
        }
        if (messageText.contains("Основное меню")) {
            dataCache.deleteUserMessage(userId);
            dataCache.setUserCurrentBotState(userId, BotState.START);
        }
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
