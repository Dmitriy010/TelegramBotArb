package ru.node.service;

import ru.node.dto.OrderDto;
import ru.node.model.OrderSubscribe;

import java.util.List;

public interface OrderSubscribeService {

    OrderSubscribe create(OrderDto orderDto);

    List<OrderSubscribe> findAll();

    List<OrderSubscribe> findAllByUserId(Long UserId);

    Boolean deleteById(Long id);
}
