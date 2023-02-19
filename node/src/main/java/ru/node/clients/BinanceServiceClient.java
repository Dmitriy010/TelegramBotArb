package ru.node.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.node.clients.request.binance.BinanceBody;
import ru.node.clients.response.binance.BinanceResponse;
import ru.node.config.FeignBinanceConfig;

@FeignClient(name = "binance-client", url = "https://p2p.binance.com/bapi/c2c/v2/friendly/c2c/adv/search", configuration = FeignBinanceConfig.class)
public interface BinanceServiceClient {

    @PostMapping(path = "")
    ResponseEntity<BinanceResponse> getP2Pinfo(@RequestBody BinanceBody binanceBody);
}
