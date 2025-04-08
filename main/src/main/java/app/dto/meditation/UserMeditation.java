package app.dto.meditation;

import app.entity.usermeditation.StatusEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserMeditation {
    private UUID id;
    private Meditation meditation;
    private List<StatusEntity> statuses;
    private double pauseTime;
}
