package app.dto.meditation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModelMeditationRequest {
    String text;
    List<Tag> tags;
}
