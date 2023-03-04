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
import ru.lds.telegram.dto.UserRegisterDto;
import ru.lds.telegram.enums.Asset;
import ru.lds.telegram.enums.BotState;
import ru.lds.telegram.enums.Exchange;
import ru.lds.telegram.enums.PaymentSystem;
import ru.lds.telegram.enums.SubscribeAction;
import ru.lds.telegram.enums.TradeType;
import ru.lds.telegram.enums.UserActionExchange;
import ru.lds.telegram.enums.UserActionPaymentSystem;
import ru.lds.telegram.service.ProducerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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
        }
    }

    private void actionWithTextMessage(Long userId, String messageText) {
        switch (dataCache.getUserCurrentBotState(userId)) {
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
                    var message = new SendMessage(userId.toString(), "Введите идентификатор подписки: ");
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotState.DELETE_SUBSCRIBE);
                }
                if (messageText.contains("Подписаться на цену")) {
                    dataCache.deleteUserMessage(userId);
                    var orderDto = new OrderDto(userId);
                    orderDto.setPrice(0.0);
                    dataCache.setCurrentUserMessage(userId, orderDto);

                    var message = new SendMessage(userId.toString(), "Выберите криптовалюту: ");
                    message.setReplyMarkup(telegramButton.getKeyBoardAssetType());
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotState.ASSET);
                }
                if (messageText.contains("Основное меню")) {
                    dataCache.setUserCurrentBotState(userId, BotState.START);

                    var message = new SendMessage(userId.toString(), "Выберите действие");
                    message.setReplyMarkup(telegramButton.getMainMenu());
                    telegramBot.sendAnswerMessage(message);
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
            case DELETE_SUBSCRIBE -> {
                try {
                    var subscribeId = Long.parseLong(messageText);
                    producerService.produceSubscribeAction("text_action_subscribe",
                            SubscribeActionDto.builder()
                                    .action(SubscribeAction.DELETE.getName())
                                    .subscribeId(subscribeId)
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_SUBSCRIBES);
                } catch (NumberFormatException e) {
                    var message = new SendMessage(userId.toString(), "Введите числовой идентификатор!");
                    telegramBot.sendAnswerMessage(message);
                }
            }
            //exchange cases
            case CHECK_EXCHANGES -> {
                var message = new SendMessage(userId.toString(), "Выберите действие");
                message.setReplyMarkup(telegramButton.addKeyBoardExchange());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotState.ACTION_EXCHANGES);
            }
            case ACTION_EXCHANGES -> {
                if (messageText.contains("Доступные биржи")) {
                    producerService.produceUserAction("text_action_user_exchange",
                            UserActionDto.builder()
                                    .action(UserActionExchange.FIND_ALL_EXCHANGES.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_EXCHANGES);
                }
                if (messageText.contains("Мои биржи")) {
                    producerService.produceUserAction("text_action_user_exchange",
                            UserActionDto.builder()
                                    .action(UserActionExchange.FIND_ALL_USER_EXCHANGES.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_EXCHANGES);
                }
                if (messageText.contains("Добавить биржу")) {
                    var message = new SendMessage(userId.toString(), "Введите номер или номера бирж через запятую. " +
                            "Для того чтобы узнать номера бирж, нажмите: Доступные биржи" + EmojiParser.parseToUnicode("✨"));
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotState.ADD_MY_EXCHANGES);
                }
                if (messageText.contains("Удалить биржу")) {
                    var message = new SendMessage(userId.toString(), "Введите номер или номера бирж через запятую. " +
                            "Для того чтобы узнать номера добавленных бирж, нажмите: Мои биржи" + EmojiParser.parseToUnicode("⭐"));
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotState.DELETE_MY_EXCHANGES);
                }
                if (messageText.contains("Удалить все биржи")) {
                    producerService.produceUserAction("text_action_user_exchange",
                            UserActionDto.builder()
                                    .action(UserActionExchange.DELETE_ALL_EXCHANGES.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_EXCHANGES);
                }
                if (messageText.contains("Основное меню")) {
                    dataCache.setUserCurrentBotState(userId, BotState.START);

                    var message = new SendMessage(userId.toString(), "Выберите действие");
                    message.setReplyMarkup(telegramButton.getMainMenu());
                    telegramBot.sendAnswerMessage(message);
                }
            }
            case ADD_MY_EXCHANGES -> {
                if (messageText.contains("Доступные биржи")) {
                    producerService.produceUserAction("text_action_user",
                            UserActionDto.builder()
                                    .action(UserActionExchange.FIND_ALL_EXCHANGES.getName())
                                    .userId(userId)
                                    .build());
                } else {
                    List<Long> exchageIds = new ArrayList<>();
                    var pattern = Pattern.compile("\\d+");
                    var matcher = pattern.matcher(messageText);
                    while (matcher.find()) {
                        exchageIds.add(Long.parseLong(matcher.group()));
                    }
                    if (exchageIds.isEmpty()) {
                        var message = new SendMessage(userId.toString(), "Некорректный ввод, повторите попытку");
                        telegramBot.sendAnswerMessage(message);
                    } else {
                        producerService.produceUserAction("text_action_user_exchange",
                                UserActionDto.builder()
                                        .action(UserActionExchange.ADD_EXCHANGES.getName())
                                        .userId(userId)
                                        .listIds(exchageIds)
                                        .build());

                        dataCache.setUserCurrentBotState(userId, BotState.ACTION_EXCHANGES);
                    }
                }
            }
            case DELETE_MY_EXCHANGES -> {
                if (messageText.contains("Мои биржи")) {
                    producerService.produceUserAction("text_action_user_exchange",
                            UserActionDto.builder()
                                    .action(UserActionExchange.FIND_ALL_USER_EXCHANGES.getName())
                                    .userId(userId)
                                    .build());
                } else {
                    List<Long> exchageIds = new ArrayList<>();
                    var pattern = Pattern.compile("\\d+");
                    var matcher = pattern.matcher(messageText);
                    while (matcher.find()) {
                        exchageIds.add(Long.parseLong(matcher.group()));
                    }
                    if (exchageIds.isEmpty()) {
                        var message = new SendMessage(userId.toString(), "Некорректный ввод, повторите попытку");
                        telegramBot.sendAnswerMessage(message);
                    } else {
                        producerService.produceUserAction("text_action_user_exchange",
                                UserActionDto.builder()
                                        .action(UserActionExchange.DELETE_EXCHANGES.getName())
                                        .userId(userId)
                                        .listIds(exchageIds)
                                        .build());

                        dataCache.setUserCurrentBotState(userId, BotState.ACTION_EXCHANGES);
                    }
                }
            }
            //payment systems cases
            case CHECK_PAYMENT_SYSTEMS -> {
                var message = new SendMessage(userId.toString(), "Выберите действие");
                message.setReplyMarkup(telegramButton.addKeyBoardPaymentSystem());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotState.ACTION_PAYMENT_SYSTEMS);
            }
            case ACTION_PAYMENT_SYSTEMS -> {
                if (messageText.contains("Доступные платежные системы")) {
                    producerService.produceUserAction("text_action_user_payment_system",
                            UserActionDto.builder()
                                    .action(UserActionPaymentSystem.FIND_ALL_PAYMENT_SYSTEMS.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_PAYMENT_SYSTEMS);
                }
                if (messageText.contains("Мои платежные системы")) {
                    producerService.produceUserAction("text_action_user_payment_system",
                            UserActionDto.builder()
                                    .action(UserActionPaymentSystem.FIND_ALL_USER_PAYMENT_SYSTEMS.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_PAYMENT_SYSTEMS);
                }
                if (messageText.contains("Добавить платежную систему")) {
                    var message = new SendMessage(userId.toString(), "Введите номер или номера платежных систем через запятую. " +
                            "Для того чтобы узнать номера платежных систем, нажмите: Доступные платежные системы" + EmojiParser.parseToUnicode("💸"));
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotState.ADD_MY_PAYMENT_SYSTEMS);
                }
                if (messageText.contains("Удалить платежную систему")) {
                    var message = new SendMessage(userId.toString(), "Введите номер или номера платежных систем через запятую. " +
                            "Для того чтобы узнать номера добавленных платежных систем, нажмите: Мои платежные системы" + EmojiParser.parseToUnicode("💳"));
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotState.DELETE_MY_PAYMENT_SYSTEMS);
                }
                if (messageText.contains("Удалить все платежные системы")) {
                    producerService.produceUserAction("text_action_user_payment_system",
                            UserActionDto.builder()
                                    .action(UserActionPaymentSystem.DELETE_ALL_PAYMENT_SYSTEMS.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotState.ACTION_PAYMENT_SYSTEMS);
                }
                if (messageText.contains("Основное меню")) {
                    dataCache.setUserCurrentBotState(userId, BotState.START);

                    var message = new SendMessage(userId.toString(), "Выберите действие");
                    message.setReplyMarkup(telegramButton.getMainMenu());
                    telegramBot.sendAnswerMessage(message);
                }
            }
            case ADD_MY_PAYMENT_SYSTEMS -> {
                if (messageText.contains("Доступные платежные системы")) {
                    producerService.produceUserAction("text_action_user_payment_system",
                            UserActionDto.builder()
                                    .action(UserActionPaymentSystem.FIND_ALL_PAYMENT_SYSTEMS.getName())
                                    .userId(userId)
                                    .build());
                } else {
                    List<Long> paymentSystemList = new ArrayList<>();
                    var pattern = Pattern.compile("\\d+");
                    var matcher = pattern.matcher(messageText);
                    while (matcher.find()) {
                        paymentSystemList.add(Long.parseLong(matcher.group()));
                    }
                    if (paymentSystemList.isEmpty()) {
                        var message = new SendMessage(userId.toString(), "Некорректный ввод, повторите попытку");
                        telegramBot.sendAnswerMessage(message);
                    } else {
                        producerService.produceUserAction("text_action_user_payment_system",
                                UserActionDto.builder()
                                        .action(UserActionPaymentSystem.ADD_PAYMENT_SYSTEMS.getName())
                                        .userId(userId)
                                        .listIds(paymentSystemList)
                                        .build());

                        dataCache.setUserCurrentBotState(userId, BotState.ACTION_PAYMENT_SYSTEMS);
                    }
                }
            }
            case DELETE_MY_PAYMENT_SYSTEMS -> {
                if (messageText.contains("Мои платежные системы")) {
                    producerService.produceUserAction("text_action_user_payment_system",
                            UserActionDto.builder()
                                    .action(UserActionPaymentSystem.FIND_ALL_USER_PAYMENT_SYSTEMS.getName())
                                    .userId(userId)
                                    .build());
                } else {
                    List<Long> paymentSystemList = new ArrayList<>();
                    var pattern = Pattern.compile("\\d+");
                    var matcher = pattern.matcher(messageText);
                    while (matcher.find()) {
                        paymentSystemList.add(Long.parseLong(matcher.group()));
                    }
                    if (paymentSystemList.isEmpty()) {
                        var message = new SendMessage(userId.toString(), "Некорректный ввод, повторите попытку");
                        telegramBot.sendAnswerMessage(message);
                    } else {
                        producerService.produceUserAction("text_action_user_payment_system",
                                UserActionDto.builder()
                                        .action(UserActionPaymentSystem.DELETE_PAYMENT_SYSTEMS.getName())
                                        .userId(userId)
                                        .listIds(paymentSystemList)
                                        .build());

                        dataCache.setUserCurrentBotState(userId, BotState.ACTION_PAYMENT_SYSTEMS);
                    }
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
        if (messageText.contains("Подписки")) {
            dataCache.setUserCurrentBotState(userId, BotState.CHECK_SUBSCRIBES);
        }
        if (messageText.contains("Биржи")) {
            dataCache.deleteUserMessage(userId);
            dataCache.setUserCurrentBotState(userId, BotState.CHECK_EXCHANGES);
        }
        if (messageText.contains("Платежные системы")) {
            dataCache.deleteUserMessage(userId);
            dataCache.setUserCurrentBotState(userId, BotState.CHECK_PAYMENT_SYSTEMS);
        }
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
