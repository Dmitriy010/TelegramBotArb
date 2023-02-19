package ru.node.repository.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import ru.node.model.BinanceOrder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BinanceOrderSpecification {

    private static Specification<BinanceOrder> filterByAsset(@NonNull String asset) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("asset"), asset));
    }

    private static Specification<BinanceOrder> filterByFiat(@NonNull String fiat) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("fiat"), fiat));
    }

    private static Specification<BinanceOrder> filterByTradeType(@NonNull String tradeType) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("tradeType"), tradeType));
    }

    private static Specification<BinanceOrder> filterByUserName(@NonNull String userName) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userName"), userName));
    }

    public static Specification<BinanceOrder> getFilter(String asset, String fiat, String tradeType, String userName) {
        return Specification.
                where(filterByAsset(asset))
                .and(filterByFiat(fiat))
                .and(filterByTradeType(tradeType))
                .and(filterByUserName(userName));
    }
}
