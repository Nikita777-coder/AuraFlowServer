package app.dto.meditation;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MeditationStatData {
    private UUID meditationId;
    private double averagePulse;
}
