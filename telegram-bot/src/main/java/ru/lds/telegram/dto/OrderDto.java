package ru.lds.telegram.dto;

import lombok.Data;

@Data
public class OrderDto {

    private String asset;
    private String exchange;
    private String paymentSystem;
    private String tradeType;
    private Double price;
    private Long userId;

    public OrderDto(Long userId) {
        this.userId = userId;
    }
}
