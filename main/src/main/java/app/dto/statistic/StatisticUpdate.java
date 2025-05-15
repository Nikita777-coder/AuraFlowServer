package app.dto.statistic;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class StatisticUpdate {
    @NotNull(message = "can't be null")
    private UUID statId;

    @NotNull(message = "can't be null")
    private Statistic updData;
}
