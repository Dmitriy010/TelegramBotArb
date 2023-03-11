package ru.node.service.impl;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.node.bot.TelegramButton;
import ru.node.dto.OrderDto;
import ru.node.dto.SubscribeActionDto;
import ru.node.dto.UserActionDto;
import ru.node.dto.UserActionExOrPsDto;
import ru.node.dto.UserRegisterDto;
import ru.node.enums.ExchangeEnum;
import ru.node.enums.PaymentSystemEnum;
import ru.node.enums.SubscribeActionEnum;
import ru.node.enums.UserActionEnum;
import ru.node.enums.UserActionExEnum;
import ru.node.enums.UserActionPSEnum;
import ru.node.model.Exchange;
import ru.node.model.ExchangeUser;
import ru.node.model.Limit;
import ru.node.model.LimitUser;
import ru.node.model.PaymentSystem;
import ru.node.model.PaymentSystemUser;
import ru.node.repository.specification.OrderSpecification;
import ru.node.service.ConsumerService;
import ru.node.service.ExchangeService;
import ru.node.service.ExchangeUserService;
import ru.node.service.LimitService;
import ru.node.service.LimitUserService;
import ru.node.service.OrderService;
import ru.node.service.OrderSubscribeService;
import ru.node.service.PaymentSystemService;
import ru.node.service.PaymentSystemUserService;
import ru.node.service.ProducerService;
import ru.node.service.UserService;
import ru.node.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final ProducerService producerService;
    private final OrderService orderService;
    private final OrderSubscribeService orderSubscribeService;
    private final LimitService limitService;
    private final LimitUserService limitUserService;
    private final ExchangeService exchangeService;
    private final ExchangeUserService exchangeUserService;
    private final PaymentSystemService paymentSystemService;
    private final PaymentSystemUserService paymentSystemUserService;
    private final UserService userService;
    private final TelegramButton telegramButton;

    @Override
    @RabbitListener(queues = "text_message_order_info")
    public void consumeTextMessageOrderInfo(OrderDto orderDto) {
        List<String> exchanges = new ArrayList<>();
        List<String> paymentSystems = new ArrayList<>();

        if (ExchangeEnum.MY_EXCHANGE.getName().equals(orderDto.getExchange())) {
            var exchangeList = exchangeUserService.findAllByUserId(orderDto.getUserId()).stream()
                    .map(ExchangeUser::getExchange)
                    .map(Exchange::getName)
                    .toList();
            exchanges.addAll(exchangeList);
        } else {
            exchanges.add(orderDto.getExchange());
        }

        if (PaymentSystemEnum.MY_PAYMENT_SYSTEM.getName().equals(orderDto.getPaymentSystem())) {
            var paymentSystemsList = paymentSystemUserService.findAllByUserId(orderDto.getUserId()).stream()
                    .map(exchangeUser -> exchangeUser.getPaymentSystem().getName())
                    .toList();
            paymentSystems.addAll(paymentSystemsList);
        } else {
            paymentSystems.add(orderDto.getPaymentSystem());
        }

        var limit = limitUserService.findByUserId(orderDto.getUserId()).getLimit().getVolume().doubleValue();

        var orderList = orderService.findAll(OrderSpecification.getFilterOrderCheckPrice(
                orderDto.getTradeType(),
                orderDto.getAsset(),
                limit,
                exchanges,
                paymentSystems));

        if (!orderList.isEmpty()) {
            orderList.forEach(order ->
                    producerService.producerAnswerSubscribe(new SendMessage(orderDto.getUserId().toString(), MessageUtils.getTextOrderInfo(order))));
        } else {
            producerService.producerAnswerSubscribe(new SendMessage(orderDto.getUserId().toString(), "Нет данных"));
        }
    }

    @Override
    @RabbitListener(queues = "text_message_subscribe")
    public void consumeTextMessageSubscribe(OrderDto orderDto) {
        if (ExchangeEnum.MY_EXCHANGE.getName().equals(orderDto.getExchange())) {
            var exchangeList = exchangeUserService.findAllByUserId(orderDto.getUserId()).stream()
                    .map(ExchangeUser::getExchange)
                    .toList();
            if (exchangeList.isEmpty()) {
                orderDto.setExchange(ExchangeEnum.ANY.getName());
            } else {
                var exchangeResult = exchangeList.stream().map(Exchange::getName).collect(Collectors.joining(","));
                orderDto.setExchange(exchangeResult);
            }
        }

        if (PaymentSystemEnum.MY_PAYMENT_SYSTEM.getName().equals(orderDto.getPaymentSystem())) {
            var paymentSystemList = paymentSystemUserService.findAllByUserId(orderDto.getUserId()).stream()
                    .map(PaymentSystemUser::getPaymentSystem)
                    .toList();
            if (paymentSystemList.isEmpty()) {
                orderDto.setPaymentSystem(PaymentSystemEnum.ANY.getName());
            } else {
                var paymentSystemResult = paymentSystemList.stream().map(PaymentSystem::getName).collect(Collectors.joining(","));
                orderDto.setPaymentSystem(paymentSystemResult);
            }
        }
        var limit = limitUserService.findByUserId(orderDto.getUserId()).getLimit().getVolume().doubleValue();

        var orderSubscribe = orderSubscribeService.create(orderDto, limit);
        var sendMessage = new SendMessage(orderDto.getUserId().toString(), MessageUtils.getTextOrderSubscribe(orderSubscribe));
        producerService.producerAnswer(sendMessage);
    }

    @Override
    @RabbitListener(queues = "text_action_subscribe")
    public void consumeTextActionSubscribe(SubscribeActionDto subscribeActionDto) {
        if (SubscribeActionEnum.FIND_ALL.getName().equals(subscribeActionDto.getAction())) {
            var orderSubscribes = orderSubscribeService.findAllByUserId(subscribeActionDto.getUserId());
            if (orderSubscribes.isEmpty()) {
                producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                        String.format("%s Мои подписки %s\n Подписок нет", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
            } else {
                producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                        String.format("%s Мои подписки %s", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
                orderSubscribes.forEach(orderSubscribe ->
                {
                    var sendMessage = new SendMessage(subscribeActionDto.getUserId().toString(),
                            MessageUtils.getTextOrderSubscribeForUser(orderSubscribe));
                    sendMessage.setReplyMarkup(telegramButton.getKeyBoardDeleteSubscribe(orderSubscribe.getId()));
                    producerService.producerAnswerActionSubscribe(sendMessage);
                });
            }
        } else if (SubscribeActionEnum.DELETE.getName().equals(subscribeActionDto.getAction())) {
            var orderSubscribes = orderSubscribeService.findAllByUserId(subscribeActionDto.getUserId());
            if (orderSubscribes.isEmpty()) {
                producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                        String.format("%s Мои подписки %s\n Подписок нет", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
            } else {
                orderSubscribeService.deleteById(subscribeActionDto.getSubscribeId());
                var orderSubscribesNew = orderSubscribeService.findAllByUserId(subscribeActionDto.getUserId());
                if (orderSubscribesNew.isEmpty()) {
                    producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                            String.format("Подписка успешно удалена.\n%s Мои подписки %s\n Подписок нет.", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
                } else {
                    producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                            String.format("Подписка успешно удалена.\n%s Мои подписки %s", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
                    orderSubscribesNew.forEach(orderSubscribe -> {
                        var sendMessage = new SendMessage(subscribeActionDto.getUserId().toString(),
                                MessageUtils.getTextOrderSubscribeForUser(orderSubscribe));
                        sendMessage.setReplyMarkup(telegramButton.getKeyBoardDeleteSubscribe(orderSubscribe.getId()));
                        producerService.producerAnswerActionSubscribe(sendMessage);
                    });
                }
            }
        } else if (SubscribeActionEnum.DELETE_ALL.getName().equals(subscribeActionDto.getAction())) {
            orderSubscribeService.deleteAllByUserId(subscribeActionDto.getUserId());
            producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                    String.format("%s Все подписки удалены %s", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
        }
    }

    @Override
    @RabbitListener(queues = "text_action_user_exchange")
    public void consumeTextActionUserExchange(UserActionExOrPsDto userActionExOrPsDto) {
        var user = userService.findByUserId(userActionExOrPsDto.getUserId());
        if (Objects.nonNull(user)) {
            switch (UserActionExEnum.getByName(userActionExOrPsDto.getAction())) {
                case ADD -> {
                    exchangeUserService.createByUserIdAndExchangeId(user.getId(), userActionExOrPsDto.getId());
                    getUserExchanges(userActionExOrPsDto);
                }
                case DELETE -> {
                    exchangeUserService.deleteByUserAndExchangeId(user, userActionExOrPsDto.getId());
                    getUserExchanges(userActionExOrPsDto);
                }
                case DELETE_ALL -> {
                    exchangeUserService.deleteAllByUser(user);
                    getUserExchanges(userActionExOrPsDto);
                }
                case FIND_ALL -> getUserExchanges(userActionExOrPsDto);
            }
        }
    }

    @Override
    @RabbitListener(queues = "text_action_user_payment_system")
    public void consumeTextActionUserPaymentSystem(UserActionExOrPsDto userActionExOrPsDto) {
        var user = userService.findByUserId(userActionExOrPsDto.getUserId());
        if (Objects.nonNull(user)) {
            switch (UserActionPSEnum.getByName(userActionExOrPsDto.getAction())) {
                case ADD -> {
                    paymentSystemUserService.createByUserIdAndPaymentSystemId(user.getId(), userActionExOrPsDto.getId());
                    getUserPaymentSystem(userActionExOrPsDto);
                }
                case DELETE -> {
                    paymentSystemUserService.deleteByUserAndPaymentSystemId(user, userActionExOrPsDto.getId());
                    getUserPaymentSystem(userActionExOrPsDto);
                }
                case DELETE_ALL -> {
                    paymentSystemUserService.deleteAllByUser(user);
                    getUserPaymentSystem(userActionExOrPsDto);
                }
                case FIND_ALL -> getUserPaymentSystem(userActionExOrPsDto);
            }
        }
    }

    @Override
    @RabbitListener(queues = "text_register_user")
    public void consumeTextRegisterUser(UserRegisterDto userRegisterDto) {
        var user = userService.findByUserName(userRegisterDto.getUserName());
        if (Objects.isNull(user)) {
            var newUser = userService.create(userRegisterDto);
            var limit = limitService.findByLimit(20000L);
            limitUserService.create(new LimitUser(newUser, limit));
        }
    }

    @Override
    @RabbitListener(queues = "text_action_user")
    public void consumeTextActionUser(UserActionDto userActionDto) {
        switch (UserActionEnum.getByName(userActionDto.getAction())) {
            case FIND_ALL_LIMITS -> getUserLimit(userActionDto);
            case UPDATE_LIMITS -> {
                var limitUser = limitUserService.findByUserId(userActionDto.getUserId());
                limitUser.setLimit(new Limit(userActionDto.getLimit()));
                limitUserService.update(limitUser);
                getUserLimit(userActionDto);
            }
        }
    }

    private void getUserLimit(UserActionDto userActionDto) {
        var limitList = limitService.findAll();
        var limitUser = List.of(limitUserService.findByUserId(userActionDto.getUserId()));
        var groupLimit = Stream.concat(limitList.stream(), limitUser.stream().map(LimitUser::getLimit))
                .collect(Collectors.groupingBy(Limit::getId,
                        Collectors.mapping(Limit::getVolume, Collectors.toList())));

        var message = new SendMessage(userActionDto.getUserId().toString(), "Выберите лимит");
        message.setReplyMarkup(telegramButton.getKeyBoardLimit(groupLimit));
        producerService.producerAnswerActionUser(message);
    }

    private void getUserExchanges(UserActionExOrPsDto userActionExOrPsDto) {
        var exchangeList = exchangeService.findAll();
        var exchangeUserList = exchangeUserService.findAllByUserId(userActionExOrPsDto.getUserId());
        var groupExchange = Stream.concat(exchangeList.stream(), exchangeUserList.stream().map(ExchangeUser::getExchange))
                .collect(Collectors.groupingBy(Exchange::getId,
                        Collectors.mapping(Exchange::getName, Collectors.toList())));

        var message = new SendMessage(userActionExOrPsDto.getUserId().toString(), "Выберите биржи");
        message.setReplyMarkup(telegramButton.getKeyBoardUpdate(groupExchange));
        producerService.producerAnswerActionUserExchange(message);
    }

    private void getUserPaymentSystem(UserActionExOrPsDto userActionExOrPsDto) {
        var paymentSystemList = paymentSystemService.findAll();
        var paymentSystemUserList = paymentSystemUserService.findAllByUserId(userActionExOrPsDto.getUserId());
        var groupPaymentSystem = Stream.concat(paymentSystemList.stream(), paymentSystemUserList.stream().map(PaymentSystemUser::getPaymentSystem))
                .collect(Collectors.groupingBy(PaymentSystem::getId,
                        Collectors.mapping(PaymentSystem::getName, Collectors.toList())));

        var message = new SendMessage(userActionExOrPsDto.getUserId().toString(), "Выберите платежные системамы");
        message.setReplyMarkup(telegramButton.getKeyBoardUpdate(groupPaymentSystem));
        producerService.producerAnswerActionUserPaymentSystem(message);
    }
}
