package app.dto.meditation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UploadData {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private String embedLink;
    private LocalDateTime createdAt;
}
