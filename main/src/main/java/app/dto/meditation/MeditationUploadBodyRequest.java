package app.dto.meditation;

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
    private MultipartFile uploadVideo;
    private String title;
    private String description;
    private List<Tag> tags;
}
