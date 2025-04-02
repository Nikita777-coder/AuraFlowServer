package app.dto.meditationalbum;

import app.entity.meditation.MeditationEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MeditationAlbumRequest {
    private String title;
    private String description;
    private List<MeditationEntity> meditations;
}
