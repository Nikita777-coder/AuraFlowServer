package app.dto.statistic;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StatisticUpdate {
    @NotNull(message = "can't be null")
    private UUID statId;

    @NotNull(message = "can't be null")
    private Statistic updData;
}
