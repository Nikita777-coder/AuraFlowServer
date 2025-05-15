package app.dto.statistic;

import app.dto.meditation.MeditationStatData;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class Statistic {
    @Min(value = 1, message = "entranceCountPerDay can't be < 1")
    private Integer entranceCountPerDay;

    private LocalDate statisticTimeFixing;

    private List<MeditationStatData> watchedMeditationsPerDay;
}
