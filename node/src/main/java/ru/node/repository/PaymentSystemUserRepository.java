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
            "WHERE psu.user = :user")
    List<PaymentSystemUser> findAllByUser(@Param("user") User user);

    @Transactional
    void deleteAllByUser(User user);

    @Query("DELETE from PaymentSystemUser exs WHERE exs.user = :user AND exs.paymentSystem IN (:paymentSystemList)")
    @Modifying
    @Transactional
    void deleteAllByPaymentSystemsAndUser(@Param("user") User user, @Param("paymentSystemList") List<PaymentSystem> paymentSystemList);
}
