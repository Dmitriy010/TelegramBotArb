package ru.node.clients;

import io.micrometer.core.annotation.Timed;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.node.clients.response.huobi.HuobiResponse;
import ru.node.config.FeignConfig;

import java.util.Map;

@FeignClient(name = "huobi-client", url = "https://otc-api.trygofast.com", configuration = FeignConfig.class)
public interface HuobiServiceClient {
    @GetMapping(path = "/v1/data/trade-market", headers = {
            "Host=otc-api.trygofast.com",
            "Origin=https://www.huobi.com",
            "Refer=https://www.huobi.com/"})
    @Timed("getOrdersHuobi")
    ResponseEntity<HuobiResponse> getOrders(@RequestParam Map<String, String> allParams);
}
