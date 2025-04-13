package app.dto.meditation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserMeditationUpdateRequest {
    @NotBlank
    private UUID id;
    @Min(value = 0, message = "must be equal or greater then zero")
    private double pauseTime;
    private List<Status> statuses;
}
