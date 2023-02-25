package ru.node.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.node.clients.BinanceServiceClient;
import ru.node.clients.HuobiServiceClient;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.enums.Asset;
import ru.node.enums.PaymentSystem;
import ru.node.enums.TradeType;
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

            var binanceData = responseBody.getData().get(0);
            var tradeMethodsList = binanceData.getAdv().getTradeMethods().stream()
                    .filter(tradeM -> tradeM.getIdentifier().equals(binanceBody.getPayTypes().get(0)))
                    .collect(Collectors.toList());
            binanceData.getAdv().setTradeMethods(tradeMethodsList);
            var binanceOrder = orderMapper.binanceDataToOrder(binanceData);
            merge(binanceOrder);
        }
    }

    @Override
    public void scheduledOrderHuobi(@NonNull PaymentSystem payMethod,
                                    @NonNull Asset asset,
                                    @NonNull TradeType tradeType) {
        Map<String, String> mapParameters = new HashMap<>();
        mapParameters.put("coinId", asset.getNameHuobi());
        mapParameters.put("currency", "11");
        mapParameters.put("tradeType", tradeType.getNameHuobi());
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

            var huobiData = responseBody.getData().get(0);
            var tradeMethodsList = huobiData.getPayMethods().stream()
                    .filter(tradeM -> tradeM.getPayMethodId().toString().equals(payMethod.getNameHuobi()))
                    .collect(Collectors.toList());
            huobiData.setPayMethods(tradeMethodsList);
            var huobiOrder = orderMapper.huobiDataToOrder(huobiData);
            huobiOrder.setFiat(FIAT_RUB);
            huobiOrder.setAsset(asset.name());
            huobiOrder.setTradeType(tradeType.name());
            huobiOrder.setTradeMethod(payMethod.getName());
            merge(huobiOrder);
        }
    }

    @Override
    @Transactional
    public void deleteOldOrders() {
        orderRepository.deleteAllByDateIsLessThan(LocalDateTime.now(ZoneId.of(ZONE_ID)).minusMinutes(5));
    }

    @Override
    @Transactional
    public void merge(@NonNull Order order) {
        var orderFromBd = orderRepository.findByExchangeAndAssetAndTradeMethodAndTradeType(
                order.getExchange(),
                order.getAsset(),
                order.getTradeMethod(),
                order.getTradeType());

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
