package app.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SignUpRequest {
    @NotBlank(message = "email can't be empty!")
    private String email;

    private String name = "";

    @NotBlank(message = "password can't be empty!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "password must contains 1 capital, lowercase letter; 1 digit; 1 of symbol: @$!%*?& and min length is 8")
    private String password;
}

