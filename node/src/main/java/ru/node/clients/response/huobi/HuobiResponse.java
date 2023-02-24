package ru.node.clients.response.huobi;

import lombok.Data;

import java.util.List;

@Data
public class HuobiResponse {

    private List<HuobiData> data;
}
