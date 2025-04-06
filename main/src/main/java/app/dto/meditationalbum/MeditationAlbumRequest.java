package app.dto.meditationalbum;

import app.entity.meditation.MeditationEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MeditationAlbumRequest {
    @NotBlank(message = "must not be null and empty")
    private String title;
    private String description;
    private List<UUID> meditations;
}
