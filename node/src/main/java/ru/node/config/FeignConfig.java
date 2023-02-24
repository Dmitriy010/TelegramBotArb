package ru.node.config;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.support.AbstractFormWriter;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;


@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.3 Safari/605.1.15");
            requestTemplate.header(HttpHeaders.CONNECTION, "keep-alive");
            requestTemplate.header(HttpHeaders.ACCEPT_LANGUAGE, "ru");
            requestTemplate.header(HttpHeaders.ACCEPT, "*/*");
            requestTemplate.header(HttpHeaders.CONTENT_TYPE, "application/json");
        };
    }

    @Bean
    public AbstractFormWriter jsonFormWriter() {
        return new JsonFormWriter();
    }
}
