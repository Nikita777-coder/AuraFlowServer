package app.dto.meditation;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserMeditationUploadRequest {
    private UUID id;
    private String videoUrl;
    private String title;
}
