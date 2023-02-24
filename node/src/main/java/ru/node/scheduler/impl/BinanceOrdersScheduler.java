package ru.node.scheduler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.enums.Asset;
import ru.node.enums.Exchange;
import ru.node.enums.PaymentSystem;
import ru.node.enums.TradeType;
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
        startScheduled(PaymentSystem.PAYEER, Asset.USDT, TradeType.BUY);
        startScheduled(PaymentSystem.SBERBANK, Asset.USDT, TradeType.BUY);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.USDT, TradeType.BUY);
        startScheduled(PaymentSystem.TINKOFF, Asset.USDT, TradeType.BUY);
        startScheduled(PaymentSystem.QIWI, Asset.USDT, TradeType.BUY);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 2000)
    public void scheduledSellUsdt() {
        startScheduled(PaymentSystem.PAYEER, Asset.USDT, TradeType.SELL);
        startScheduled(PaymentSystem.SBERBANK, Asset.USDT, TradeType.SELL);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.USDT, TradeType.SELL);
        startScheduled(PaymentSystem.TINKOFF, Asset.USDT, TradeType.SELL);
        startScheduled(PaymentSystem.QIWI, Asset.USDT, TradeType.SELL);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 4000)
    public void scheduledBuyBtc() {
        startScheduled(PaymentSystem.PAYEER, Asset.BTC, TradeType.BUY);
        startScheduled(PaymentSystem.SBERBANK, Asset.BTC, TradeType.BUY);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.BTC, TradeType.BUY);
        startScheduled(PaymentSystem.TINKOFF, Asset.BTC, TradeType.BUY);
        startScheduled(PaymentSystem.QIWI, Asset.BTC, TradeType.BUY);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 6000)
    public void scheduledSellBtc() {
        startScheduled(PaymentSystem.PAYEER, Asset.BTC, TradeType.SELL);
        startScheduled(PaymentSystem.SBERBANK, Asset.BTC, TradeType.SELL);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.BTC, TradeType.SELL);
        startScheduled(PaymentSystem.TINKOFF, Asset.BTC, TradeType.SELL);
        startScheduled(PaymentSystem.QIWI, Asset.BTC, TradeType.SELL);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 8000)
    public void scheduledBuyEth() {
        startScheduled(PaymentSystem.PAYEER, Asset.ETH, TradeType.BUY);
        startScheduled(PaymentSystem.SBERBANK, Asset.ETH, TradeType.BUY);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.ETH, TradeType.BUY);
        startScheduled(PaymentSystem.TINKOFF, Asset.ETH, TradeType.BUY);
        startScheduled(PaymentSystem.QIWI, Asset.ETH, TradeType.BUY);
    }

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    public void scheduledSellEth() {
        startScheduled(PaymentSystem.PAYEER, Asset.ETH, TradeType.SELL);
        startScheduled(PaymentSystem.SBERBANK, Asset.ETH, TradeType.SELL);
        startScheduled(PaymentSystem.RAIFFEISENBANK, Asset.ETH, TradeType.SELL);
        startScheduled(PaymentSystem.TINKOFF, Asset.ETH, TradeType.SELL);
        startScheduled(PaymentSystem.QIWI, Asset.ETH, TradeType.SELL);
    }

    private void startScheduled(PaymentSystem payMethod, Asset asset, TradeType tradeType) {
        CompletableFuture.runAsync(() -> orderService.scheduledOrderBinance(new BinanceBody(
                Boolean.FALSE,
                1,
                5,
                List.of(payMethod.getNameBinance()),
                Collections.emptyList(),
                null,
                Integer.parseInt(BALANCE),
                asset.getNameBinance(),
                FIAT_RUB,
                tradeType.getNameBinance()))).exceptionally((e) -> {
            log.error("!-----" +
                    Exchange.BINANCE.getName() + " -> " +
                    payMethod.getNameBinance() + " -> " +
                    asset.getNameBinance() + " -> " +
                    tradeType.getNameBinance() + "-----!");
            log.error(e.getMessage(), e);
            return null;
        });
    }
}
