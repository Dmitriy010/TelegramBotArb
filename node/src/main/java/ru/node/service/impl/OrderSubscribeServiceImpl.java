package ru.node.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import ru.node.dto.OrderSubscribeDto;
import ru.node.mapper.OrderMapper;
import ru.node.model.OrderSubscribe;
import ru.node.repository.OrderSubscribeRepository;
import ru.node.service.OrderSubscribeService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderSubscribeServiceImpl implements OrderSubscribeService {

    private final OrderSubscribeRepository orderSubscribeRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderSubscribe create(OrderSubscribeDto orderSubscribeDto) {
        var orderSubscribe = orderMapper.orderSubscribeDtoToOrderSubscribe(orderSubscribeDto);
        orderSubscribe.setDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));

        return orderSubscribeRepository.save(orderSubscribe);
    }

    @Override
    public List<OrderSubscribe> findAll() {
        return orderSubscribeRepository.findAll();
    }

    @Override
    public List<OrderSubscribe> findAllByUserId(Long userId) {
        return orderSubscribeRepository.findAllByUserId(userId);
    }

    @Override
    public Boolean deleteById(Long id) {
        try {
            orderSubscribeRepository.deleteById(id);
            return Boolean.TRUE;
        } catch (EmptyResultDataAccessException e) {
            return Boolean.FALSE;
        }
    }
}
