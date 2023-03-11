package ru.node.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.node.model.Limit;
import ru.node.repository.LimitRepository;
import ru.node.service.LimitService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LimitServiceImpl implements LimitService {

    private final LimitRepository limitRepository;

    @Override
    public List<Limit> findAll() {
        return limitRepository.findAll();
    }

    @Override
    public Limit findByLimit(Long limit) {
        return limitRepository.findByVolume(limit);
    }
}
