package ru.node.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.enums.AssetEnum;
import ru.node.enums.PaymentSystemEnum;
import ru.node.enums.TradeTypeEnum;
import ru.node.model.Order;

import java.util.List;

public interface OrderService {

    void merge(@NonNull Order order);

    List<Order> findAll(Specification<Order> specification);

    void scheduledOrderBinance(@NonNull BinanceBody binanceBody);

    void scheduledOrderHuobi(@NonNull PaymentSystemEnum payMethod,
                             @NonNull AssetEnum assetEnum,
                             @NonNull TradeTypeEnum tradeTypeEnum);

    void deleteOldOrders();
}
