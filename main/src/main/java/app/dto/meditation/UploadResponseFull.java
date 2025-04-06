package app.dto.meditation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadResponseFull {
    private UploadResponse uploadResponse;
    private boolean wasUploadFromUrl;
}
