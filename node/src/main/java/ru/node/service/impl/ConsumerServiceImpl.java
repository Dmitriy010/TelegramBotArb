package ru.node.service.impl;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.node.bot.TelegramButton;
import ru.node.dto.OrderDto;
import ru.node.dto.UserActionLimitDto;
import ru.node.dto.UserActionDto;
import ru.node.dto.UserRegisterDto;
import ru.node.enums.ExchangeEnum;
import ru.node.enums.PaymentSystemEnum;
import ru.node.enums.UserActionEnum;
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
    @RabbitListener(queues = "order_info")
    public void consumeOrderInfo(OrderDto orderDto) {
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
                    producerService.producerAnswerOrderInfo(new SendMessage(orderDto.getUserId().toString(), MessageUtils.getTextOrderInfo(order))));
        } else {
            producerService.producerAnswerOrderInfo(new SendMessage(orderDto.getUserId().toString(), "Нет данных"));
        }
    }

    @Override
    @RabbitListener(queues = "create_subscribe")
    public void consumeCreateSubscribe(OrderDto orderDto) {
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
        producerService.producerAnswerCreateSubscribe(sendMessage);
    }

    @RabbitListener(queues = "subscribe")
    public void consumeSubscribe(UserActionDto subscribeActionDto) {
        if (UserActionEnum.FIND_ALL.getName().equals(subscribeActionDto.getAction())) {
            var orderSubscribes = orderSubscribeService.findAllByUserId(subscribeActionDto.getUserId());
            if (orderSubscribes.isEmpty()) {
                producerService.producerAnswerSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                        String.format("%s Мои подписки %s\n Подписок нет", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
            } else {
                producerService.producerAnswerSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                        String.format("%s Мои подписки %s", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
                orderSubscribes.forEach(orderSubscribe ->
                {
                    var sendMessage = new SendMessage(subscribeActionDto.getUserId().toString(),
                            MessageUtils.getTextOrderSubscribeForUser(orderSubscribe));
                    sendMessage.setReplyMarkup(telegramButton.getKeyBoardDeleteSubscribe(orderSubscribe.getId()));
                    producerService.producerAnswerSubscribe(sendMessage);
                });
            }
        } else if (UserActionEnum.DELETE.getName().equals(subscribeActionDto.getAction())) {
            var orderSubscribes = orderSubscribeService.findAllByUserId(subscribeActionDto.getUserId());
            if (orderSubscribes.isEmpty()) {
                producerService.producerAnswerSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                        String.format("%s Мои подписки %s\n Подписок нет", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
            } else {
                orderSubscribeService.deleteById(subscribeActionDto.getId());
                var orderSubscribesNew = orderSubscribeService.findAllByUserId(subscribeActionDto.getUserId());
                if (orderSubscribesNew.isEmpty()) {
                    producerService.producerAnswerSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                            String.format("Подписка успешно удалена.\n%s Мои подписки %s\n Подписок нет.", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
                } else {
                    producerService.producerAnswerSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                            String.format("Подписка успешно удалена.\n%s Мои подписки %s", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
                    orderSubscribesNew.forEach(orderSubscribe -> {
                        var sendMessage = new SendMessage(subscribeActionDto.getUserId().toString(),
                                MessageUtils.getTextOrderSubscribeForUser(orderSubscribe));
                        sendMessage.setReplyMarkup(telegramButton.getKeyBoardDeleteSubscribe(orderSubscribe.getId()));
                        producerService.producerAnswerSubscribe(sendMessage);
                    });
                }
            }
        } else if (UserActionEnum.DELETE_ALL.getName().equals(subscribeActionDto.getAction())) {
            orderSubscribeService.deleteAllByUserId(subscribeActionDto.getUserId());
            producerService.producerAnswerSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                    String.format("%s Все подписки удалены %s", EmojiParser.parseToUnicode("⏰⏰⏰"), EmojiParser.parseToUnicode("⏰⏰⏰"))));
        }
    }

    @Override
    @RabbitListener(queues = "exchange")
    public void consumeExchange(UserActionDto userActionDto) {
        var user = userService.findByUserId(userActionDto.getUserId());
        if (Objects.nonNull(user)) {
            switch (UserActionEnum.getByName(userActionDto.getAction())) {
                case ADD -> {
                    exchangeUserService.createByUserIdAndExchangeId(user.getId(), userActionDto.getId());
                    getUserExchanges(userActionDto);
                }
                case DELETE -> {
                    exchangeUserService.deleteByUserAndExchangeId(user, userActionDto.getId());
                    getUserExchanges(userActionDto);
                }
                case DELETE_ALL -> {
                    exchangeUserService.deleteAllByUser(user);
                    getUserExchanges(userActionDto);
                }
                case FIND_ALL -> getUserExchanges(userActionDto);
            }
        }
    }

    @Override
    @RabbitListener(queues = "payment_system")
    public void consumePaymentSystem(UserActionDto userActionDto) {
        var user = userService.findByUserId(userActionDto.getUserId());
        if (Objects.nonNull(user)) {
            switch (UserActionEnum.getByName(userActionDto.getAction())) {
                case ADD -> {
                    paymentSystemUserService.createByUserIdAndPaymentSystemId(user.getId(), userActionDto.getId());
                    getUserPaymentSystem(userActionDto);
                }
                case DELETE -> {
                    paymentSystemUserService.deleteByUserAndPaymentSystemId(user, userActionDto.getId());
                    getUserPaymentSystem(userActionDto);
                }
                case DELETE_ALL -> {
                    paymentSystemUserService.deleteAllByUser(user);
                    getUserPaymentSystem(userActionDto);
                }
                case FIND_ALL -> getUserPaymentSystem(userActionDto);
            }
        }
    }

    @Override
    @RabbitListener(queues = "register_user")
    public void consumeRegisterUser(UserRegisterDto userRegisterDto) {
        var user = userService.findByUserName(userRegisterDto.getUserName());
        if (Objects.isNull(user)) {
            var newUser = userService.create(userRegisterDto);
            var limit = limitService.findByLimit(20000L);
            limitUserService.create(new LimitUser(newUser, limit));
        }
    }

    @Override
    @RabbitListener(queues = "limit")
    public void consumeLimit(UserActionLimitDto userActionLimitDto) {
        switch (UserActionEnum.getByName(userActionLimitDto.getAction())) {
            case FIND_ALL -> getUserLimit(userActionLimitDto);
            case UPDATE -> {
                var limitUser = limitUserService.findByUserId(userActionLimitDto.getUserId());
                limitUser.setLimit(new Limit(userActionLimitDto.getLimit()));
                limitUserService.update(limitUser);
                getUserLimit(userActionLimitDto);
            }
        }
    }

    private void getUserLimit(UserActionLimitDto userActionLimitDto) {
        var limitList = limitService.findAll();
        var limitUser = List.of(limitUserService.findByUserId(userActionLimitDto.getUserId()));
        var groupLimit = Stream.concat(limitList.stream(), limitUser.stream().map(LimitUser::getLimit))
                .collect(Collectors.groupingBy(Limit::getId,
                        Collectors.mapping(Limit::getVolume, Collectors.toList())));

        var message = new SendMessage(userActionLimitDto.getUserId().toString(), "Выберите лимит");
        message.setReplyMarkup(telegramButton.getKeyBoardLimit(groupLimit));
        producerService.producerAnswerLimit(message);
    }

    private void getUserExchanges(UserActionDto userActionDto) {
        var exchangeList = exchangeService.findAll();
        var exchangeUserList = exchangeUserService.findAllByUserId(userActionDto.getUserId());
        var groupExchange = Stream.concat(exchangeList.stream(), exchangeUserList.stream().map(ExchangeUser::getExchange))
                .collect(Collectors.groupingBy(Exchange::getId,
                        Collectors.mapping(Exchange::getName, Collectors.toList())));

        var message = new SendMessage(userActionDto.getUserId().toString(), "Выберите биржи");
        message.setReplyMarkup(telegramButton.getKeyBoardExchangeOrPaymentSystem(groupExchange));
        producerService.producerAnswerExchange(message);
    }

    private void getUserPaymentSystem(UserActionDto userActionDto) {
        var paymentSystemList = paymentSystemService.findAll();
        var paymentSystemUserList = paymentSystemUserService.findAllByUserId(userActionDto.getUserId());
        var groupPaymentSystem = Stream.concat(paymentSystemList.stream(), paymentSystemUserList.stream().map(PaymentSystemUser::getPaymentSystem))
                .collect(Collectors.groupingBy(PaymentSystem::getId,
                        Collectors.mapping(PaymentSystem::getName, Collectors.toList())));

        var message = new SendMessage(userActionDto.getUserId().toString(), "Выберите платежные системамы");
        message.setReplyMarkup(telegramButton.getKeyBoardExchangeOrPaymentSystem(groupPaymentSystem));
        producerService.producerAnswerPaymentSystem(message);
    }
}
