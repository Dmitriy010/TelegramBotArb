package ru.lds.telegram.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscribeActionDto {

    private String action;
    private Long userId;
    private Long subscribeId;
}
