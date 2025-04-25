package app.dto.meditation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GeneratedMeditation {
    private String status;
    private String url;
    private String wasUsed;
}
