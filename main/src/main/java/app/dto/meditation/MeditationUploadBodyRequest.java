package app.dto.meditation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MeditationUploadBodyRequest {
    private String sourceLink;
    private UUID parentId;

    @Null
    private MultipartFile uploadVideoPath;

    @NotBlank(message = "can't be null")
    private String title;

    private String description;
    private List<Tag> tags;
}
