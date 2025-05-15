package app.entity.payment;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    WAITING_FOR_CAPTURE,
    SUCCEEDED,
    CANCELED
}
