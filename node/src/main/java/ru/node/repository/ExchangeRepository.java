package ru.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.node.model.Exchange;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    @Query("SELECT ex from Exchange ex WHERE ex.id IN (:listIds)")
    List<Exchange> findAllByIds(@Param("listIds") List<Long> listIds);
}
