package ru.node.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.node.dto.OrderDto;
import ru.node.dto.SubscribeActionDto;
import ru.node.enums.SubscribeAction;
import ru.node.repository.specification.OrderSpecification;
import ru.node.service.ConsumerService;
import ru.node.service.OrderService;
import ru.node.service.OrderSubscribeService;
import ru.node.service.ProducerService;
import ru.node.utils.MessageUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final ProducerService producerService;
    private final OrderService orderService;
    private final OrderSubscribeService orderSubscribeService;

    @Override
    @RabbitListener(queues = "text_message_order_info")
    public void consumeTextMessageOrderInfo(OrderDto orderDto) {
        var orderList = orderService.findAll(OrderSpecification.getFilterOrderCheckPrice(
                orderDto.getTradeType(),
                orderDto.getAsset(),
                orderDto.getExchange(),
                orderDto.getPaymentSystem()));
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
        var orderSubscribe = orderSubscribeService.create(orderDto);
        var sendMessage = new SendMessage(orderDto.getUserId().toString(), MessageUtils.getTextOrderSubscribe(orderSubscribe));
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
                                MessageUtils.getTextOrderSubscribeForUser(orderSubscribe))));
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
}
