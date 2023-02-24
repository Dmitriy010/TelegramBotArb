package ru.node.service;

import ru.node.dto.OrderSubscribeDto;
import ru.node.model.OrderSubscribe;

import java.util.List;

public interface OrderSubscribeService {

    OrderSubscribe create(OrderSubscribeDto orderSubscribeDto);

    List<OrderSubscribe> findAll();

    List<OrderSubscribe> findAllByUserId(Long UserId);

    Boolean deleteById(Long id);
}
