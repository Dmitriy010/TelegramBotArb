package ru.node.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.enums.Asset;
import ru.node.enums.PaymentSystem;
import ru.node.enums.TradeType;
import ru.node.model.Order;

import java.util.List;

public interface OrderService {

    void merge(@NonNull Order order);

    List<Order> findAll(Specification<Order> specification);

    void scheduledOrderBinance(@NonNull BinanceBody binanceBody);

    void scheduledOrderHuobi(@NonNull PaymentSystem payMethod,
                             @NonNull Asset asset,
                             @NonNull TradeType tradeType);

    void deleteOldOrders();
}
