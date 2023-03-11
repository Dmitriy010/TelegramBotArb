package ru.node.clients;

import io.micrometer.core.annotation.Timed;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.clients.response.binance.BinanceResponse;
import ru.node.config.FeignConfig;

@FeignClient(name = "binance-client", url = "https://p2p.binance.com/bapi/c2c/v2/friendly/c2c/adv/search", configuration = FeignConfig.class)
public interface BinanceServiceClient {

    @PostMapping(path = "", headers = {
            "Host=p2p.binance.com",
            "Origin=https://p2p.binance.com"})
    @Timed("getOrdersBinance")
    ResponseEntity<BinanceResponse> getOrders(@RequestBody BinanceBody binanceBody);
}
