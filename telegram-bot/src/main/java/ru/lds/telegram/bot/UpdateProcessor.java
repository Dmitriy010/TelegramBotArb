package ru.lds.telegram.bot;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.lds.telegram.cache.DataCache;
import ru.lds.telegram.dto.OrderDto;
import ru.lds.telegram.dto.UserActionLimitDto;
import ru.lds.telegram.dto.UserActionDto;
import ru.lds.telegram.dto.UserRegisterDto;
import ru.lds.telegram.enums.AssetEnum;
import ru.lds.telegram.enums.BotStateEnum;
import ru.lds.telegram.enums.ExchangeEnum;
import ru.lds.telegram.enums.PaymentSystemEnum;
import ru.lds.telegram.enums.TradeTypeEnum;
import ru.lds.telegram.enums.UserActionEnum;
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
            if (dataCache.getUserCurrentBotState(userId).equals(BotStateEnum.START)) {
                var updateMessage = update.getMessage();
                var userRegisterDto = new UserRegisterDto(
                        updateMessage.getChatId(),
                        updateMessage.getFrom().getFirstName(),
                        updateMessage.getFrom().getLastName(),
                        updateMessage.getFrom().getUserName());
                producerService.produceUserRegister("register_user", userRegisterDto);

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
            case CHOOSE_ASSET -> {
                var orderDto = dataCache.getCurrentOrderDtoByUserId(userId);
                orderDto.setAsset(AssetEnum.valueOf(callbackData).name());
                dataCache.setCurrentOrderDtoByUserId(userId, orderDto);

                var message = new SendMessage(userId.toString(), "Выберите биржу: ");
                message.setReplyMarkup(telegramButton.getKeyBoardExchange());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotStateEnum.CHOOSE_EXCHANGE);
            }
            case CHOOSE_EXCHANGE -> {
                var orderDto = dataCache.getCurrentOrderDtoByUserId(userId);
                orderDto.setExchange(ExchangeEnum.getByName(callbackData).getName());
                dataCache.setCurrentOrderDtoByUserId(userId, orderDto);

                var message = new SendMessage(userId.toString(), "Выберите платежную систему: ");
                message.setReplyMarkup(telegramButton.getKeyBoardPaymentSystem());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotStateEnum.CHOOSE_PAYMENT_SYSTEM);
            }
            case CHOOSE_PAYMENT_SYSTEM -> {
                var orderDto = dataCache.getCurrentOrderDtoByUserId(userId);
                orderDto.setPaymentSystem(PaymentSystemEnum.valueOf(callbackData).getName());
                dataCache.setCurrentOrderDtoByUserId(userId, orderDto);

                var message = new SendMessage(userId.toString(), "Выберите тип сделки: ");
                message.setReplyMarkup(telegramButton.getKeyBoardTradeType());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotStateEnum.CHOOSE_TRADE_TYPE);
            }
            case CHOOSE_TRADE_TYPE -> {
                var orderDto = dataCache.getCurrentOrderDtoByUserId(userId);
                orderDto.setTradeType(TradeTypeEnum.valueOf(callbackData).name());
                dataCache.setCurrentOrderDtoByUserId(userId, orderDto);

                if (Objects.nonNull(orderDto.getPrice())) {
                    var message = new SendMessage(userId.toString(), "Введите цену для подписки: ");
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotStateEnum.CHOOSE_PRICE);
                } else {
                    producerService.produceOrderInfo("order_info", orderDto);
                    dataCache.setUserCurrentBotState(userId, BotStateEnum.START);
                    dataCache.deleteOrderDtoByUserId(userId);
                }
            }
            case ACTION_EXCHANGE -> {
                var action = callbackData.split(",");
                producerService.produceUserAction("exchange",
                        UserActionDto.builder()
                                .action(action[0])
                                .userId(userId)
                                .id(Long.parseLong(action[1]))
                                .build());

                dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_EXCHANGE);
            }
            case ACTION_PAYMENT_SYSTEM -> {
                var action = callbackData.split(",");
                producerService.produceUserAction("payment_system",
                        UserActionDto.builder()
                                .action(action[0])
                                .userId(userId)
                                .id(Long.parseLong(action[1]))
                                .build());

                dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_PAYMENT_SYSTEM);
            }
            case ACTION_LIMIT -> {
                var action = callbackData.split(",");
                producerService.produceUserActionLimit("limit",
                        UserActionLimitDto.builder()
                                .action(action[0])
                                .userId(userId)
                                .limit(Long.parseLong(action[1]))
                                .build());

                dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_LIMIT);
            }
            case ACTION_SUBSCRIBE -> {
                var action = callbackData.split(",");
                producerService.produceUserAction("subscribe",
                        UserActionDto.builder()
                                .action(action[0])
                                .userId(userId)
                                .id(Long.parseLong(action[1]))
                                .build());

                dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_SUBSCRIBE);
            }
        }
    }

    private void actionWithTextMessage(Long userId, String messageText) {
        switch (dataCache.getUserCurrentBotState(userId)) {
            case SETTINGS_PROFILE -> {
                var message = new SendMessage(userId.toString(), "Выберите действие");
                message.setReplyMarkup(telegramButton.getProfileMenu());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_PROFILE);
            }
            case ACTION_PROFILE, ACTION_EXCHANGE, ACTION_PAYMENT_SYSTEM, ACTION_LIMIT -> {
                if (messageText.contains("Мои биржи")) {
                    var message = new SendMessage(userId.toString(), "Управление биржами " + EmojiParser.parseToUnicode("✨"));
                    telegramBot.sendAnswerMessage(message);
                    producerService.produceUserAction("exchange",
                            UserActionDto.builder()
                                    .action(UserActionEnum.FIND_ALL.getName())
                                    .userId(userId)
                                    .build());
                    dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_EXCHANGE);
                } else if (messageText.contains("Мои платежные системы")) {
                    var message = new SendMessage(userId.toString(), "Управление платежными системами " + EmojiParser.parseToUnicode("💸"));
                    telegramBot.sendAnswerMessage(message);
                    producerService.produceUserAction("payment_system",
                            UserActionDto.builder()
                                    .action(UserActionEnum.FIND_ALL.getName())
                                    .userId(userId)
                                    .build());
                    dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_PAYMENT_SYSTEM);
                } else if (messageText.contains("Мои лимиты")) {
                    var message = new SendMessage(userId.toString(), "Управление лимитами");
                    telegramBot.sendAnswerMessage(message);
                    producerService.produceUserActionLimit("limit",
                            UserActionLimitDto.builder()
                                    .action(UserActionEnum.FIND_ALL.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_LIMIT);
                } else if (messageText.contains("Основное меню")) {
                    var message = new SendMessage(userId.toString(), "Выберите действие: ");
                    message.setReplyMarkup(telegramButton.getMainMenu());
                    telegramBot.sendAnswerMessage(message);
                    dataCache.setUserCurrentBotState(userId, BotStateEnum.START);
                }
            }
            //subscribe cases
            case CHECK_SUBSCRIBE -> {
                var message = new SendMessage(userId.toString(), "Выберите действие");
                message.setReplyMarkup(telegramButton.addKeyBoardSubscribe());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_SUBSCRIBE);
            }
            case ACTION_SUBSCRIBE -> {
                if (messageText.contains("Мои подписки")) {
                    producerService.produceUserAction("subscribe",
                            UserActionDto.builder()
                                    .action(UserActionEnum.FIND_ALL.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_SUBSCRIBE);
                }
                if (messageText.contains("Удалить все подписки")) {
                    producerService.produceUserAction("subscribe",
                            UserActionDto.builder()
                                    .action(UserActionEnum.DELETE_ALL.getName())
                                    .userId(userId)
                                    .build());

                    dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_SUBSCRIBE);
                }
                if (messageText.contains("Добавить подписку")) {
                    dataCache.deleteOrderDtoByUserId(userId);
                    var orderDto = new OrderDto(userId);
                    orderDto.setPrice(0.0);
                    dataCache.setCurrentOrderDtoByUserId(userId, orderDto);

                    var message = new SendMessage(userId.toString(), "Выберите криптовалюту: ");
                    message.setReplyMarkup(telegramButton.getKeyBoardAssetType());
                    telegramBot.sendAnswerMessage(message);

                    dataCache.setUserCurrentBotState(userId, BotStateEnum.CHOOSE_ASSET);
                }
            }
            case CHOOSE_PRICE -> {
                try {
                    var price = Double.parseDouble(messageText);
                    var orderDto = dataCache.getCurrentOrderDtoByUserId(userId);
                    orderDto.setPrice(price);
                    dataCache.setCurrentOrderDtoByUserId(userId, orderDto);
                    producerService.produceOrderInfo("create_subscribe", orderDto);

                    dataCache.setUserCurrentBotState(userId, BotStateEnum.ACTION_SUBSCRIBE);
                    dataCache.deleteOrderDtoByUserId(userId);
                } catch (NumberFormatException e) {
                    telegramBot.sendAnswerMessage(new SendMessage(userId.toString(), "Введите числовое значение!"));
                }
            }
            case CHECK_ORDER -> {
                var message = new SendMessage(userId.toString(), "Выберите криптовалюту: ");
                message.setReplyMarkup(telegramButton.getKeyBoardAssetType());
                telegramBot.sendAnswerMessage(message);

                dataCache.setUserCurrentBotState(userId, BotStateEnum.CHOOSE_ASSET);
            }
        }
    }

    private void checkStartMessageAndChangeStatus(Long userId, String messageText) {
        if (messageText.contains("/start")) {
            dataCache.deleteOrderDtoByUserId(userId);
            dataCache.setUserCurrentBotState(userId, BotStateEnum.START);
        }
        if (messageText.contains("Узнать цену")) {
            dataCache.deleteOrderDtoByUserId(userId);
            dataCache.setCurrentOrderDtoByUserId(userId, new OrderDto(userId));
            dataCache.setUserCurrentBotState(userId, BotStateEnum.CHECK_ORDER);
        }
        if (messageText.contains("Подписка на цену")) {
            dataCache.setUserCurrentBotState(userId, BotStateEnum.CHECK_SUBSCRIBE);
        }
        if (messageText.contains("Настройки профиля")) {
            dataCache.deleteOrderDtoByUserId(userId);
            dataCache.setUserCurrentBotState(userId, BotStateEnum.SETTINGS_PROFILE);
        }
        if (messageText.contains("Основное меню")) {
            dataCache.deleteOrderDtoByUserId(userId);
            dataCache.setUserCurrentBotState(userId, BotStateEnum.START);
        }
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
