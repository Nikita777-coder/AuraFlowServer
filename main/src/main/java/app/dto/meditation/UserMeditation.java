package app.dto.meditation;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserMeditation {
    private UUID id;
    private Meditation meditation;
    private Set<Status> statuses;
    private double pauseTime;
    private String generatedMeditationLink;
    private String title;
}
