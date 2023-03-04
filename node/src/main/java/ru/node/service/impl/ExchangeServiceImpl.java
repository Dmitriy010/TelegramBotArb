package ru.node.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.node.model.Exchange;
import ru.node.model.ExchangeUser;
import ru.node.repository.ExchangeRepository;
import ru.node.service.ExchangeService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRepository exchangeRepository;

    @Override
    public List<Exchange> findAll() {
        var exchangeList = exchangeRepository.findAll();
        return exchangeList.stream().sorted(Comparator.comparing(Exchange::getId)).collect(Collectors.toList());
    }

    @Override
    public List<Exchange> findAllByIds(List<Long> listIds) {
        return exchangeRepository.findAllByIds(listIds);
    }
}
