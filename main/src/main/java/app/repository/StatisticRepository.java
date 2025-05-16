package app.repository;

import app.entity.StatisticEntity;
import app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StatisticRepository extends JpaRepository<StatisticEntity, UUID> {
    List<StatisticEntity> findAllByUserAndFixedTimeBetween(UserEntity user, LocalDate from, LocalDate to);
}
