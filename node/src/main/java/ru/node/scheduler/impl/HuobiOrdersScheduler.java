package ru.node.scheduler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.node.enums.Asset;
import ru.node.enums.Exchange;
import ru.node.enums.PaymentSystem;
import ru.node.enums.TradeType;
import ru.node.service.OrderService;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HuobiOrdersScheduler {

    private final OrderService orderService;

    @Scheduled(fixedRate = 10000)
    public void scheduledBuyUsdt() {
        startScheduled(PaymentSystem.SBERBANK, Asset.USDT, TradeType.BUY);
        startScheduled(PaymentSystem.TINKOFF, Asset.USDT, TradeType.BUY);
        startScheduled(PaymentSystem.PAYEER, Asset.USDT, TradeType.BUY);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.USDT, TradeType.BUY);
        startScheduled(PaymentSystem.QIWI, Asset.USDT, TradeType.BUY);
        startScheduled(PaymentSystem.ALFABANK, Asset.USDT, TradeType.BUY);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 2000)
    public void scheduledSellUsdt() {
        startScheduled(PaymentSystem.SBERBANK, Asset.USDT, TradeType.SELL);
        startScheduled(PaymentSystem.TINKOFF, Asset.USDT, TradeType.SELL);
        startScheduled(PaymentSystem.PAYEER, Asset.USDT, TradeType.SELL);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.USDT, TradeType.SELL);
        startScheduled(PaymentSystem.QIWI, Asset.USDT, TradeType.SELL);
        startScheduled(PaymentSystem.ALFABANK, Asset.USDT, TradeType.SELL);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 4000)
    public void scheduledBuyBtc() {
        startScheduled(PaymentSystem.SBERBANK, Asset.BTC, TradeType.BUY);
        startScheduled(PaymentSystem.TINKOFF, Asset.BTC, TradeType.BUY);
        startScheduled(PaymentSystem.PAYEER, Asset.BTC, TradeType.BUY);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.BTC, TradeType.BUY);
        startScheduled(PaymentSystem.QIWI, Asset.BTC, TradeType.BUY);
        startScheduled(PaymentSystem.ALFABANK, Asset.BTC, TradeType.BUY);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 6000)
    public void scheduledSellBtc() {
        startScheduled(PaymentSystem.SBERBANK, Asset.BTC, TradeType.SELL);
        startScheduled(PaymentSystem.TINKOFF, Asset.BTC, TradeType.SELL);
        startScheduled(PaymentSystem.PAYEER, Asset.BTC, TradeType.SELL);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.BTC, TradeType.SELL);
        startScheduled(PaymentSystem.QIWI, Asset.BTC, TradeType.SELL);
        startScheduled(PaymentSystem.ALFABANK, Asset.BTC, TradeType.SELL);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 8000)
    public void scheduledBuyEth() {
        startScheduled(PaymentSystem.SBERBANK, Asset.ETH, TradeType.BUY);
        startScheduled(PaymentSystem.TINKOFF, Asset.ETH, TradeType.BUY);
        startScheduled(PaymentSystem.PAYEER, Asset.ETH, TradeType.BUY);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.ETH, TradeType.BUY);
        startScheduled(PaymentSystem.QIWI, Asset.ETH, TradeType.BUY);
        startScheduled(PaymentSystem.ALFABANK, Asset.ETH, TradeType.BUY);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    public void scheduledSellEth() {
        startScheduled(PaymentSystem.SBERBANK, Asset.ETH, TradeType.SELL);
        startScheduled(PaymentSystem.TINKOFF, Asset.ETH, TradeType.SELL);
        startScheduled(PaymentSystem.PAYEER, Asset.ETH, TradeType.SELL);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.ETH, TradeType.SELL);
        startScheduled(PaymentSystem.QIWI, Asset.ETH, TradeType.SELL);
        startScheduled(PaymentSystem.ALFABANK, Asset.ETH, TradeType.SELL);
    }

    private void startScheduled(PaymentSystem payMethod, Asset asset, TradeType tradeType) {
        CompletableFuture.runAsync(() -> orderService.scheduledOrderHuobi(payMethod, asset, tradeType)).exceptionally((e) -> {
            log.error("!-----" +
                    Exchange.HUOBI.getName() + " -> " +
                    payMethod.getNameHuobi() + " -> " +
                    asset.getNameHuobi() + " -> " +
                    tradeType.getNameHuobi() + "-----!");
            log.error(e.getMessage(), e);
            return null;
        });
    }
}
