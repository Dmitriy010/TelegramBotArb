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
            "WHERE exchu.user.userId = :userId")
    List<ExchangeUser> findAllByUser(@Param("userId") Long userId);

    @Transactional
    void deleteAllByUser(User user);

    @Query("DELETE from ExchangeUser exs WHERE exs.user = :user AND exs.exchange = :exchange")
    @Modifying
    @Transactional
    void deleteByUserAndExchange(@Param("user") User user, @Param("exchange") Exchange exchange);
}
