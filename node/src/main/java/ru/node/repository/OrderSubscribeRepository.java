package ru.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.node.model.OrderSubscribe;

import java.util.List;

@Repository
public interface OrderSubscribeRepository extends JpaRepository<OrderSubscribe, Long> {

    List<OrderSubscribe> findAllByUserId(Long userId);
}
