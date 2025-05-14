package app.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PaymentObject {
    private UUID id;
    private String status;
    private boolean paid;

    @JsonProperty("amount.value")
    private BigDecimal amountValue;

    @JsonProperty("amount.currency")
    private String currency;

    @JsonProperty("payment_method.id")
    private UUID paymentId;

    private boolean refundable;
    private boolean test;
}
