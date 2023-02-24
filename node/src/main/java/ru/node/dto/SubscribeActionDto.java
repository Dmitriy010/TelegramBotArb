package ru.node.dto;

import lombok.Data;

@Data
public class SubscribeActionDto {

    private String action;
    private Long id;
    private Long userId;
    private Long subscribeId;
}
