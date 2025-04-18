package app.dto.meditation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GeneratedMeditation {
    private UUID newMeditationId;

    @JsonProperty("audio_link")
    private String audioLink;

    private String videoLink;
}
