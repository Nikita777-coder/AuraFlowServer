package app.entity.payment;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    WAITING("waiting_for_capture"),
    SUCCESS("succeeded"),
    CANCELED("canceled");

    private final String name;

    TransactionStatus(String name) {
        this.name = name;
    }
}
