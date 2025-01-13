package app.repository;

import app.entity.payment.PremiumEntity;
import app.entity.payment.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PremiumRepository extends JpaRepository<PremiumEntity, UUID> {
    @Query("select r from PremiumEntity r where r.userId = :userId and r.transactionStatus = :status and r.expiredTime > CURRENT_TIMESTAMP ")
    List<PremiumEntity> getCurrentPremium(UUID userId, TransactionStatus status);

    List<PremiumEntity> getPremiumEntitiesByUserId(UUID userId);
}
