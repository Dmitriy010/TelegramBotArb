package ru.node.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.node.model.Exchange;
import ru.node.model.ExchangeUser;
import ru.node.model.User;

import java.util.List;

@Repository
public interface ExchangeUserRepository extends JpaRepository<ExchangeUser, Long> {
    @Query("SELECT exchu FROM ExchangeUser exchu " +
            "JOIN FETCH exchu.exchange " +
            "WHERE exchu.user = :user")
    List<ExchangeUser> findAllByUser(@Param("user") User user);

    @Transactional
    void deleteAllByUser(User user);

    @Query("DELETE from ExchangeUser exs WHERE exs.user = :user AND exs.exchange IN (:exchangeList)")
    @Modifying
    @Transactional
    void deleteAllByExchangesAndUser(@Param("user") User user, @Param("exchangeList") List<Exchange> exchangeList);
}
