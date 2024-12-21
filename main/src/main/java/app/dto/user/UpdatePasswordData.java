package app.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordData {
    @NotBlank(message = "oldPassword field can't be empty!")
    private String oldPassword;

    @NotBlank(message = "oldPassword field can't be empty!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private String newPassword;
}
