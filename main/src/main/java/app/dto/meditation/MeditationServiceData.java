package app.dto.meditation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MeditationServiceData {
    private UUID id;
    private String title;
    private String description;
    private Double duration;
    private String status;

    @JsonProperty("embed_link")
    private String embedLink;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
