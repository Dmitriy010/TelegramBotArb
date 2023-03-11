package ru.node.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.node.constants.Constants.BALANCE;
import static ru.node.constants.Constants.FIAT_RUB;
import static ru.node.constants.Constants.ZONE_ID;

@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final int ORDER_COUNT = 15;
    private static final double ORDER_PERCENT = 90.0;
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
                    .filter(data -> data.getAdvertiser().getMonthOrderCount() > ORDER_COUNT &&
                            data.getAdvertiser().getMonthFinishRate() * 100 > ORDER_PERCENT)
                    .toList();

            if (!binanceData.isEmpty()) {
                binanceData.forEach(data -> {
                    var tradeMethodsList = data.getAdv().getTradeMethods().stream()
                            .filter(tradeM -> tradeM.getIdentifier().equals(binanceBody.getPayTypes().get(0)))
                            .collect(Collectors.toList());
                    data.getAdv().setTradeMethods(tradeMethodsList);
                });

                var binanceOrderList = orderMapper.binanceDataToOrder(binanceData);

                binanceOrderList.forEach(this::merge);
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
                    .filter(data -> data.getTradeMonthTimes() > ORDER_COUNT &&
                            Double.parseDouble(data.getOrderCompleteRate()) > ORDER_PERCENT)
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

                huobiOrderList.forEach(this::merge);
            }
        }
    }

    @Override
    @Transactional
    public void deleteOldOrders() {
        orderRepository.deleteAllByDateIsLessThan(LocalDateTime.now(ZoneId.of(ZONE_ID)).minusSeconds(30));
    }

    @Override
    @Transactional
    public void merge(@NonNull Order order) {
        var orderFromBd = orderRepository.findByExchangeAndAssetAndTradeMethodAndTradeTypeAndUserName(
                order.getExchange(),
                order.getAsset(),
                order.getTradeMethod(),
                order.getTradeType(),
                order.getUserName());

        if (orderFromBd.isPresent()) {
            order.setId(orderFromBd.get().getId());
            orderRepository.save(order);
        } else {
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> findAll(Specification<Order> specification) {
        return orderRepository.findAll(specification);
    }
}
