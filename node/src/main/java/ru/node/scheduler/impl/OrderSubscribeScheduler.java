package ru.node.scheduler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.node.repository.specification.OrderSpecification;
import ru.node.service.OrderService;
import ru.node.service.OrderSubscribeService;
import ru.node.service.ProducerService;
import ru.node.utils.MessageUtils;

import java.util.Arrays;

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
                        orderSubscribe.getTransAmountMin(),
                        Arrays.stream(orderSubscribe.getExchange().split(",")).toList(),
                        Arrays.stream(orderSubscribe.getPaymentSystem().split(",")).toList(),
                        orderSubscribe.getPrice()));

                if (!orderList.isEmpty()) {
                    producerService.producerAnswerSubscribe(new SendMessage(orderSubscribe.getUserId().toString(),
                            MessageUtils.getTextOrderSubscribeResult(orderList.get(0))));
                    orderSubscribeService.deleteById(orderSubscribe.getId());
                }
        });
    }
}
