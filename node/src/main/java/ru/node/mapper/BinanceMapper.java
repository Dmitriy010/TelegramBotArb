package ru.node.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.NonNull;
import ru.node.clients.response.binance.BinanceData;
import ru.node.config.MapStructConfig;
import ru.node.model.BinanceOrder;

@Mapper(config = MapStructConfig.class)
public interface BinanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tradeType", source = "binanceData.adv.tradeType")
    @Mapping(target = "asset", source = "binanceData.adv.asset")
    @Mapping(target = "fiat", source = "binanceData.adv.fiatUnit")
    @Mapping(target = "price", source = "binanceData.adv.price")
    @Mapping(target = "transAmount", expression = "java(binanceData.getAdv().getMinSingleTransAmount() + \" - \" + binanceData.getAdv().getMaxSingleTransAmount())")
    @Mapping(target = "tradeMethod", expression = "java(binanceData.getAdv().getTradeMethods().get(0).getTradeMethodName())")
    @Mapping(target = "tradableQuantity", source = "binanceData.adv.tradableQuantity")
    @Mapping(target = "userName", source = "binanceData.advertiser.nickName")
    @Mapping(target = "successOrders", source = "binanceData.advertiser.monthOrderCount")
    @Mapping(target = "successOrdersPercent", expression = "java(java.math.BigDecimal.valueOf(binanceData.getAdvertiser().getMonthFinishRate() * 100).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue())")
    BinanceOrder binanceDataToBinanceOrder(@NonNull BinanceData binanceData);
}
