package ru.node.scheduler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.enums.AssetEnum;
import ru.node.enums.PaymentSystemEnum;
import ru.node.enums.TradeTypeEnum;
import ru.node.service.OrderService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static ru.node.constants.Constants.BALANCE;
import static ru.node.constants.Constants.FIAT_RUB;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceOrdersScheduler {

    private final OrderService orderService;

    @Scheduled(fixedRate = 10000)
    public void scheduledBuyUsdt() {
        startScheduled(PaymentSystemEnum.PAYEER, AssetEnum.USDT, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.SBERBANK, AssetEnum.USDT, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.RAIFFEISENBANK, AssetEnum.USDT, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.TINKOFF, AssetEnum.USDT, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.QIWI, AssetEnum.USDT, TradeTypeEnum.BUY);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 1500)
    public void scheduledSellUsdt() {
        startScheduled(PaymentSystemEnum.PAYEER, AssetEnum.USDT, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.SBERBANK, AssetEnum.USDT, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.RAIFFEISENBANK, AssetEnum.USDT, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.TINKOFF, AssetEnum.USDT, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.QIWI, AssetEnum.USDT, TradeTypeEnum.SELL);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 3000)
    public void scheduledBuyBtc() {
        startScheduled(PaymentSystemEnum.PAYEER, AssetEnum.BTC, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.SBERBANK, AssetEnum.BTC, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.RAIFFEISENBANK, AssetEnum.BTC, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.TINKOFF, AssetEnum.BTC, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.QIWI, AssetEnum.BTC, TradeTypeEnum.BUY);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 4500)
    public void scheduledSellBtc() {
        startScheduled(PaymentSystemEnum.PAYEER, AssetEnum.BTC, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.SBERBANK, AssetEnum.BTC, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.RAIFFEISENBANK, AssetEnum.BTC, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.TINKOFF, AssetEnum.BTC, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.QIWI, AssetEnum.BTC, TradeTypeEnum.SELL);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 6000)
    public void scheduledBuyEth() {
        startScheduled(PaymentSystemEnum.PAYEER, AssetEnum.ETH, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.SBERBANK, AssetEnum.ETH, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.RAIFFEISENBANK, AssetEnum.ETH, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.TINKOFF, AssetEnum.ETH, TradeTypeEnum.BUY);
        startScheduled(PaymentSystemEnum.QIWI, AssetEnum.ETH, TradeTypeEnum.BUY);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 7500)
    public void scheduledSellEth() {
        startScheduled(PaymentSystemEnum.PAYEER, AssetEnum.ETH, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.SBERBANK, AssetEnum.ETH, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.RAIFFEISENBANK, AssetEnum.ETH, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.TINKOFF, AssetEnum.ETH, TradeTypeEnum.SELL);
        startScheduled(PaymentSystemEnum.QIWI, AssetEnum.ETH, TradeTypeEnum.SELL);
    }

    private void startScheduled(PaymentSystemEnum payMethod, AssetEnum assetEnum, TradeTypeEnum tradeTypeEnum) {
        CompletableFuture.runAsync(() -> orderService.scheduledOrderBinance(new BinanceBody(
                        Boolean.FALSE,
                        1,
                        20,
                        List.of(payMethod.getNameBinance()),
                        Collections.emptyList(),
                        null,
                        Integer.parseInt(BALANCE),
                        assetEnum.getNameBinance(),
                        FIAT_RUB,
                        tradeTypeEnum.getNameBinance())))
                .exceptionally((e) -> {
                    log.error(e.getMessage(), e);
                    return null;
                });
    }
}
