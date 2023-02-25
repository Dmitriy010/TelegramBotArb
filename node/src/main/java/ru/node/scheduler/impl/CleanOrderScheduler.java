package ru.node.scheduler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.node.service.OrderService;


@Component
@RequiredArgsConstructor
@Slf4j
public class CleanOrderScheduler {

    private final OrderService orderService;

    @Scheduled(fixedRate = 300000)
    public void scheduledCleanOrder() {
        orderService.deleteOldOrders();
    }
}
