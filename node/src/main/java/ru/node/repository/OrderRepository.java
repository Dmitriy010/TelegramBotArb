package ru.node.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.node.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAll(Specification<Order> specification);

    @Transactional
    void deleteAllByExchangeAndAssetAndTradeMethodAndTradeType(String exchange, String asset, String tradeMethod, String tradeType);
}