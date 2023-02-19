package ru.node.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.node.model.BinanceOrder;

import java.util.List;
import java.util.Optional;

@Repository
public interface BinanceOrderRepository extends JpaRepository<BinanceOrder, Long> {

    Optional<BinanceOrder> findByAssetAndFiatAndTradeTypeAndTradeMethod(String asset, String fiat, String tradeType, String tradeMethod);

    Optional<BinanceOrder> findByTradeMethodAndTradeType(String tradeMethod, String tradeType);

    List<BinanceOrder> findAll(Specification<BinanceOrder> specification);
}