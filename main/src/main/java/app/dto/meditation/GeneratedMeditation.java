package app.dto.meditation;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GeneratedMeditation {
    private UUID newMeditationId;
    private String audioLink;
    private String videoLink;
}
