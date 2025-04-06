package app.dto.meditationalbum;

import app.entity.meditation.MeditationEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MeditationAlbum {
    private UUID id;
    private String title;
    private String description;
//    private List<UserMeditationEntity> meditations;
}
