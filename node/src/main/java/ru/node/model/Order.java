package ru.node.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tradeType;

    private String asset;

    private String fiat;

    private Double price;

    private String transAmount;

    private String tradeMethod;

    private Double tradableQuantity;

    private String userName;

    private Long successOrders;

    private Double successOrdersPercent;

    private String exchange;

    private LocalDateTime date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order that = (Order) o;
        return Objects.equals(tradeType, that.tradeType) && Objects.equals(asset, that.asset) && Objects.equals(fiat, that.fiat) && Objects.equals(price, that.price) && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeType, asset, fiat, price, userName);
    }
}