package app.dto.meditationalbum;

import app.dto.meditation.UserMeditation;
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
    private List<UserMeditation> meditations;
    private UUID ownerId;
}
