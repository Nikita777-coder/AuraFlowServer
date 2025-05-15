package app.repository;

import app.entity.payment.PremiumEntity;
import app.entity.payment.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PremiumRepository extends JpaRepository<PremiumEntity, UUID> {
    @Query("select r from PremiumEntity r where r.userEntity.email = :userEmail and r.transactionStatus = :status and r.expiredTime > CURRENT_TIMESTAMP ")
    List<PremiumEntity> getCurrentPremium(String userEmail, TransactionStatus status);
    List<PremiumEntity> getPremiumEntitiesByUserEntity_Email(String email);
    Optional<PremiumEntity> getPremiumEntityByIdAndUserEntity_Email(UUID id, String email);
}
