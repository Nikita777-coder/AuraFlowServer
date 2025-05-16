package app.entity.payment;

import app.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "premiums")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PremiumEntity {
    @Id
    private UUID id;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST})
    private UserEntity userEntity;

    @Column
    private String paymentToken;

    @Column(name = "transaction_status")
    private TransactionStatus transactionStatus;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "expired_time")
    private LocalDateTime expiredTime;

    @Column(name = "transaction_id")
    private UUID transactionId;
}
