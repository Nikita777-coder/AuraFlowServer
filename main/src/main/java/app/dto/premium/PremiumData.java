package app.dto.premium;

import app.entity.payment.TransactionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PremiumData {
    private TransactionStatus transactionStatus;
    private LocalDateTime transactionTime;
    private LocalDateTime expiredTime;
}
