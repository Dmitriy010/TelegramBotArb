package ru.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.node.model.PaymentSystem;

import java.util.List;

@Repository
public interface PaymentSystemRepository extends JpaRepository<PaymentSystem, Long> {
    @Query("SELECT ps from PaymentSystem ps WHERE ps.id IN (:listIds)")
    List<PaymentSystem> findAllByIds(@Param("listIds") List<Long> listIds);
}
