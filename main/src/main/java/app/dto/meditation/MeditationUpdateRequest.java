package app.dto.meditation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MeditationUpdateRequest {
    @NotNull
    private UUID id;
    private String author;
    private String title;
    private String description;
    private String videoLink;
    private LocalDateTime updatedAt;
    private List<Tag> tags;
}
