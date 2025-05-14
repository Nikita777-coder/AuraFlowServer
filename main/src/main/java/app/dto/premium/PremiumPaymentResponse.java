package app.dto.premium;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PremiumPaymentResponse {
    private UUID id;
    private String payLink;
}
