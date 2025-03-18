package app.dto.meditation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModelMeditationRequest {
    private String text;
    private String musicTitle;
    private int timeDuration;
}
