package ru.node.repository.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import ru.node.enums.Exchange;
import ru.node.enums.PaymentSystem;
import ru.node.enums.TradeType;
import ru.node.model.Order;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSpecification {

    private static Specification<Order> filterByAsset(@NonNull String asset) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("asset"), asset));
    }

    private static Specification<Order> filterByTradeType(@NonNull String tradeType) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("tradeType"), tradeType));
    }

    private static Specification<Order> filterByExchange(@NonNull String exchange) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("exchange"), exchange));
    }

    private static Specification<Order> filterByTradeMethod(@NonNull String tradeMethod) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("tradeMethod"), tradeMethod));
    }

    private static Specification<Order> filterByPriceBuy(@NonNull Double price) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price));
    }

    private static Specification<Order> filterByPriceSell(@NonNull Double price) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), price));
    }

    private static Specification<Order> orderByPrice(String tradeType) {
        return ((root, query, criteriaBuilder) -> {
            if (TradeType.SELL.name().equals(tradeType)) {
                return query.orderBy(criteriaBuilder.asc(root.get("price"))).getRestriction();
            } else {
                return query.orderBy(criteriaBuilder.desc(root.get("price"))).getRestriction();
            }
        });
    }

    private static Specification<Order> filterByBestPrice(String tradeType, String asset, String exchange, String tradeMethod) {
        return ((root, query, criteriaBuilder) -> {
            var subquery = query.subquery(Double.class);
            var subRoot = subquery.from(Order.class);
            if (tradeType.equals(TradeType.SELL.name())) {
                subquery.select(criteriaBuilder.min(subRoot.get("price")));
            } else {
                subquery.select(criteriaBuilder.max(subRoot.get("price")));
            }

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(subRoot.get("asset"), asset));
            predicates.add(criteriaBuilder.equal(subRoot.get("tradeType"), tradeType));

            if (!Exchange.ANY.getName().equals(exchange)) {
                predicates.add(criteriaBuilder.equal(subRoot.get("exchange"), exchange));
            }

            if (!PaymentSystem.ANY.getName().equals(tradeMethod)) {
                predicates.add(criteriaBuilder.equal(subRoot.get("tradeMethod"), tradeMethod));
            }

            subquery.where(predicates.toArray(new Predicate[]{}));

            return criteriaBuilder.equal(root.get("price"), subquery);
        });
    }

    public static Specification<Order> getFilterOrderSubscribePrice(String tradeType, String asset, String exchange, String tradeMethod, Double price) {
        return Specification.
                where(filterByTradeType(tradeType))
                .and(filterByAsset(asset))
                .and(exchange.equals(Exchange.ANY.getName()) ? null : filterByExchange(exchange))
                .and(tradeMethod.equals(PaymentSystem.ANY.getName()) ? null : filterByTradeMethod(tradeMethod))
                .and(tradeType.equals(TradeType.SELL.name()) ? filterByPriceSell(price) : filterByPriceBuy(price))
                .and(orderByPrice(tradeType));
    }

    public static Specification<Order> getFilterOrderCheckPrice(String tradeType, String asset, String exchange, String tradeMethod) {
        return Specification.
                where(filterByTradeType(tradeType))
                .and(filterByAsset(asset))
                .and(exchange.equals(Exchange.ANY.getName()) ? null : filterByExchange(exchange))
                .and(tradeMethod.equals(PaymentSystem.ANY.getName()) ? null : filterByTradeMethod(tradeMethod))
                .and(filterByBestPrice(tradeType, asset, exchange, tradeMethod));
    }
}
