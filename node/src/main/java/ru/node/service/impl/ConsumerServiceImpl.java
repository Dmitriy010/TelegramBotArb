package ru.node.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.node.model.BinanceOrder;
import ru.node.repository.BinanceOrderRepository;
import ru.node.service.ConsumerService;
import ru.node.service.ProducerService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final ProducerService producerService;
    private final BinanceOrderRepository binanceOrderRepository;

    @Override
    @RabbitListener(queues = "text_message_update")
    public void consumeTextMessageUpdates(Update update) {
        log.info("Node received message");
        if (update.hasMessage() && update.getMessage().hasText()) {
            log.error("Not realized");
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage().hasText()) {
            var messageText = update.getCallbackQuery().getMessage().getText();
            var chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            var split = messageText.split(" ");
            if (messageText.split(" ").length == 2) {
                binanceOrderRepository.findByTradeMethodAndTradeType(split[0], split[1])
                        .ifPresent(order -> producerService.producerAnswer(
                                new SendMessage(chatId, getText(order))));
            } else {
                producerService.producerAnswer(new SendMessage(chatId, "Неверный ввод"));
            }
        }
    }

    private static String getText(BinanceOrder order) {
        return "Криптовалюта: " +
                order.getAsset() +
                "\n" +
                "Цена: " +
                order.getPrice() +
                " " +
                order.getFiat() +
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
                " " +
                "%";
    }
}
