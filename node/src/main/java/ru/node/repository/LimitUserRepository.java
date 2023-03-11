package ru.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.node.model.LimitUser;

@Repository
public interface LimitUserRepository extends JpaRepository<LimitUser, Long> {

    @Query("SELECT lu FROM LimitUser lu " +
            "JOIN FETCH lu.limit " +
            "JOIN FETCH lu.user " +
            "WHERE lu.user.userId = :userId")
    LimitUser findByUserId(@Param("userId") Long userId);
}
