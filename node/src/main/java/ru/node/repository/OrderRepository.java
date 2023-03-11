package ru.node.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.node.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByExchangeAndAssetAndTradeMethodAndTradeTypeAndUserName(
            @NonNull String exchange,
            @NonNull String asset,
            @NonNull String tradeMethod,
            @NonNull String tradeType,
            @NonNull String userName);

    List<Order> findAll(Specification<Order> specification);

    void deleteAllByDateIsLessThan(LocalDateTime date);
}