package ru.node.scheduler.impl;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.node.model.Order;
import ru.node.repository.specification.OrderSpecification;
import ru.node.service.OrderService;
import ru.node.service.OrderSubscribeService;
import ru.node.service.ProducerService;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderSubscribeScheduler {

    private final OrderSubscribeService orderSubscribeService;
    private final OrderService orderService;
    private final ProducerService producerService;


    @Scheduled(fixedRate = 5000)
    public void scheduledSubscribe() {
        orderSubscribeService.findAll().forEach(orderSubscribe -> {
            var orderList = orderService.findAll(OrderSpecification.getFilterOrderSubscribePrice(
                    orderSubscribe.getTradeType(),
                    orderSubscribe.getAsset(),
                    orderSubscribe.getExchange(),
                    orderSubscribe.getPaymentSystem(),
                    orderSubscribe.getPrice()));
            if (!orderList.isEmpty()) {
                var sendMessage = new SendMessage(orderSubscribe.getUserId().toString(), getTextOrder(orderList.get(0)));
                producerService.producerAnswerSubscribe(sendMessage);
                orderSubscribeService.deleteById(orderSubscribe.getId());
            }
        });
    }

    private String getTextOrder(Order order) {
        var tradeType = order.getTradeType().equals("SELL") ? "Купить" : "Продать";
        return EmojiParser.parseToUnicode("🚀🚀🚀") +
                "Цена достигнута" +
                EmojiParser.parseToUnicode("🚀🚀🚀") +
                "\n" +
                "Дата: " +
                order.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) +
                "\n" +
                "Тип сделки: " +
                tradeType +
                "\n" +
                "Биржа: " +
                order.getExchange() +
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
