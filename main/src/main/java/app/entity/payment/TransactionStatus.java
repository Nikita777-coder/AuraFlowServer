package app.entity.payment;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    WAITING("payment.waiting_for_capture"),
    SUCCESS("payment.succeeded"),
    CANCELED("payment.canceled");

    private final String name;

    TransactionStatus(String name) {
        this.name = name;
    }
}
