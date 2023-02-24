package ru.node.service.impl;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.node.dto.OrderInfoDto;
import ru.node.dto.OrderSubscribeDto;
import ru.node.dto.SubscribeActionDto;
import ru.node.enums.SubscribeAction;
import ru.node.enums.TradeType;
import ru.node.model.Order;
import ru.node.model.OrderSubscribe;
import ru.node.repository.specification.OrderSpecification;
import ru.node.service.ConsumerService;
import ru.node.service.OrderService;
import ru.node.service.OrderSubscribeService;
import ru.node.service.ProducerService;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final ProducerService producerService;
    private final OrderService orderService;
    private final OrderSubscribeService orderSubscribeService;

    @Override
    @RabbitListener(queues = "text_message_update")
    public void consumeTextMessageUpdates(OrderInfoDto orderInfoDto) {
        var orderList = orderService.findAll(OrderSpecification.getFilterOrderCheckPrice(
                orderInfoDto.getTradeType(),
                orderInfoDto.getAsset(),
                orderInfoDto.getExchange(),
                orderInfoDto.getPaymentSystem()));
        if (!orderList.isEmpty()) {
            orderList.forEach(order ->
                    producerService.producerAnswerSubscribe(new SendMessage(orderInfoDto.getUserId().toString(), getTextOrder(order))));
        } else {
            producerService.producerAnswerSubscribe(new SendMessage(orderInfoDto.getUserId().toString(), "Нет данных"));
        }
    }

    @Override
    @RabbitListener(queues = "text_message_subscribe")
    public void consumeTextMessageSubscribe(OrderSubscribeDto orderSubscribeDto) {
        var orderSubscribe = orderSubscribeService.create(orderSubscribeDto);
        var sendMessage = new SendMessage(orderSubscribeDto.getUserId().toString(), getTextOrderSubscribe(orderSubscribe));
        producerService.producerAnswer(sendMessage);
    }

    @Override
    @RabbitListener(queues = "text_action_subscribe")
    public void consumeTextActionSubscribe(SubscribeActionDto subscribeActionDto) {
        if (SubscribeAction.FIND_ALL.getName().equals(subscribeActionDto.getAction())) {
            var orderSubscribes = orderSubscribeService.findAllByUserId(subscribeActionDto.getUserId());
            if (orderSubscribes.isEmpty()) {
                producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(), "Подписок нет"));
            } else {
                orderSubscribes.forEach(orderSubscribe ->
                        producerService.producerAnswerActionSubscribe(new SendMessage(subscribeActionDto.getUserId().toString(),
                                getTextOrderSubscribeForUser(orderSubscribe))));
            }
        } else if (SubscribeAction.DELETE.getName().equals(subscribeActionDto.getAction())) {
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

    private String getTextOrderSubscribeForUser(OrderSubscribe orderSubscribe) {
        var tradeType = orderSubscribe.getTradeType().equals(TradeType.SELL.name()) ?
                TradeType.SELL.getName() : TradeType.BUY.getName();
        return EmojiParser.parseToUnicode("📋📋📋") +
                "Подписка" +
                EmojiParser.parseToUnicode("📋📋📋") +
                "\n" +
                "Идентификатор: " +
                orderSubscribe.getId() +
                "\n" +
                "Дата: " +
                orderSubscribe.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) +
                "\n" +
                "Тип сделки: " +
                tradeType +
                "\n" +
                "Биржа: " +
                orderSubscribe.getExchange() +
                "\n" +
                "Криптовалюта: " +
                orderSubscribe.getAsset() +
                "\n" +
                "Цена: " +
                orderSubscribe.getPrice() +
                " " +
                "RUB" +
                "\n" +
                "Платежная система: " +
                orderSubscribe.getPaymentSystem() +
                "\n" +
                "<------------------------------>";
    }

    private String getTextOrderSubscribe(OrderSubscribe orderSubscribe) {
        var tradeType = orderSubscribe.getTradeType().equals("SELL") ? "Купить" : "Продать";
        return EmojiParser.parseToUnicode("❗❗❗") +
                "Подписка оформлена" +
                EmojiParser.parseToUnicode("❗❗❗") +
                "\n" +
                "Идентификатор: " +
                orderSubscribe.getId() +
                "\n" +
                "Дата: " +
                orderSubscribe.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) +
                "\n" +
                "Тип сделки: " +
                tradeType +
                "\n" +
                "Биржа: " +
                orderSubscribe.getExchange() +
                "\n" +
                "Криптовалюта: " +
                orderSubscribe.getAsset() +
                "\n" +
                "Цена: " +
                orderSubscribe.getPrice() +
                " " +
                "RUB" +
                "\n" +
                "Платежная система: " +
                orderSubscribe.getPaymentSystem() +
                "\n" +
                "<------------------------------>";
    }

    private String getTextOrder(Order order) {
        return EmojiParser.parseToUnicode("⭐⭐⭐") +
                order.getExchange() +
                EmojiParser.parseToUnicode("⭐⭐⭐") +
                "\n" +
                "Дата: " +
                order.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) +
                "\n" +
                "Криптовалюта: " +
                order.getAsset() +
                "\n" +
                "Цена: " +
                order.getPrice() +
                " " +
                order.getFiat() +
                "\n" +
                "Платежная система: " +
                order.getTradeMethod() +
                "\n" +
                "Пользователь: " +
                order.getUserName() +
                "\n" +
                "Доступно: " +
                order.getTradableQuantity() +
                " " +
                order.getAsset() +
                "\n" +
                "Лимит: " +
                order.getTransAmount() +
                " " +
                order.getFiat() +
                "\n" +
                "Ордеров: " +
                order.getSuccessOrders() +
                "\n" +
                "Выполнено: " +
                order.getSuccessOrdersPercent() +
                " " + "%" +
                "\n" +
                "<------------------------------>";
    }
}
