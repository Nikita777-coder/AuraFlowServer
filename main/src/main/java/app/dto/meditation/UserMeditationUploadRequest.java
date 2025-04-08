package app.dto.meditation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserMeditationUploadRequest {
    @NotBlank
    private UUID id;
}
