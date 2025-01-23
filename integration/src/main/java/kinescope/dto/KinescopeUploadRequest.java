package kinescope.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KinescopeUploadRequest {
    private UUID projectId;
    private String title;
    private String description;
    private String url;
}
