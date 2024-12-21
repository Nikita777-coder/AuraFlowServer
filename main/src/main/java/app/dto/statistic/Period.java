package app.dto.statistic;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Period {
    @NotBlank(message = "from date can't be empty!")
    private LocalDate from;

    @NotBlank(message = "to date can't be empty!")
    private LocalDate to;
}
