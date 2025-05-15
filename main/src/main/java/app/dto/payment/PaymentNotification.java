package app.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentNotification {
    private String type;
    private String event;
    private PaymentObject object;
}
