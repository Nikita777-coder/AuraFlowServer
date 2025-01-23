package system.kinescope.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class KinescopeData {
    private UUID id;
    private UUID parentId;
    private String title;
    private String description;
    private String status;
    private String playLink;
    private String embedLink;
    private LocalDateTime createdAt;
}
