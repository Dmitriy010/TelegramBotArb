package ru.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.node.model.Limit;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {

    Limit findByVolume(Long volume);
}
