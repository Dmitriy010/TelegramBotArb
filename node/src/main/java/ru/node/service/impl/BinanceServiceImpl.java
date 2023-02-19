package ru.node.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.node.clients.BinanceServiceClient;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.mapper.BinanceMapper;
import ru.node.model.BinanceOrder;
import ru.node.repository.BinanceOrderRepository;
import ru.node.service.BinanceService;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BinanceServiceImpl implements BinanceService {

    private final BinanceServiceClient binanceServiceClient;
    private final BinanceMapper binanceMapper;
    private final BinanceOrderRepository binanceOrderRepository;

    public void scheduledOrderBinance(@NonNull BinanceBody binanceBody) {
        var response = binanceServiceClient.getP2Pinfo(binanceBody);

        var responseBody = response.getBody();
        if (Objects.nonNull(responseBody)) {
            var binanceData = responseBody.getData().get(0);
            var tradeMethodsList = binanceData.getAdv().getTradeMethods().stream()
                    .filter(tradeM -> tradeM.getIdentifier().equals(binanceBody.getPayTypes().get(0)))
                    .collect(Collectors.toList());
            binanceData.getAdv().setTradeMethods(tradeMethodsList);
            var binanceOrder = binanceMapper.binanceDataToBinanceOrder(binanceData);
            merge(binanceOrder);
        }
    }

    @Override
    @Transactional
    public void merge(BinanceOrder binanceOrder) {
        var order = binanceOrderRepository.findByAssetAndFiatAndTradeTypeAndTradeMethod(
                binanceOrder.getAsset(),
                binanceOrder.getFiat(),
                binanceOrder.getTradeType(),
                binanceOrder.getTradeMethod());

        if (order.isPresent()) {
            binanceOrder.setId(order.get().getId());
            binanceOrderRepository.save(binanceOrder);
        } else {
            binanceOrderRepository.save(binanceOrder);
        }
    }
}
