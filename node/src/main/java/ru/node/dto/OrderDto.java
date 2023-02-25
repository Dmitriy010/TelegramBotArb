package ru.node.dto;

import lombok.Data;

@Data
public class OrderDto {

    private String asset;
    private String exchange;
    private String paymentSystem;
    private String tradeType;
    private Long userId;
    private Double price;
}
