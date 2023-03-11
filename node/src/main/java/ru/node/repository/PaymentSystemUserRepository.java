package ru.node.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.node.model.PaymentSystem;
import ru.node.model.PaymentSystemUser;
import ru.node.model.User;

import java.util.List;

@Repository
public interface PaymentSystemUserRepository extends JpaRepository<PaymentSystemUser, Long> {
    @Query("SELECT psu FROM PaymentSystemUser psu " +
            "JOIN FETCH psu.paymentSystem " +
            "WHERE psu.user.userId = :userId")
    List<PaymentSystemUser> findAllByUser(@Param("userId") Long userId);

    @Transactional
    void deleteAllByUser(User user);

    @Query("DELETE from PaymentSystemUser psu WHERE psu.user = :user AND psu.paymentSystem = :paymentSystem")
    @Modifying
    @Transactional
    void deleteByUserAndPaymentSystem(@Param("user") User user, @Param("paymentSystem") PaymentSystem paymentSystem);
}
