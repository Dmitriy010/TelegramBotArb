package ru.node.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.node.dto.OrderDto;
import ru.node.dto.SubscribeActionDto;
import ru.node.dto.UserActionDto;
import ru.node.dto.UserRegisterDto;
import ru.node.enums.ExchangeEnum;
import ru.node.enums.PaymentSystemEnum;
import ru.node.enums.SubscribeActionEnum;
import ru.node.enums.UserActionExnEnum;
import ru.node.enums.UserActionPSEnum;
import ru.node.model.Exchange;
import ru.node.model.ExchangeUser;
import ru.node.model.PaymentSystem;
import ru.node.model.PaymentSystemUser;
import ru.node.model.User;
import ru.node.repository.specification.OrderSpecification;
import ru.node.service.ConsumerService;
import ru.node.service.ExchangeService;
import ru.node.service.ExchangeUserService;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final ProducerService producerService;
    private final OrderService orderService;
    private final OrderSubscribeService orderSubscribeService;
    private final ExchangeService exchangeService;
    private final ExchangeUserService exchangeUserService;
    private final PaymentSystemService paymentSystemService;
    private final PaymentSystemUserService paymentSystemUserService;
    private final UserService userService;

    @Override
    @RabbitListener(queues = "text_message_order_info")
    public void consumeTextMessageOrderInfo(OrderDto orderDto) {
        List<String> exchanges = null;
        List<String> paymentSystems = null;

        if (ExchangeEnum.MY_EXCHANGE.getName().equals(orderDto.getExchange())) {
            var user = userService.findByUserId(orderDto.getUserId());
            if (Objects.nonNull(user)) {
                exchanges = exchangeUserService.findAllByUserId(user.getId()).stream()
                        .map(exchangeUser -> exchangeUser.getExchange().getName())
                        .toList();
            }
        } else {
            exchanges = List.of(orderDto.getExchange());
        }

        if (PaymentSystemEnum.MY_PAYMENT_SYSTEM.getName().equals(orderDto.getPaymentSystem())) {
            var user = userService.findByUserId(orderDto.getUserId());
            if (Objects.nonNull(user)) {
                paymentSystems = paymentSystemUserService.findAllByUserId(user.getId()).stream()
                        .map(exchangeUser -> exchangeUser.getPaymentSystem().getName())
                        .toList();
            }
        } else {
            paymentSystems = List.of(orderDto.getPaymentSystem());
        }

        if (Objects.isNull(exchanges) || Objects.isNull(paymentSystems)) {
            producerService.producerAnswer(new SendMessage(orderDto.getUserId().toString(), "Пользователь не найден"));
        } else {
            var orderList = orderService.findAll(OrderSpecification.getFilterOrderCheckPrice(
                    orderDto.getTradeType(),
                    orderDto.getAsset(),
                    exchanges,
                    paymentSystems));

            if (!orderList.isEmpty()) {
                orderList.forEach(order ->
                        producerService.producerAnswerSubscribe(new SendMessage(orderDto.getUserId().toString(), MessageUtils.getTextOrderInfo(order))));
            } else {
                producerService.producerAnswerSubscribe(new SendMessage(orderDto.getUserId().toString(), "Нет данных"));
            }
        }
    }

    @Override
    @RabbitListener(queues = "text_message_subscribe")
    public void consumeTextMessageSubscribe(OrderDto orderDto) {

        if (ExchangeEnum.MY_EXCHANGE.getName().equals(orderDto.getExchange())) {
            var user = userService.findByUserId(orderDto.getUserId());
            if (Objects.nonNull(user)) {
               var exchanges = exchangeUserService.findAllByUserId(user.getId()).stream()
                        .map(exchangeUser -> exchangeUser.getExchange().getName())
                       .collect(Collectors.joining(","));
               orderDto.setExchange(exchanges);
            }
        }

        if (PaymentSystemEnum.MY_PAYMENT_SYSTEM.getName().equals(orderDto.getPaymentSystem())) {
            var user = userService.findByUserId(orderDto.getUserId());
            if (Objects.nonNull(user)) {
                var paymentSystems = paymentSystemUserService.findAllByUserId(user.getId()).stream()
                        .map(exchangeUser -> exchangeUser.getPaymentSystem().getName())
                        .collect(Collectors.joining(","));
                orderDto.setPaymentSystem(paymentSystems);
            }
        }

        var orderSubscribe = orderSubscribeService.create(orderDto);
        var sendMessage = new SendMessage(orderDto.getUserId().toString(), MessageUtils.getTextOrderSubscribe(orderSubscribe));
        producerService.producerAnswer(sendMessage);
    }

    @Override
    @RabbitListener(queues = "text_action_subscribe")
    public void consumeTextActionSubscribe(SubscribeActionDto subscribeActionDto) {
        if (SubscribeActionEnum.FIND_ALL.getName().equals(subscribeActionDto.getAction())) {
            var orderSubscribes = orderSubscribeService.findAllByUserId(subscribeActionDto.getUserId());
            if (orderSubscribes.isEmpty()) {
                producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(), "Подписок нет"));
            } else {
                orderSubscribes.forEach(orderSubscribe ->
                        producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                                MessageUtils.getTextOrderSubscribeForUser(orderSubscribe))));
            }
        } else if (SubscribeActionEnum.DELETE.getName().equals(subscribeActionDto.getAction())) {
            var result = orderSubscribeService.deleteById(subscribeActionDto.getSubscribeId());
            if (Boolean.TRUE.equals(result)) {
                producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                        String.format("Подписка с идентификатором %s удалена", subscribeActionDto.getSubscribeId())));
            } else {
                producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                        String.format("Подписки с идентификатором %s не существует", subscribeActionDto.getSubscribeId())));
            }
        }
    }

    @Override
    @RabbitListener(queues = "text_action_user_exchange")
    public void consumeTextActionUserExchange(UserActionDto userActionDto) {
        if (UserActionExnEnum.FIND_ALL_EXCHANGES.getName().equals(userActionDto.getAction())) {
            var exchangeList = exchangeService.findAll();
            if (exchangeList.isEmpty()) {
                producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                        "Доступных бирж не найдено"));
            } else {
                var result = new StringBuilder();
                exchangeList.forEach(exchange -> result.append(exchange.getId())
                        .append(". ")
                        .append(exchange.getName())
                        .append("\n"));
                producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                        result.toString()));
            }
        } else if (UserActionExnEnum.ADD_EXCHANGES.getName().equals(userActionDto.getAction())) {
            var user = userService.findByUserId(userActionDto.getUserId());
            if (Objects.isNull(user)) {
                producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                        "Необходимо пройти регистрацию"));
            } else {
                var listIds = userActionDto.getListIds();
                var exchangeList = exchangeService.findAllByIds(listIds);
                if (exchangeList.isEmpty()) {
                    producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                            "Бирж с такими номерами не найдено, проверьте корректность ввода и повторите попытку."));
                } else {
                    var userExchangesFromBd = exchangeUserService.findAllByUserId(user.getId())
                            .stream()
                            .map(exchangeUser -> exchangeUser.getExchange().getId())
                            .toList();

                    var exchangeIdListNew = exchangeList.stream().map(Exchange::getId).collect(Collectors.toList());
                    exchangeIdListNew.removeIf(userExchangesFromBd::contains);

                    if (exchangeIdListNew.isEmpty()) {
                        producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                                "Биржи уже добавлены"));
                    } else {
                        List<ExchangeUser> exchangeUserList = new ArrayList<>();
                        exchangeIdListNew.forEach(exchange -> exchangeUserList.add(new ExchangeUser(
                                null,
                                new Exchange(exchange),
                                new User(user.getId()))));
                        exchangeUserService.create(exchangeUserList);
                        producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                                "Биржи успешно добавлены"));
                    }
                }
            }
        } else if (UserActionExnEnum.FIND_ALL_USER_EXCHANGES.getName().equals(userActionDto.getAction())) {
            var user = userService.findByUserId(userActionDto.getUserId());
            if (Objects.isNull(user)) {
                producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                        "Необходимо пройти регистрацию"));
            } else {
                var exchangeList = exchangeUserService.findAllByUserId(user.getId()).stream()
                        .map(ExchangeUser::getExchange)
                        .toList();

                if (!exchangeList.isEmpty()) {
                    var result = new StringBuilder();
                    exchangeList.forEach(exchange -> result
                            .append(exchange.getId())
                            .append(". ")
                            .append(exchange.getName())
                            .append("\n"));
                    producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                            result.toString()));
                } else {
                    producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                            "Бирж не добавлено"));
                }
            }
        } else if (UserActionExnEnum.DELETE_EXCHANGES.getName().equals(userActionDto.getAction())) {
            var user = userService.findByUserId(userActionDto.getUserId());
            if (Objects.isNull(user)) {
                producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                        "Необходимо пройти регистрацию"));
            } else {
                var listIds = userActionDto.getListIds();
                var exchangeList = exchangeService.findAllByIds(listIds);
                if (exchangeList.isEmpty()) {
                    producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                            "Бирж с такими номерами не найдено, проверьте корректность ввода и повторите попытку."));
                } else {
                    var userExchangesFromBd = exchangeUserService.findAllByUserId(user.getId())
                            .stream()
                            .map(exchangeUser -> exchangeUser.getExchange().getId())
                            .toList();

                    var exchangeIdListNew = exchangeList.stream()
                            .filter(e -> userExchangesFromBd.contains(e.getId()))
                            .collect(Collectors.toList());

                    exchangeUserService.deleteByExchangesAndUser(user, exchangeIdListNew);
                    producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                            "Биржи успешно удалены"));
                }
            }
        } else if (UserActionExnEnum.DELETE_ALL_EXCHANGES.getName().equals(userActionDto.getAction())) {
            var user = userService.findByUserId(userActionDto.getUserId());
            if (Objects.isNull(user)) {
                producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                        "Необходимо пройти регистрацию"));
            } else {
                exchangeUserService.deleteAllByUser(user);
                producerService.producerAnswerActionUserExchange(new SendMessage(userActionDto.getUserId().toString(),
                        "Все биржы успешно удалены"));
            }
        }
    }

    @Override
    @RabbitListener(queues = "text_action_user_payment_system")
    public void consumeTextActionUserPaymentSystem(UserActionDto userActionDto) {
        if (UserActionPSEnum.FIND_ALL_PAYMENT_SYSTEMS.getName().equals(userActionDto.getAction())) {
            var paymentSystemList = paymentSystemService.findAll();

            if (paymentSystemList.isEmpty()) {
                producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                        "Доступных платежных систем не найдено"));
            } else {
                var result = new StringBuilder();
                paymentSystemList.forEach(paymentSystem -> result.append(paymentSystem.getId())
                        .append(". ")
                        .append(paymentSystem.getName())
                        .append("\n"));
                producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                        result.toString()));
            }
        } else if (UserActionPSEnum.ADD_PAYMENT_SYSTEMS.getName().equals(userActionDto.getAction())) {
            var user = userService.findByUserId(userActionDto.getUserId());
            if (Objects.isNull(user)) {
                producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                        "Необходимо пройти регистрацию"));
            } else {
                var listIds = userActionDto.getListIds();
                var paymentSystemList = paymentSystemService.findAllByIds(listIds);
                if (paymentSystemList.isEmpty()) {
                    producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                            "Платежных систем с такими номерами не найдено, проверьте корректность ввода и повторите попытку."));
                } else {
                    var userPaymentSystemsFromBd = paymentSystemUserService.findAllByUserId(user.getId())
                            .stream()
                            .map(exchangeUser -> exchangeUser.getPaymentSystem().getId())
                            .toList();

                    var paymentSystemIdListNew = paymentSystemList.stream().map(PaymentSystem::getId).collect(Collectors.toList());
                    paymentSystemIdListNew.removeIf(userPaymentSystemsFromBd::contains);

                    if (paymentSystemIdListNew.isEmpty()) {
                        producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                                "Платежные системы уже добавлены"));
                    } else {
                        List<PaymentSystemUser> paymentSystemUserList = new ArrayList<>();
                        paymentSystemIdListNew.forEach(paymentSystem -> paymentSystemUserList.add(new PaymentSystemUser(
                                null,
                                new PaymentSystem(paymentSystem),
                                new User(user.getId()))));
                        paymentSystemUserService.create(paymentSystemUserList);
                        producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                                "Платежные системы успешно добавлены"));
                    }
                }
            }
        } else if (UserActionPSEnum.FIND_ALL_USER_PAYMENT_SYSTEMS.getName().equals(userActionDto.getAction())) {
            var user = userService.findByUserId(userActionDto.getUserId());
            if (Objects.isNull(user)) {
                producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                        "Необходимо пройти регистрацию"));
            } else {
                var paymentSystemList = paymentSystemUserService.findAllByUserId(user.getId()).stream()
                        .map(PaymentSystemUser::getPaymentSystem)
                        .toList();

                if (!paymentSystemList.isEmpty()) {
                    var result = new StringBuilder();
                    paymentSystemList.forEach(exchange -> result
                            .append(exchange.getId())
                            .append(". ")
                            .append(exchange.getName())
                            .append("\n"));
                    producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                            result.toString()));
                } else {
                    producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                            "Платежных систем не добавлено"));
                }
            }
        } else if (UserActionPSEnum.DELETE_PAYMENT_SYSTEMS.getName().equals(userActionDto.getAction())) {
            var user = userService.findByUserId(userActionDto.getUserId());
            if (Objects.isNull(user)) {
                producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                        "Необходимо пройти регистрацию"));
            } else {
                var listIds = userActionDto.getListIds();
                var paymentSystemList = paymentSystemService.findAllByIds(listIds);
                if (paymentSystemList.isEmpty()) {
                    producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                            "Платежных систем с такими номерами не найдено, проверьте корректность ввода и повторите попытку."));
                } else {
                    var userPaymentSystemFromBd = paymentSystemUserService.findAllByUserId(user.getId())
                            .stream()
                            .map(paymentSystemUser -> paymentSystemUser.getPaymentSystem().getId())
                            .toList();

                    var paymentSystemIdListNew = paymentSystemList.stream()
                            .filter(e -> userPaymentSystemFromBd.contains(e.getId()))
                            .collect(Collectors.toList());

                    paymentSystemUserService.deleteByPaymentSystemsAndUser(user, paymentSystemIdListNew);
                    producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                            "Платежные системы успешно удалены"));
                }
            }
        } else if (UserActionPSEnum.DELETE_ALL_PAYMENT_SYSTEMS.getName().equals(userActionDto.getAction())) {
            var user = userService.findByUserId(userActionDto.getUserId());
            if (Objects.isNull(user)) {
                producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                        "Необходимо пройти регистрацию"));
            } else {
                paymentSystemUserService.deleteAllByUser(user);
                producerService.producerAnswerActionUserPaymentSystem(new SendMessage(userActionDto.getUserId().toString(),
                        "Все платежные системы успешно удалены"));
            }
        }
    }

    @Override
    @RabbitListener(queues = "text_register_user")
    public void consumeTextRegisterUser(UserRegisterDto userRegisterDto) {
        userService.create(userRegisterDto);
    }
}
