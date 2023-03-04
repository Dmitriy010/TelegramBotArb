package ru.node.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.NonNull;
import ru.node.clients.response.binance.BinanceData;
import ru.node.clients.response.huobi.HuobiData;
import ru.node.config.MapStructConfig;
import ru.node.dto.OrderDto;
import ru.node.model.Order;
import ru.node.model.OrderSubscribe;

@Mapper(config = MapStructConfig.class)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tradeType", source = "binanceData.adv.tradeType")
    @Mapping(target = "asset", source = "binanceData.adv.asset")
    @Mapping(target = "fiat", source = "binanceData.adv.fiatUnit")
    @Mapping(target = "price", source = "binanceData.adv.price")
    @Mapping(target = "transAmount", expression = "java(binanceData.getAdv().getMinSingleTransAmount() + \" - \" + binanceData.getAdv().getMaxSingleTransAmount())")
    @Mapping(target = "tradeMethod", expression = "java(ru.node.enums.PaymentSystemEnum.getByNameBinance(binanceData.getAdv().getTradeMethods().get(0).getIdentifier()).getName())")
    @Mapping(target = "tradableQuantity", source = "binanceData.adv.tradableQuantity")
    @Mapping(target = "userName", source = "binanceData.advertiser.nickName")
    @Mapping(target = "successOrders", source = "binanceData.advertiser.monthOrderCount")
    @Mapping(target = "successOrdersPercent", expression = "java(java.math.BigDecimal.valueOf(binanceData.getAdvertiser().getMonthFinishRate() * 100).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue())")
    Order binanceDataToOrder(@NonNull BinanceData binanceData);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tradeType", ignore = true)
    @Mapping(target = "asset", ignore = true)
    @Mapping(target = "fiat", ignore = true)
    @Mapping(target = "tradeMethod", expression = "java(huobiData.getPayMethods().get(0).getName())")
    @Mapping(target = "transAmount", expression = "java(huobiData.getMinTradeLimit() + \" - \" + huobiData.getMaxTradeLimit())")
    @Mapping(target = "tradableQuantity", source = "huobiData.tradeCount")
    @Mapping(target = "successOrders", source = "huobiData.tradeMonthTimes")
    @Mapping(target = "successOrdersPercent", expression = "java(java.math.BigDecimal.valueOf(Double.parseDouble(huobiData.getOrderCompleteRate())).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue())")
    Order huobiDataToOrder(@NonNull HuobiData huobiData);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    OrderSubscribe ordertoToOrderSubscribe(@NonNull OrderDto orderDto);
}
