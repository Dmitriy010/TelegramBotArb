package ru.node.dto;

import lombok.Data;

@Data
public class OrderInfoDto {

    private String asset;
    private String exchange;
    private String paymentSystem;
    private String tradeType;
    private Long userId;
}
