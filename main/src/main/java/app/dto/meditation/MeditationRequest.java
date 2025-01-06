package app.dto.meditation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MeditationRequest {
    private List<Status> statuses;
}
