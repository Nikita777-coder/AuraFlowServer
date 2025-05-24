package app.dto.statistic;

import app.dto.meditation.MeditationStatData;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Statistic {
    @NotNull(message = "can't be null")
    private String userEmail;

    @Min(value = 1, message = "entranceCountPerDay can't be < 1")
    private Integer entranceCountPerDay;

    private LocalDate statisticTimeFixing = LocalDate.now();

    private List<MeditationStatData> watchedMeditationsPerDay;
}
