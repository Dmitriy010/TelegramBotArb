package ru.node.repository.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import ru.node.enums.ExchangeEnum;
import ru.node.enums.PaymentSystemEnum;
import ru.node.enums.TradeTypeEnum;
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

    private static Specification<Order> filterByAllExchange(List<String> exchangeList) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("exchange")).value(exchangeList));
    }

    private static Specification<Order> filterByTradeMethod(@NonNull String tradeMethod) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("tradeMethod"), tradeMethod));
    }

    private static Specification<Order> filterByAllPaymentSystem(List<String> tradeMethodList) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("tradeMethod")).value(tradeMethodList));
    }

    private static Specification<Order> filterByPriceBuy(@NonNull Double price) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price));
    }

    private static Specification<Order> filterByPriceSell(@NonNull Double price) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), price));
    }

    private static Specification<Order> filterByTransAmountMax(@NonNull Double transAmountMin) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("transAmountMax"), transAmountMin));
    }

    private static Specification<Order> filterByTransAmountMin(@NonNull Double transAmountMin) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("transAmountMin"), transAmountMin));
    }

    private static Specification<Order> orderByPrice(String tradeType) {
        return ((root, query, criteriaBuilder) -> {
            if (TradeTypeEnum.SELL.name().equals(tradeType)) {
                return query.orderBy(criteriaBuilder.asc(root.get("price"))).getRestriction();
            } else {
                return query.orderBy(criteriaBuilder.desc(root.get("price"))).getRestriction();
            }
        });
    }

    private static Specification<Order> filterByBestPrice(String tradeType, String asset, Double transAmountMin, List<String> exchange, List<String> tradeMethod) {
        return ((root, query, criteriaBuilder) -> {
            var subquery = query.subquery(Double.class);
            var subRoot = subquery.from(Order.class);
            if (tradeType.equals(TradeTypeEnum.SELL.name())) {
                subquery.select(criteriaBuilder.min(subRoot.get("price")));
            } else {
                subquery.select(criteriaBuilder.max(subRoot.get("price")));
            }

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(subRoot.get("asset"), asset));
            predicates.add(criteriaBuilder.equal(subRoot.get("tradeType"), tradeType));
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(subRoot.get("transAmountMax"), transAmountMin));
            predicates.add(criteriaBuilder.lessThanOrEqualTo(subRoot.get("transAmountMin"), transAmountMin));

            if (exchange.size() == 1 && !exchange.get(0).equals(ExchangeEnum.ANY.getName())) {
                predicates.add(criteriaBuilder.equal(subRoot.get("exchange"), exchange.get(0)));
            } else if (exchange.size() > 1) {
                predicates.add(criteriaBuilder.in(subRoot.get("exchange")).value(exchange));
            }

            if (tradeMethod.size() == 1 && !tradeMethod.get(0).equals(PaymentSystemEnum.ANY.getName())) {
                predicates.add(criteriaBuilder.equal(subRoot.get("tradeMethod"), tradeMethod.get(0)));
            } else if (tradeMethod.size() > 1) {
                predicates.add(criteriaBuilder.in(subRoot.get("tradeMethod")).value(tradeMethod));
            }

            subquery.where(predicates.toArray(new Predicate[]{}));

            return criteriaBuilder.equal(root.get("price"), subquery);
        });
    }

    public static Specification<Order> getFilterOrderSubscribePrice(String tradeType, String asset, Double transAmountMin, List<String> exchange, List<String> tradeMethod, Double price) {
        Specification<Order> exchangeSpec;
        Specification<Order> paymentSystemSpec;

        if (exchange.isEmpty() || exchange.get(0).equals(ExchangeEnum.ANY.getName())) {
            exchangeSpec = null;
        } else if (exchange.size() == 1) {
            exchangeSpec = filterByExchange(exchange.get(0));
        } else {
            exchangeSpec = filterByAllExchange(exchange);
        }

        if (tradeMethod.isEmpty() || tradeMethod.get(0).equals(PaymentSystemEnum.ANY.getName())) {
            paymentSystemSpec = null;
        } else if (tradeMethod.size() == 1) {
            paymentSystemSpec = filterByTradeMethod(tradeMethod.get(0));
        } else {
            paymentSystemSpec = filterByAllPaymentSystem(tradeMethod);
        }

        return Specification.
                where(filterByTradeType(tradeType))
                .and(filterByAsset(asset))
                .and(filterByTransAmountMin(transAmountMin))
                .and(filterByTransAmountMax(transAmountMin))
                .and(exchangeSpec)
                .and(paymentSystemSpec)
                .and(tradeType.equals(TradeTypeEnum.SELL.name()) ? filterByPriceSell(price) : filterByPriceBuy(price))
                .and(orderByPrice(tradeType));
    }

    public static Specification<Order> getFilterOrderCheckPrice(String tradeType, String asset, Double transAmountMin, List<String> exchange, List<String> tradeMethod) {
        Specification<Order> exchangeSpec;
        Specification<Order> paymentSystemSpec;

        if (exchange.isEmpty() || exchange.get(0).equals(ExchangeEnum.ANY.getName())) {
            exchangeSpec = null;
        } else if (exchange.size() == 1) {
            exchangeSpec = filterByExchange(exchange.get(0));
        } else {
            exchangeSpec = filterByAllExchange(exchange);
        }

        if (tradeMethod.isEmpty() || tradeMethod.get(0).equals(PaymentSystemEnum.ANY.getName())) {
            paymentSystemSpec = null;
        } else if (tradeMethod.size() == 1) {
            paymentSystemSpec = filterByTradeMethod(tradeMethod.get(0));
        } else {
            paymentSystemSpec = filterByAllPaymentSystem(tradeMethod);
        }

        return Specification.
                where(filterByTradeType(tradeType))
                .and(filterByAsset(asset))
                .and(filterByTransAmountMin(transAmountMin))
                .and(filterByTransAmountMax(transAmountMin))
                .and(exchangeSpec)
                .and(paymentSystemSpec)
                .and(filterByBestPrice(tradeType, asset, transAmountMin, exchange, tradeMethod));
    }
}
