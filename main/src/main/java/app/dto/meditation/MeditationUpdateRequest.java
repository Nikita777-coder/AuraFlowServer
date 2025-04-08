package app.dto.meditation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MeditationUpdateRequest {
    @NotBlank
    private UUID id;
    private String author;
    private String title;
    private String description;
    private String videoLink;
    private LocalDateTime updatedAt;
    private List<Tag> tags;
}
