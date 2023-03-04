package ru.node.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.node.enums.TradeTypeEnum;
import ru.node.model.Order;
import ru.node.model.OrderSubscribe;

import java.time.format.DateTimeFormatter;

import static ru.node.constants.Constants.FIAT_RUB;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtils {
    public static String getTextOrderSubscribeForUser(OrderSubscribe orderSubscribe) {
        var tradeType = orderSubscribe.getTradeType().equals(TradeTypeEnum.SELL.name()) ?
                TradeTypeEnum.SELL.getName() : TradeTypeEnum.BUY.getName();
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
                FIAT_RUB +
                "\n" +
                "Платежная система: " +
                orderSubscribe.getPaymentSystem() +
                "\n" +
                "<------------------------------>";
    }

    public static String getTextOrderSubscribe(OrderSubscribe orderSubscribe) {
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
                FIAT_RUB +
                "\n" +
                "Платежная система: " +
                orderSubscribe.getPaymentSystem() +
                "\n" +
                "<------------------------------>";
    }

    public static String getTextOrderInfo(Order order) {
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

    public static String getTextOrderSubscribeResult(Order order) {
        var tradeType = order.getTradeType().equals(TradeTypeEnum.SELL.name()) ?
                TradeTypeEnum.SELL.getName() : TradeTypeEnum.BUY.getName();
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
