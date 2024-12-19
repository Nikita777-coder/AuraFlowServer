package app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SignUpRequest {
    @NotBlank(message = "email can't be empty!")
    private String email;

    private String name;

    @NotBlank(message = "password can't be empty!")
    private String password;
}

