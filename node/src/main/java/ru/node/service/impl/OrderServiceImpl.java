package ru.node.service.impl;

import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.node.clients.BinanceServiceClient;
import ru.node.clients.HuobiServiceClient;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.enums.AssetEnum;
import ru.node.enums.PaymentSystemEnum;
import ru.node.enums.TradeTypeEnum;
import ru.node.mapper.OrderMapper;
import ru.node.model.Order;
import ru.node.repository.OrderRepository;
import ru.node.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.node.constants.Constants.BALANCE;
import static ru.node.constants.Constants.FIAT_RUB;

@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Value("${orders.count}")
    private int orderCount;
    @Value("${orders.percent}")
    private double orderPercent;
    private final BinanceServiceClient binanceServiceClient;
    private final HuobiServiceClient huobiServiceClient;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    @Override
    public void scheduledOrderBinance(@NonNull BinanceBody binanceBody) {
        var response = binanceServiceClient.getOrders(binanceBody);

        var responseBody = response.getBody();
        if (Objects.nonNull(responseBody) &&
                Objects.nonNull(responseBody.getData()) &&
                !responseBody.getData().isEmpty()) {

            var binanceData = responseBody.getData().stream()
                    .filter(data -> data.getAdvertiser().getMonthOrderCount() > orderCount &&
                            data.getAdvertiser().getMonthFinishRate() * 100 > orderPercent)
                    .toList();

            if (!binanceData.isEmpty()) {
                binanceData.forEach(data -> {
                    var tradeMethodsList = data.getAdv().getTradeMethods().stream()
                            .filter(tradeM -> tradeM.getIdentifier().equals(binanceBody.getPayTypes().get(0)))
                            .collect(Collectors.toList());
                    data.getAdv().setTradeMethods(tradeMethodsList);
                });

                var binanceOrderList = orderMapper.binanceDataToOrder(binanceData);

                var newOrder = binanceOrderList.get(0);
                deleteAllByExchangeAndAssetAndTradeMethodAndTradeType(
                        newOrder.getExchange(),
                        newOrder.getAsset(),
                        newOrder.getTradeMethod(),
                        newOrder.getTradeType());

                create(binanceOrderList);
            }
        }
    }

    @Override
    public void scheduledOrderHuobi(@NonNull PaymentSystemEnum payMethod,
                                    @NonNull AssetEnum assetEnum,
                                    @NonNull TradeTypeEnum tradeTypeEnum) {
        Map<String, String> mapParameters = new HashMap<>();
        mapParameters.put("coinId", assetEnum.getNameHuobi());
        mapParameters.put("currency", "11");
        mapParameters.put("tradeType", tradeTypeEnum.getNameHuobi());
        mapParameters.put("currPage", "1");
        mapParameters.put("payMethod", payMethod.getNameHuobi());
        mapParameters.put("acceptOrder", "0");
        mapParameters.put("country", null);
        mapParameters.put("blockType", "general");
        mapParameters.put("online", "1");
        mapParameters.put("range", "0");
        mapParameters.put("amount", BALANCE);
        mapParameters.put("isThumbsUp", "false");
        mapParameters.put("isMerchant", "false");
        mapParameters.put("isTraded", "false");
        mapParameters.put("onlyTradable", "false");
        mapParameters.put("isFollowed", "false");

        var responseBody = huobiServiceClient.getOrders(mapParameters).getBody();
        if (Objects.nonNull(responseBody) &&
                Objects.nonNull(responseBody.getData()) &&
                !responseBody.getData().isEmpty()) {

            var huobiData = responseBody.getData().stream()
                    .filter(data -> data.getTradeMonthTimes() > orderCount &&
                            Double.parseDouble(data.getOrderCompleteRate()) > orderPercent)
                    .toList();

            if (!huobiData.isEmpty()) {

                huobiData.forEach(data -> {
                    var tradeMethodsList = data.getPayMethods().stream()
                            .filter(tradeM -> tradeM.getPayMethodId().toString().equals(payMethod.getNameHuobi()))
                            .collect(Collectors.toList());
                    data.setPayMethods(tradeMethodsList);
                });

                var huobiOrderList = orderMapper.huobiDataToOrder(huobiData);

                huobiOrderList.forEach(order -> {
                    order.setFiat(FIAT_RUB);
                    order.setAsset(assetEnum.name());
                    order.setTradeType(tradeTypeEnum.name());
                    order.setTradeMethod(payMethod.getName());
                });

                var newOrder = huobiOrderList.get(0);
                deleteAllByExchangeAndAssetAndTradeMethodAndTradeType(
                        newOrder.getExchange(),
                        newOrder.getAsset(),
                        newOrder.getTradeMethod(),
                        newOrder.getTradeType());

                create(huobiOrderList);
            }
        }
    }

    @Override
    public void deleteAllByExchangeAndAssetAndTradeMethodAndTradeType(@NonNull String exchange,
                                                                      @NonNull String asset,
                                                                      @NonNull String tradeMethod,
                                                                      @NonNull String tradeType) {
        orderRepository.deleteAllByExchangeAndAssetAndTradeMethodAndTradeType(exchange, asset, tradeMethod, tradeType);
    }

    @Override
    @Transactional
    public void create(@NonNull List<Order> orderList) {
        orderRepository.saveAll(orderList);
    }

    @Override
    @Timed("getOrderInfo")
    public List<Order> findAll(Specification<Order> specification) {
        return orderRepository.findAll(specification);
    }
}
