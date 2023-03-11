package ru.node.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.node.dto.OrderDto;
import ru.node.mapper.OrderMapper;
import ru.node.model.OrderSubscribe;
import ru.node.repository.OrderSubscribeRepository;
import ru.node.service.OrderSubscribeService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static ru.node.constants.Constants.ZONE_ID;

@Component
@RequiredArgsConstructor
public class OrderSubscribeServiceImpl implements OrderSubscribeService {

    private final OrderSubscribeRepository orderSubscribeRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderSubscribe create(OrderDto orderSubscribeDto, Double limit) {
        var orderSubscribe = orderMapper.ordertoToOrderSubscribe(orderSubscribeDto);
        orderSubscribe.setDate(LocalDateTime.now(ZoneId.of(ZONE_ID)));
        orderSubscribe.setTransAmountMin(limit);

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
    public void deleteById(Long id) {
        orderSubscribeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllByUserId(Long userId) {
        orderSubscribeRepository.deleteAllByUserId(userId);
    }
}
