package ru.node.service;

import ru.node.model.Limit;

import java.util.List;

public interface LimitService {

    List<Limit> findAll();
   Limit findByLimit(Long limit);
}
