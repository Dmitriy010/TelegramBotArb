package ru.node.service;

import org.springframework.lang.NonNull;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.model.BinanceOrder;

public interface BinanceService {

    void merge(BinanceOrder binanceOrder);

    void scheduledOrderBinance(@NonNull BinanceBody binanceBody);
}
