package app.dto.premium;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PremiumIntegrationServiceResponse {
    private UUID id;

    private Confirmation confirmation;

    private boolean test;
}
