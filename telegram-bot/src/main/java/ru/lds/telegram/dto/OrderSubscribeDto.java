package ru.lds.telegram.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderSubscribeDto extends OrderInfoDto {

    private String price;
}