package ru.node.scheduler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.service.BinanceService;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceOrdersScheduler {

    private final BinanceService binanceService;

    @Scheduled(fixedRate = 5000)
    public void scheduledPayeer() {
        binanceService.scheduledOrderBinance(new BinanceBody(
                Boolean.FALSE,
                1,
                5,
                List.of("Payeer"),
                Collections.emptyList(),
                null,
                30_000,
                "USDT",
                "RUB",
                "BUY"));
        binanceService.scheduledOrderBinance(new BinanceBody(
                Boolean.FALSE,
                1,
                5,
                List.of("Payeer"),
                Collections.emptyList(),
                null,
                30_000,
                "USDT",
                "RUB",
                "SELL"));
    }

    @Scheduled(fixedRate = 5000)
    public void scheduledRosBank() {
        binanceService.scheduledOrderBinance(new BinanceBody(
                Boolean.FALSE,
                1,
                5,
                List.of("RosBankNew"),
                Collections.emptyList(),
                null,
                30_000,
                "USDT",
                "RUB",
                "BUY"));
        binanceService.scheduledOrderBinance(new BinanceBody(
                Boolean.FALSE,
                1,
                5,
                List.of("RosBankNew"),
                Collections.emptyList(),
                null,
                30_000,
                "USDT",
                "RUB",
                "SELL"));
    }

    @Scheduled(fixedRate = 5000)
    public void scheduledRaiffeisenBank() {
        binanceService.scheduledOrderBinance(new BinanceBody(
                Boolean.FALSE,
                1,
                5,
                List.of("RaiffeisenBank"),
                Collections.emptyList(),
                null,
                30_000,
                "USDT",
                "RUB",
                "BUY"));
        binanceService.scheduledOrderBinance(new BinanceBody(
                Boolean.FALSE,
                1,
                5,
                List.of("RaiffeisenBank"),
                Collections.emptyList(),
                null,
                30_000,
                "USDT",
                "RUB",
                "SELL"));
    }

    @Scheduled(fixedRate = 5000)
    public void scheduledTinkoff() {
        binanceService.scheduledOrderBinance(new BinanceBody(
                Boolean.FALSE,
                1,
                5,
                List.of("TinkoffNew"),
                Collections.emptyList(),
                null,
                30_000,
                "USDT",
                "RUB",
                "BUY"));
        binanceService.scheduledOrderBinance(new BinanceBody(
                Boolean.FALSE,
                1,
                5,
                List.of("TinkoffNew"),
                Collections.emptyList(),
                null,
                30_000,
                "USDT",
                "RUB",
                "SELL"));
    }
}
